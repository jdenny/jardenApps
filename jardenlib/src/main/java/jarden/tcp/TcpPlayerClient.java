package jarden.tcp;

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
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by john.denny@gmail.com on 03/01/2026.
 */
public class TcpPlayerClient {
    private static final String TAG = "TcpPlayerClient";

    public interface Listener {
        void onHostFound(String hostIp, int port);
        void onConnected();
        void onMessage(String message);
        void onDisconnected();
        void onError(Exception e);
    }
    private ExecutorService udpExecutor =
            Executors.newSingleThreadExecutor();
    private final ExecutorService readExecutor =
            Executors.newSingleThreadExecutor();
    private final ExecutorService writeExecutor =
            Executors.newSingleThreadExecutor();
    private Listener listener;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean running = false;
    private String hostAddress;
    private int port;
    private String playerName;

    // ----------------------------
    // Connect / Disconnect
    // ----------------------------

    public void connect(
            String hostAddress,
            int port,
            String playerName,
            Listener listener) {
        this.hostAddress = hostAddress;
        this.port = port;
        this.playerName = playerName;
        this.listener = listener;

        readExecutor.execute(() -> {
            try {
                socket = new Socket();
                socket.connect(
                        new InetSocketAddress(hostAddress, port),
                        5000); // connect timeout only
                out = new PrintWriter(
                        new BufferedWriter(
                                new OutputStreamWriter(socket.getOutputStream())),
                        true);
                in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                // Send JOIN immediately
                out.println("JOIN|" + playerName);
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
        if (running) {
            running = false;
            if (readExecutor != null) {
                readExecutor.shutdownNow();
            }
            if (writeExecutor != null) {
                writeExecutor.shutdownNow();
            }
            listener.onDisconnected();
            try {
                if (socket != null) socket.close();
            } catch (IOException ignored) {}
        }
    }
    public String getPlayerName() {
        return playerName;
    }

    // ----------------------------
    // Send messages
    // ----------------------------
    // ----------------------------

    public void sendAnswer(int round, String answer) {
        send("ANSWER|" + round + "|" + answer);
    }

    public void sendVote(int round, int selectionIndex) {
        send("VOTE|" + round + "|" + selectionIndex);
    }
    public void send(String message) {
        writeExecutor.execute(() -> {
            if (out != null) {
                out.println(message);
            } else {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "send(" + message + ") out is null!");
                }
            }
        });
    }
    public void listenForHostBroadcast(WifiManager wifi, Listener callback) {
        if (udpExecutor == null ||
                udpExecutor.isShutdown() ||
                udpExecutor.isTerminated()) {
            udpExecutor = Executors.newSingleThreadExecutor();
        }
        udpExecutor.execute(() -> {
            WifiManager.MulticastLock lock =
                    wifi.createMulticastLock("codswallopLock");
            lock.acquire();
            DatagramSocket socket = null;
            try {
                socket = new DatagramSocket(TcpControllerServer.UDP_PORT);
                socket.setBroadcast(true);
                byte[] buf = new byte[1024];
                if (BuildConfig.DEBUG) {
                    Log.d("UDP_CLIENT", "Listening for host...");
                }
                boolean hostFound = false;
                while (!hostFound) {
                    DatagramPacket packet =
                            new DatagramPacket(buf, buf.length);
                    socket.receive(packet);
                    String msg = new String(packet.getData(),
                            0,
                            packet.getLength(),
                            StandardCharsets.UTF_8);
                    Log.d("UDP_CLIENT", "Received: " + msg);
                    if (msg.startsWith("HOST_ANNOUNCE|")) {
                        String[] parts = msg.split("\\|");
                        String hostIp = parts[1];
                        int port = Integer.parseInt(parts[2]);
                        callback.onHostFound(hostIp, port);
                        hostFound = true;
                    }
                }
            } catch (Exception e) {
                if (BuildConfig.DEBUG) {
                    Log.e("UDP_CLIENT", "Listen failed", e);
                }
                callback.onError(e);
            } finally {
                if (socket != null) {
                    socket.close();
                }
                lock.release();
            }
        });
    }
    public void stopListening() {
        udpExecutor.shutdownNow();
    }
}

