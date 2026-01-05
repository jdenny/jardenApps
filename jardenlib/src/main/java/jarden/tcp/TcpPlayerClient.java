package jarden.tcp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by john.denny@gmail.com on 03/01/2026.
 */
public class TcpPlayerClient {
    private static final String TAG = "TcpPlayerClient";

    public interface Listener {
        void onConnected();
        void onMessage(String message);
        void onDisconnected();
        void onError(Exception e);
    }

    private final ExecutorService readExecutor =
            Executors.newSingleThreadExecutor();
    private final ExecutorService writeExecutor =
            Executors.newSingleThreadExecutor();

    private final Listener listener;

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private volatile boolean running = false;

    private final String hostAddress;
    private final int port;
    private final String playerId;

    public TcpPlayerClient(
            String hostAddress,
            int port,
            String playerId,
            Listener listener) {

        this.hostAddress = hostAddress;
        this.port = port;
        this.playerId = playerId;
        this.listener = listener;
    }

    // ----------------------------
    // Connect / Disconnect
    // ----------------------------

    public void connect() {
        readExecutor.execute(() -> {
            try {
                socket = new Socket(hostAddress, port);

                out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                        true);

                in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));

                // Send JOIN immediately
                out.println("JOIN|" + playerId);

                running = true;
                listener.onConnected();

                String line;
                while (running && (line = in.readLine()) != null) {
                    listener.onMessage(line);
                }

            } catch (Exception e) {
                listener.onError(e);
            } finally {
                disconnect();
            }
        });
    }

    public void disconnect() {
        running = false;

        try {
            if (socket != null) socket.close();
        } catch (IOException ignored) {}

        listener.onDisconnected();
    }

    // ----------------------------
    // Send messages
    // ----------------------------

    public void sendAnswer(int round, String answer) {
        send("ANSWER|" + round + "|" + answer);
    }

    public void sendVote(int round, int choiceIndex) {
        send("VOTE|" + round + "|" + choiceIndex);
    }

    public void send(String message) {
        Log.d(TAG, "send(" + message + ")");
        writeExecutor.execute(() -> {
            Log.d(TAG, "send(" + message + ") inside execute thread");
            if (out != null) {
                out.println(message);
            } else {
                Log.d(TAG, "send(" + message + ") out is null!");
            }
        });
    }
}

