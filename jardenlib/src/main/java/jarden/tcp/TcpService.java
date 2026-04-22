package jarden.tcp;

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
import android.os.IBinder;
import android.util.Log;

import com.jardenconsulting.jardenlib.BuildConfig;

import androidx.core.app.NotificationCompat;

public class TcpService extends Service {
    private static final String TAG = "TcpService";
    public static final String CHANNEL_ID = "codswallop_network";
    private final IBinder binder = new LocalBinder();
    private TcpHostServer tcpHostServer;
    private TcpPlayerClient tcpPlayerClient = new TcpPlayerClient();
    private boolean isForeground = false;
    private WifiManager.WifiLock wifiLock;
    private boolean netWorkRunning = true;
    private String hostIpAddress;

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
    public void listenForHostBroadcast(WifiManager wifi, TcpPlayerClient.Listener listener) {
        tcpPlayerClient.listenForHostBroadcast(wifi, listener);
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
    public class LocalBinder extends Binder {
        public TcpService getService() {
            return TcpService.this;
        }
    }
    public void startHosting(TcpHostServer.ServerListener serverListener) {
        tcpHostServer = new TcpHostServer(serverListener);
        tcpHostServer.start();
    }
    public void connect(String hostIp,
                         String playerName,
                         TcpPlayerClient.Listener listener) {
        tcpPlayerClient.connect(hostIp,
                TcpHostServer.TCP_PORT,
                playerName,
                listener);
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