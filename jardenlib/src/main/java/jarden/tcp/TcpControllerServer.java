package jarden.tcp;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
        void onServerStarted();
    }

    private String controllerIpAddress = null;
    public static final int TCP_PORT = 50001;
    public static final int UDP_PORT = 45454;


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
                serverSocket = new ServerSocket(TCP_PORT);
                listener.onServerStarted();
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
        // TODO: run on thread?
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
                throw new RuntimeException(e);
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
    public void sendHostBroadcast(Context context) {
        new Thread(() -> {
            try {
                if (controllerIpAddress == null) {
                    WifiManager wifi =
                            (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                    WifiInfo info = wifi.getConnectionInfo();
                    int ipInt = info.getIpAddress();

                    controllerIpAddress = String.format(
                            "%d.%d.%d.%d",
                            (ipInt & 0xff),
                            (ipInt >> 8 & 0xff),
                            (ipInt >> 16 & 0xff),
                            (ipInt >> 24 & 0xff));
                }
                String message =
                        "HOST_ANNOUNCE|" + controllerIpAddress + "|" + TCP_PORT;
                DatagramSocket socket = new DatagramSocket();
                socket.setBroadcast(true);
                InetAddress broadcastAddress =
                        InetAddress.getByName("255.255.255.255");
                byte[] data = message.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet =
                        new DatagramPacket(data, data.length,
                                broadcastAddress, UDP_PORT);
                socket.send(packet);
                socket.close();
                Log.d("UDP_HOST", "Broadcast sent: " + message);
            } catch (Exception e) {
                Log.e("UDP_HOST", "Broadcast failed", e);
            }
        }).start();
    }
}

