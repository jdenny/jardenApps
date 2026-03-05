package jarden.tcp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class TcpService extends Service {

    public static final String CHANNEL_ID = "codswallop_network";
    private final IBinder binder = new LocalBinder();

    private TcpControllerServer tcpControllerServer;
    private TcpPlayerClient tcpPlayerClient = new TcpPlayerClient();

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForegroundServiceNotification();
    }
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void sendAnswer(int questionSequence, String answer) {
        tcpPlayerClient.sendAnswer(questionSequence, answer);
    }
    public void sendVote(int questionSequence, int position) {
        tcpPlayerClient.sendVote(questionSequence, position);
    }
    public void listenForHostBroadcast(WifiManager wifi, TcpPlayerClient.Listener listener) {
        tcpPlayerClient.listenForHostBroadcast(wifi, listener);
    }
    public void sendToAll(String namedAnswersMessage) {
        tcpControllerServer.sendToAll((namedAnswersMessage));
    }
    public void sendMultipleHostBroadcasts(Context context, int count) {
        tcpControllerServer.sendMultipleHostBroadcasts(context, count);
    }
    public class LocalBinder extends Binder {
        public TcpService getService() {
            return TcpService.this;
        }
    }
    public void startHosting(String playerName, TcpPlayerClient.Listener clientListener,
                             TcpControllerServer.ServerListener serverListener) {
        tcpControllerServer = new TcpControllerServer(serverListener);
        tcpControllerServer.start();
        /*!!
        tcpPlayerClient.connect("127.0.0.1",
                TcpControllerServer.TCP_PORT,
                playerName,
                clientListener);
         */
    }
    public void connect(String hostIp,
                         String playerName,
                         TcpPlayerClient.Listener listener) {
        tcpPlayerClient.connect(hostIp,
                TcpControllerServer.TCP_PORT,
                playerName,
                listener);
    }

    // =========================
    // STOP NETWORKING
    // =========================

    public void stopNetworking() {

        if (tcpPlayerClient != null) {
            tcpPlayerClient.disconnect();
            tcpPlayerClient = null;
        }

        if (tcpControllerServer != null) {
            tcpControllerServer.stop();
            tcpControllerServer = null;
        }

        stopForeground(true);
        stopSelf();
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