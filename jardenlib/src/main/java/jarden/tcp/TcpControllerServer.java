package jarden.tcp;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.jardenconsulting.jardenlib.BuildConfig;

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
        void onMessage(String playerName, String message);
        void onPlayerConnected(String playerName);
        void onPlayerDisconnected(String playerName);
        void onServerStarted();
    }

    private static final String TAG = "TcpControllerServer";
    public static final int TCP_PORT = 50001;
    public static final int UDP_PORT = 45454;
    private final ExecutorService executor =
            Executors.newCachedThreadPool();
    private final Map<String, ClientHandler> clients =
            new ConcurrentHashMap<>();
    private final MessageListener listener;
    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private String HostIpAddress = null; // "192.168.0.12"; // john's Moto g8 at home
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
                    Socket tcpSocket = serverSocket.accept();
                    ClientHandler handler = new ClientHandler(tcpSocket);
                    executor.execute(handler);
                }
            } catch (IOException e) {
                if (running) {
                    if (BuildConfig.DEBUG) {
                        Log.e(TAG, "Server error", e);
                    }
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

    public void sendToPlayer(String playerName, String message) {
        ClientHandler handler = clients.get(playerName);
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
        private final Socket tcpSocket;
        private BufferedReader in;
        private PrintWriter out;
        private String playerName;

        ClientHandler(Socket tcpSocket) {
            this.tcpSocket = tcpSocket;
        }

        @Override
        public void run() {
            try {
                in = new BufferedReader(
                        new InputStreamReader(tcpSocket.getInputStream()));
                out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(tcpSocket.getOutputStream())),
                        true);
                // First message must be JOIN
                // JOIN|playerName
                String join = in.readLine();
                if (join == null || !join.startsWith("JOIN|")) {
                    close();
                    return;
                }
                playerName = join.split("\\|", 2)[1];
                clients.put(playerName, this);
                listener.onPlayerConnected(playerName);
                String line;
                while ((line = in.readLine()) != null) {
                    listener.onMessage(playerName, line);
                }
            } catch (IOException e) {
                if (BuildConfig.DEBUG) {
                    Log.w(TAG, "Client disconnected: " + playerName, e);
                }
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
                tcpSocket.close();
            } catch (IOException ignored) {}
            if (playerName != null) {
                clients.remove(playerName);
                listener.onPlayerDisconnected(playerName);
            }
        }
    }
    public void sendHostBroadcast(Context context) {
        new Thread(() -> {
            try {
                if (HostIpAddress == null) {
                    WifiManager wifi =
                            (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = wifi.getConnectionInfo();
                    int ipInt = info.getIpAddress();
                    HostIpAddress = String.format(
                            "%d.%d.%d.%d",
                            (ipInt & 0xff),
                            (ipInt >> 8 & 0xff),
                            (ipInt >> 16 & 0xff),
                            (ipInt >> 24 & 0xff));
                }
                String message =
                        "HOST_ANNOUNCE|" + HostIpAddress + "|" + TCP_PORT;
                DatagramSocket udpSocket = new DatagramSocket();
                udpSocket.setBroadcast(true);
                InetAddress broadcastAddress =
                        InetAddress.getByName("255.255.255.255");
                byte[] data = message.getBytes(StandardCharsets.UTF_8);
                DatagramPacket packet =
                        new DatagramPacket(data, data.length,
                                broadcastAddress, UDP_PORT);
                udpSocket.send(packet);
                udpSocket.close();
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "Broadcast sent: " + message);
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.e(TAG, "Broadcast failed", e);
                }
            }
        }).start();
    }
}

