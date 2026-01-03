package jarden.tcp;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by john.denny@gmail.com on 03/01/2026.
 */
public class TcpControllerServer {

    public interface MessageListener {
        void onMessage(String playerId, String message);
        void onPlayerConnected(String playerId);
        void onPlayerDisconnected(String playerId);
    }

    private static final int PORT = 50001;

    private final ExecutorService executor =
            Executors.newCachedThreadPool();

    private final Map<String, ClientHandler> clients =
            new ConcurrentHashMap<>();

    private final MessageListener listener;

    private ServerSocket serverSocket;
    private volatile boolean running = false;

    public TcpControllerServer(MessageListener listener) {
        this.listener = listener;
    }

    // ----------------------------
    // Start / Stop server
    // ----------------------------

    public void start() {
        running = true;

        executor.execute(() -> {
            try {
                serverSocket = new ServerSocket(PORT);

                while (running) {
                    Socket socket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(socket);
                    executor.execute(handler);
                }

            } catch (IOException e) {
                if (running) {
                    Log.e("TCP", "Server error", e);
                }
            }
        });
    }

    public void stop() {
        running = false;

        try {
            if (serverSocket != null) {
                serverSocket.close();
            }
        } catch (IOException ignored) {}

        for (ClientHandler handler : clients.values()) {
            handler.close();
        }

        executor.shutdownNow();
    }

    // ----------------------------
    // Send messages
    // ----------------------------

    public void sendToPlayer(String playerId, String message) {
        ClientHandler handler = clients.get(playerId);
        executor.execute(() -> {
            if (handler != null) {
                handler.send(message);
            }
        });


    }

    public void sendToAll(String message) {
        for (ClientHandler handler : clients.values()) {
            handler.send(message);
        }
    }

    // ----------------------------
    // Client handler
    // ----------------------------

    private class ClientHandler implements Runnable {

        private final Socket socket;
        private BufferedReader in;
        private PrintWriter out;
        private String playerId;

        ClientHandler(Socket socket) {
            this.socket = socket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                        true);

                // First message must be JOIN
                // JOIN|playerId
                String join = in.readLine();
                if (join == null || !join.startsWith("JOIN|")) {
                    close();
                    return;
                }

                playerId = join.split("\\|", 2)[1];
                clients.put(playerId, this);

                listener.onPlayerConnected(playerId);

                String line;
                while ((line = in.readLine()) != null) {
                    listener.onMessage(playerId, line);
                }

            } catch (IOException e) {
                // Client disconnected
            } finally {
                close();
            }
        }

        void send(String message) {
            executor.execute(() -> {
                if (out != null) {
                    out.println(message);
                }
            });
        }

        void close() {
            try {
                socket.close();
            } catch (IOException ignored) {}

            if (playerId != null) {
                clients.remove(playerId);
                listener.onPlayerDisconnected(playerId);
            }
        }
    }
}

