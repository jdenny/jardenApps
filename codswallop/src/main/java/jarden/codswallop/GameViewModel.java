package jarden.codswallop;

import android.util.Log;

import androidx.lifecycle.ViewModel;
import jarden.tcp.TcpControllerServer;
import jarden.tcp.TcpPlayerClient;

/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class GameViewModel extends ViewModel {
    private final static String TAG = "GameViewModel";
    private final TcpPlayerClient tcpPlayerClient = new TcpPlayerClient();
    private TcpControllerServer tcpControllerServer;
    public TcpPlayerClient getTcpPlayerClient() {
        return tcpPlayerClient;
    }
    public TcpControllerServer getTcpControllerServer() {
        return tcpControllerServer;
    }
    public void setTcpControllerServer(TcpControllerServer server) {
        this.tcpControllerServer = server;
    }
    @Override
    protected void onCleared() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCleared()");
        }
        if (tcpControllerServer != null) {
            tcpControllerServer.stop();
        }
        if (tcpPlayerClient != null) {
            tcpPlayerClient.stopListening();
            tcpPlayerClient.disconnect();
        }
    }
}
