package com.jardenconsulting.wifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MyWiFiActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "MyWiFiActivity";
    private static final int PORT = 1234;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver;
    private IntentFilter mIntentFilter;
    private EditText messageEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        Button button = findViewById(R.id.discoverButton);
        button.setOnClickListener(this);
        button = findViewById(R.id.serverButton);
        button.setOnClickListener(this);
        button = findViewById(R.id.transmitButton);
        button.setOnClickListener(this);
        this.messageEditText = findViewById(R.id.messageEditText);
    }
    /* register the broadcast receiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }
    public void showMessage(String message) {
        if (BuildConfig.DEBUG) Log.d(TAG, message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }
    public void transferMessage(String host) {
        String message = messageEditText.getText().toString();
        showMessage("transferMessage(" + host + "); message=" + message);
        new ClientAsyncTask(this, host, message).execute();
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.serverButton) {
            new ServerAsyncTask(this).execute();
        } else if (id == R.id.discoverButton) {
            mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    showMessage("discoverPeers.onSuccess()");
                }

                @Override
                public void onFailure(int reasonCode) {
                    showMessage("discoverPeers.onFailure()");
                }
            });
        } else if (id == R.id.transmitButton) {
            showMessage("not implemented!");
        }
    }
    public static class ClientAsyncTask extends AsyncTask<Void, Void, String> {
        private MyWiFiActivity activity;
        private String outMessage;
        private String host;

        public ClientAsyncTask(MyWiFiActivity activity, String host, String outMessage) {
            this.activity = activity;
            this.host = host;
            this.outMessage = outMessage;
        }

        @Override
        protected String doInBackground(Void... voids) {
            Socket socket = null;
            try {
                socket = new Socket(host, PORT);
                OutputStream os = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(os);
                writer.println(outMessage);
                writer.flush();
                socket.close();
            } catch (IOException ioe) {
                Log.e(TAG, "exception=" + ioe);
            }
            return "message sent: " + outMessage;
        }
    }
    public static class ServerAsyncTask extends AsyncTask<Void, Void, String> {

        private MyWiFiActivity activity;
        private String message;

        public ServerAsyncTask(MyWiFiActivity activity) {
            this.activity = activity;
        }
        @Override
        protected String doInBackground(Void... params) {
            try {

                /**
                 * Create a server socket and wait for client connections. This
                 * call blocks until a connection is accepted from a client
                 */
                ServerSocket serverSocket = new ServerSocket(PORT);
                Log.d(TAG, "server socket waiting for clients!");
                Socket client = serverSocket.accept();

                /**
                 * If this code is reached, a client has connected and transferred data
                 */
                InputStream inputstream = client.getInputStream();
                InputStreamReader isr = new InputStreamReader(inputstream);
                BufferedReader reader = new BufferedReader(isr);
                String line;
                StringBuilder builder = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
                serverSocket.close();
                message = builder.toString();
                Log.e(TAG, message);
                return message;
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            activity.showMessage("result=" + result);
            activity.showMessage("message=" + message);
        }

    }
}
