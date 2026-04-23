package jarden.codswallop;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jarden.quiz.EndOfQuestionsException;
import jarden.tcp.TcpHostServer;
import jarden.tcp.TcpPlayerClient;

import static jarden.codswallop.Constants.ALL_ANSWERS;
import static jarden.codswallop.Constants.ANSWER;
import static jarden.codswallop.Constants.CORRECT;
import static jarden.codswallop.Constants.GAME_PREFS;
import static jarden.codswallop.Constants.NAMED_ANSWERS;
import static jarden.codswallop.Constants.QUESTION;
import static jarden.codswallop.Constants.QUESTION_SEQUENCE_KEY;
import static jarden.codswallop.Constants.VOTE;

    /**
    Network event (host found) -> TcpService.onHostFound() -> connect()
    Network event (connected to host) -> TcpService.connected() ->
        update ViewModel (state only) -> Activity observes -> updates UI
     */
public class TcpService extends Service implements TcpHostServer.ServerListener,
        TcpPlayerClient.ClientListener, QuestionManager.QuestionListener {
    private static final String TAG = "TcpService";
    public static final String CHANNEL_ID = "codswallop_network";
    private SharedPreferences preferences;
    private final IBinder binder = new LocalBinder();
    private TcpHostServer tcpHostServer;
    private TcpPlayerClient tcpPlayerClient = new TcpPlayerClient();
    private WifiManager.WifiLock wifiLock;
    private boolean netWorkRunning = true;
    private String hostIpAddress;
    private GameViewModel gameViewModel;
    private String thisPlayerName;
    private String currentQuestion;
    private final List<String> shuffledNameList = new ArrayList<>();
    private QuestionManager.QuestionAnswer currentQA;
    private QuestionManager questionManager;
    private Map<String, Player> players;
    private Map<String, Player> leftPlayers;
    private String lastJoinedPlayerName;
    private boolean isPlayerLeaving = false;
    private int questionSequence = 21;
    private boolean gameEnding = false;
    private boolean iChoseToLeave = false;
    private boolean isHost;
    @Override // Service
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
        preferences = getSharedPreferences(GAME_PREFS, Context.MODE_PRIVATE);
    }
    @Override // Service
    public IBinder onBind(Intent intent) {
        return binder;
    }
    @Override  // Service
    public void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDestroy()");
        }
        stopNetworking();
        releaseWifiLock();
        super.onDestroy();
    }
    public void onPlayerSignedIn(String playerName, boolean host) {
        thisPlayerName = playerName;
        if (host) {
            startHost();
        }
        WifiManager wifi =
                (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);
        listenForHostBroadcast(wifi);
    }
    //================================================
    // code to implement TcpHostServer.ServerListener
    //================================================
    @Override  // TcpHostServer.ServerListener
    // i.e. message sent from player to host
    public void onMessageToServer(String playerName, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "from player: " + playerName + " message: " + message);
        }
        Player currentPlayer = players.get(playerName);
        if (message.startsWith(ANSWER)) {
            String answer = message.split("\\|", 3)[2];
            currentPlayer.setAnswer(answer);
            checkForAllAnswers();
        } else if (message.startsWith(VOTE)) {
            String index = message.split("\\|", 3)[2];
            int indexOfVotedItem = Integer.parseInt(index);
            String nameVotedFor = shuffledNameList.get(indexOfVotedItem);
            currentPlayer.setNameVotedFor(nameVotedFor);
            if (nameVotedFor.equals(CORRECT)) {
                currentPlayer.incrementScore();
            } else {
                try {
                    if (!nameVotedFor.equals(playerName)) {
                        players.get(nameVotedFor).incrementScore();
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, nameVotedFor + " no longer connected");
                }
            }
            checkForAllVotes();
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "unrecognised message received by host: " + message);
            }
        }
    }
    private void checkForAllAnswers() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "checkForAllAnswers");
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            if (getAnswersCt() >= (players.size())) {
                sendToAll(getAllAnswersMessage());
                waitingForVotes();
            } else {
                waitingForAnswers();
            }
        });
    }
    private void checkForAllVotes() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "checkForAllVotes");
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            if (getVotesCt() >= (players.size())) {
                sendToAll(getNamedAnswersMessage());
                gameViewModel.setHostStateLiveData(Constants.HostState.READY_FOR_NEXT_QUESTION);
            } else {
                waitingForVotes();
            }
        });
    }    public void releaseWifiLock () {
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
            hostIpAddress = getLocalIpAddress();
        }
        if (hostIpAddress != null) {
            tcpHostServer.sendMultipleHostBroadcasts(hostIpAddress, count);
            gameViewModel.setHostBroadcastSentLiveData(true);
        } else {
            Log.e(TAG, "Could not obtain local IP address");
            Toast.makeText(this, "Could not obtain local IP address", Toast.LENGTH_SHORT).show();
        }
    }

    private String getLocalIpAddress() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return null;

        // Try active network first
        Network activeNetwork = cm.getActiveNetwork();
        if (activeNetwork != null) {
            String ip = getIpFromNetwork(cm, activeNetwork);
            if (ip != null) return ip;
        }

        // Otherwise, iterate over all networks to find a Wi-Fi one
        for (Network network : cm.getAllNetworks()) {
            NetworkCapabilities nc = cm.getNetworkCapabilities(network);
            if (nc != null && nc.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                String ip = getIpFromNetwork(cm, network);
                if (ip != null) return ip;
            }
        }
        return null;
    }

    private String getIpFromNetwork(ConnectivityManager cm, Network network) {
        LinkProperties lp = cm.getLinkProperties(network);
        if (lp != null) {
            for (LinkAddress la : lp.getLinkAddresses()) {
                InetAddress address = la.getAddress();
                if (address instanceof Inet4Address && !address.isLoopbackAddress()) {
                    return address.getHostAddress();
                }
            }
        }
        return null;
    }

    private void waitingForAnswers() {
        gameViewModel.setMissingAnswerCtLiveData(getNotAnsweredCount());
        gameViewModel.setHostStateLiveData(Constants.HostState.AWAITING_CT_ANSWERS);
    }
    private void waitingForVotes() {
        gameViewModel.setMissingVoteCtLiveData(getNotVotedCount());
        gameViewModel.setHostStateLiveData(Constants.HostState.AWAITING_CT_VOTES);
    }

    //================================================
    // code to implement TcpHostServer.ServerListener
    //================================================
    @Override // TcpHostServer.ServerListener
    public void onPlayerConnected(String name) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPlayerConnected(" + name + ')');
        }
        if (players.containsKey(name)) {
            Log.d(TAG, "Player name already used: " + name);
            gameViewModel.setHostStateLiveData(Constants.HostState.DUPLICATE_PLAYER_NAME);
        } else {
            if (leftPlayers.containsKey(name)) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "player " + name + " re-connecting");
                }
                Player player = leftPlayers.get(name);
                players.put(name, player);
                leftPlayers.remove(name);
            } else {
                Player player = new Player(name);
                players.put(name, player);
            }
            lastJoinedPlayerName = name;
            new Handler(Looper.getMainLooper()).post(() -> {
                gameViewModel.setPlayerJoiningEvent(
                        new GameViewModel.PlayerJoinedData(lastJoinedPlayerName, players.size()));
            });
        }
    }
    @Override // TcpHostServer.ServerListener
    public void onPlayerDisconnected(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPlayerDisconnected(" + playerName + ")");
        }
        if (!gameEnding && playerName != null) {
            if (players.containsKey(playerName)) { // check not already removed
                leftPlayers.put(playerName, players.get(playerName));
                players.remove(playerName);
                Constants.HostState hostState = gameViewModel.getHostStateLiveData().getValue();
                if (hostState == Constants.HostState.AWAITING_CT_ANSWERS) {
                    checkForAllAnswers();
                } else if (hostState == Constants.HostState.AWAITING_CT_VOTES) {
                    checkForAllVotes();
                }
            }
        }
    }
    public void sendNextQuestion() {
        sendToAll(getNextQuestion());
        waitingForAnswers();
    }
    public String getNextQuestion() {
        try {
            currentQA = questionManager.getQuestionAnswer(questionSequence);
        } catch (EndOfQuestionsException e) {
            try {
                questionSequence = 0;
                currentQA = questionManager.getQuestionAnswer(questionSequence);
            } catch (EndOfQuestionsException e2) {
                throw new RuntimeException(e2);
            }
        }
        String nextQuestion = QUESTION + '|' + questionSequence + '|' + currentQA.type +
                '|' + currentQA.question;
        ++questionSequence;
        preferences.edit()
                .putInt(QUESTION_SEQUENCE_KEY, questionSequence)
                .apply();
        for (Player player: players.values()) {
            player.reset();
        }
        return nextQuestion;
    }
    @Override // TcpHostServer.ServerListener
    public void onServerStarted() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onServerStarted()");
        }
    }
    private String getAllAnswersMessage() {
        shuffledNameList.clear();
        shuffledNameList.add(CORRECT);
        shuffledNameList.addAll(players.keySet());
        Collections.shuffle(shuffledNameList);
        StringBuilder buffer = new StringBuilder(ALL_ANSWERS + '|' + questionSequence);
        for (String name : shuffledNameList) {
            if (name.equals(CORRECT)) {
                buffer.append('|' + currentQA.answer);
            } else {
                buffer.append('|' + players.get(name).getAnswer());
            }
        }
        return buffer.toString();
    }
    public String getNamedAnswersMessage() {
        List<Player> playerList = new ArrayList<>(players.values());
        playerList.sort((p1, p2) ->
                Integer.compare(p2.getScore(), p1.getScore()));
        StringBuilder buffer = new StringBuilder(NAMED_ANSWERS + '|' + questionSequence);
        buffer.append('|' + CORRECT + '|' + currentQA.answer);
        if (currentQA.comment != null) {
            buffer.append(". " + currentQA.comment);
        }
        for (Player player: playerList) {
            buffer.append('|' + player.getName() + '|' + player.getNameVotedFor() +
                    '|' + player.getScore() + '|' + player.getAnswer());
        }
        return buffer.toString();
    }
    public int getNotAnsweredCount() {
        return players.size() - getAnswersCt();
    }
    public int getNotVotedCount() {
        return (players.size() - getVotesCt());
    }
    public int getPlayersCount() {
        return (players.size());
    }
    private int getVotesCt() {
        int votesCt = 0;
        for (Player playerN : players.values()) {
            if (playerN.getNameVotedFor() != null) {
                votesCt++;
            }
        }
        return votesCt;
    }
    private int getAnswersCt() {
        int answersCt = 0;
        for (Player playerN : players.values()) {
            if (playerN.getAnswer() != null) {
                answersCt++;
            }
        }
        return answersCt;
    }
    public void startHost() {
        players = new ConcurrentHashMap<>();
        leftPlayers = new ConcurrentHashMap<>();
        questionManager = new QuestionManager(getApplication().getResources(), this);
        isHost = true;
        questionSequence = preferences.getInt(QUESTION_SEQUENCE_KEY, 0);
    }
    public boolean getIsHost() {
        return isHost;
    }
    public void setQuestionSequence(int questionSequence) {
        this.questionSequence = questionSequence;
    }
    public String getPlayerName() {
        return thisPlayerName;
    }
    @Override // QuestionManager.QuestionListener
    public void onQuestionsLoaded(int questionCount) {
        new Handler(Looper.getMainLooper()).post(() -> {
            Toast.makeText(this, questionCount + " questions loaded", Toast.LENGTH_LONG).show();
        });
    }
    @Override // QuestionManager.QuestionListener
    public void onError(String message) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
    }

    //************************************************
    // code to implement TcpHostServer.ServerListener
    //************************************************
    @Override // TcpPlayerClient.ClientListener
    public void onHostFound(String hostIp, int port) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onHostFound(" + hostIp + ", " + port + ')');
        }
        if (!isConnectedToHost()) {
            connect(hostIp, thisPlayerName, this);
        }
    }
    @Override // TcpPlayerClient.ClientListener
    public void onError(Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, e.toString());
        }
        if (!isPlayerLeaving) {
            new Handler(Looper.getMainLooper()).post(() -> {
                onPlayerLeaving();
                gameViewModel.setExceptionLiveData(e);
            });
        }
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
                endGame();
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
    public void startHosting() {
        tcpHostServer = new TcpHostServer(this);
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
    public void onPlayerLeaving() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPlayerLeaving(); isPlayerLeaving=" + isPlayerLeaving);
        }
        if (!isPlayerLeaving) {
            isPlayerLeaving = true;
            iChoseToLeave = true;
            if (isHost) {
                this.gameEnding = true;
                sendToAll(Constants.Protocol.END_GAME.name());
            } else {
                endGame();
            }
        }
    }
    public void endGame() {
        isPlayerLeaving = true;
        int messageId;
        if (iChoseToLeave) {
            if (isHost) {
                messageId = R.string.youEndedGame;
            } else {
                messageId = R.string.playerLeft;
            }
        } else {
            messageId = R.string.endedByHost;
        }
        gameViewModel.setGameEndedEvent(messageId);
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
            stopSelf();
            netWorkRunning = false;
        }
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
