import android.app.Activity;
import android.util.Log;

import jarden.tcp.TcpControllerServer;

/**
 * Created by john.denny@gmail.com on 05/01/2026.
 */
public class GameHostMessageListener implements
        TcpControllerServer.MessageListener {
    private final String TAG = "GameHostMessageListener";
    private final Activity activity;

    public GameHostMessageListener(Activity activity) {
        this.activity = activity;
    }
    @Override // TcpControllerServer.MessageListener
    public void onMessage(String playerId, String message) {
        Log.d(TAG, playerId + ": " + message);


        // Example:
        // ANSWER|3|My fake definition
        // VOTE|3|2
    }

    @Override // TcpControllerServer.MessageListener
    public void onPlayerConnected(String playerId) {
        Log.d(TAG, "Player joined: " + playerId);
    }

    @Override // TcpControllerServer.MessageListener
    public void onPlayerDisconnected(String playerId) {
        Log.d(TAG, "Player left: " + playerId);
    }

    @Override
    public void onServerStarted() {
        /*
        runOnUiThread(new Runnable() {
            public void run() {
                joinGame();
                nextQuestionButton.setEnabled(true);
            }
        });
        */
    }
}
