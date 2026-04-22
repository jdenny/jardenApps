package jarden.codswallop;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.jardenconsulting.jardenlib.BuildConfig;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import androidx.core.app.NotificationCompat;
import jarden.tcp.TcpHostServer;
import jarden.tcp.TcpPlayerClient;

import static jarden.codswallop.Constants.ALL_ANSWERS;
import static jarden.codswallop.Constants.CORRECT;
import static jarden.codswallop.Constants.NAMED_ANSWERS;
import static jarden.codswallop.Constants.QUESTION;

public class TcpService extends Service implements TcpPlayerClient.ClientListener {
    private static final String TAG = "TcpService";
    public static final String CHANNEL_ID = "codswallop_network";
    private final IBinder binder = new LocalBinder();
    private TcpHostServer tcpHostServer;
    private TcpPlayerClient tcpPlayerClient = new TcpPlayerClient();
    private boolean isForeground = false;
    private WifiManager.WifiLock wifiLock;
    private boolean netWorkRunning = true;
    private String hostIpAddress;
    private GameViewModel gameViewModel;
    private String thisPlayerName;
    private String currentQuestion;

    @Override
    public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreate()");
        }
        createNotificationChannel();
        WifiManager wifiManager =
                (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiLock = wifiManager.createWifiLock(
                WifiManager.WIFI_MODE_FULL_HIGH_PERF,
                "Codswallop:WifiLock");
        wifiLock.acquire();
    }
    @Override
    public IBinder onBind(Intent intent) {
        if (!isForeground) {
            startForegroundServiceNotification();
            isForeground = true;
        }
        return binder;
    }
    @Override
    public void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDestroy()");
        }
        stopNetworking();
        releaseWifiLock();
        super.onDestroy();
    }
    public void releaseWifiLock () {
        if (wifiLock != null && wifiLock.isHeld()) {
            wifiLock.release();
        }
    }
    public void sendAnswer(int questionSequence, String answer) {
        tcpPlayerClient.sendAnswer(questionSequence, answer);
    }
    public void sendVote(int questionSequence, int position) {
        tcpPlayerClient.sendVote(questionSequence, position);
    }
    public void listenForHostBroadcast(WifiManager wifi) {
        tcpPlayerClient.listenForHostBroadcast(wifi, this);
    }
    public void sendToAll(String message) {
        tcpHostServer.sendToAll(message);
    }
    public void sendToPlayer(String playerName, String message) {
        tcpHostServer.sendToPlayer(playerName, message);
    }
    public void sendMultipleHostBroadcasts(int count) {
        if (hostIpAddress == null) {
            WifiManager wifi =
                    (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiInfo info = wifi.getConnectionInfo();
            int ipInt = info.getIpAddress();
            hostIpAddress = String.format(
                    "%d.%d.%d.%d",
                    (ipInt & 0xff),
                    (ipInt >> 8 & 0xff),
                    (ipInt >> 16 & 0xff),
                    (ipInt >> 24 & 0xff));
        }
        tcpHostServer.sendMultipleHostBroadcasts(hostIpAddress, count);
    }

    /*
    Network event (host found) -> TcpService.onHostFound() -> connect()
    Network event (connected to host) -> TcpService.connected() ->
        update ViewModel (state only) -> Activity observes -> updates UI
     */
    @Override // TcpPlayerClient.ClientListener
    public void onHostFound(String hostIp, int port) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onHostFound(" + hostIp + ", " + port + ')');
        }
        this.thisPlayerName = gameViewModel.getPlayerName();
        if (!isConnectedToHost()) {
            connect(hostIp, thisPlayerName, this);
        }
    }
    @Override // TcpPlayerClient.ClientListener
    public void onError(Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, e.toString());
        }
        /*!!??
        if (!isPlayerLeaving) {
            new Handler(Looper.getMainLooper()).post(() -> {
                onPlayerLeaving();
                exceptionLiveData.setValue(e);
            });
        }
         */
    }
    public void attachViewModel(GameViewModel gameViewModel) {
        this.gameViewModel = gameViewModel;
    }
    public void detachViewModel() {
        this.gameViewModel = null;
    }
    @Override // TcpPLayerClient.ClientListener
    public void onConnected() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Now connected to the game host");
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            gameViewModel.setPlayerStateLiveData(
                    Constants.PlayerState.AWAITING_FIRST_QUESTION);
        });
    }
    @Override
    public void onDisconnected() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Now disconnected from the game server");
        }
    }
    @Override
    public void onMessageToClient(String message) {
        if (jarden.codswallop.BuildConfig.DEBUG) {
            Log.d(TAG, "from host to player: " + thisPlayerName + " onMessageToClient(" + message + ")");
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            if (message.startsWith(ALL_ANSWERS)) {
                showAnswers(message);
                gameViewModel.setPlayerStateLiveData(Constants.PlayerState.SUPPLY_VOTE);
            } else if (message.startsWith(NAMED_ANSWERS)) {
                showNamedAnswers(message);
                gameViewModel.setPlayerStateLiveData(Constants.PlayerState.AWAITING_NEXT_QUESTION);
            } else if (message.startsWith(QUESTION)) {
                String[] tqa = message.split("\\|", 4);
                currentQuestion = tqa[1] + ". " + tqa[2] + ": " + tqa[3];
                gameViewModel.setCurrentFragmentTagLiveData(QUESTION);
                gameViewModel.setQuestionLiveData(currentQuestion);
                gameViewModel.setAwaitingAnswerLiveData(true);
                gameViewModel.setPlayerStateLiveData(Constants.PlayerState.SUPPLY_ANSWER);
            } else if (message.startsWith(Constants.Protocol.END_GAME.name())) {
                gameViewModel.endGame();
            } else {
                if (jarden.codswallop.BuildConfig.DEBUG) {
                    Log.d(TAG, "unrecognised message received by player: " + message);
                }
            }
        });
    }
    private void showAnswers(String message) {
        // ALL_ANSWERS|2|a pig trough|dollop|
        int index = message.indexOf('|');
        int indexOfFirstAnswer = message.indexOf('|', index + 1) + 1;
        String[] answers = message.substring(indexOfFirstAnswer).split("\\|");
        List<String> answersList = Arrays.asList(answers);
        gameViewModel.setCurrentFragmentTagLiveData(ALL_ANSWERS);
        gameViewModel.setAnswersLiveData(new AllAnswers(currentQuestion, answersList, false));
    }
    private void showNamedAnswers(String message) {
        String[] tokens = message.split("\\|");
        List<String> answersList = new ArrayList<>();
        List<Integer> linesVotedForMe = new ArrayList<>();
        answersList.add(tokens[2] + ": " + tokens[3]);
        int tokenIndex = 4;
        boolean isCorrect = false;
        String currentPlayerName;
        String nameVotedFor;
        while ((tokenIndex + 3) < tokens.length) {
            currentPlayerName = tokens[tokenIndex];
            nameVotedFor = tokens[tokenIndex + 1];
            if (currentPlayerName.equals(thisPlayerName)) {
                isCorrect = CORRECT.equals(nameVotedFor);
            }
            answersList.add(currentPlayerName + " (" +
                    tokens[tokenIndex + 2] + "): " + tokens[tokenIndex + 3]);
            if (nameVotedFor.equals(thisPlayerName)) {
                linesVotedForMe.add(answersList.size() - 1);
            }
            tokenIndex += 4;
        }
        gameViewModel.setCurrentFragmentTagLiveData(ALL_ANSWERS);
        gameViewModel.setAnswersLiveData(new AllAnswers(currentQuestion, answersList, true, isCorrect,
                linesVotedForMe));
    }
    public class LocalBinder extends Binder {
        public TcpService getService() {
            return TcpService.this;
        }
    }
    public void startHosting(TcpHostServer.ServerListener serverListener) {
        tcpHostServer = new TcpHostServer(serverListener);
        tcpHostServer.start();
    }
    public void connect(String hostIp, String playerName,
                        TcpPlayerClient.ClientListener listener) {
        tcpPlayerClient.connect(hostIp, TcpHostServer.TCP_PORT,
                playerName, listener);
    }
    public boolean isConnectedToHost() {
        return tcpPlayerClient.isConnectedToHost();
    }

    // =========================
    // STOP NETWORKING
    // =========================

    public void stopNetworking() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "stopNetworking(); netWorkRunning=" + netWorkRunning);
        }
        if (netWorkRunning) {
            if (tcpPlayerClient != null) {
                tcpPlayerClient.disconnect();
                tcpPlayerClient = null;
            }
            if (tcpHostServer != null) {
                tcpHostServer.stop();
                tcpHostServer = null;
            }
            releaseWifiLock();
            stopForeground(true);
            stopSelf();
            netWorkRunning = false;
        }
    }

    // =========================
    // FOREGROUND NOTIFICATION
    // =========================

    private void startForegroundServiceNotification() {

        Notification notification =
                new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Codswallop")
                        .setContentText("Game connection active")
                      //??  .setSmallIcon(R.drawable.ic_launcher_foreground)
                        .setOngoing(true)
                        .build();
        startForeground(1, notification);
    }

    // =========================
    // CHANNEL (API 26+ only)
    // =========================

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel =
                    new NotificationChannel(
                            CHANNEL_ID,
                            "Codswallop Network",
                            NotificationManager.IMPORTANCE_LOW);

            NotificationManager manager =
                    getSystemService(NotificationManager.class);

            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }
}