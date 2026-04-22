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
    public interface ClientListener {
        void onHostFound(String hostIp, int port);
        void onConnected();
        void onMessageToClient(String message);
        void onDisconnected();
        void onError(Exception e);
    }

    private static final String TAG = "TcpPlayerClient";
    private ExecutorService udpExecutor =
            Executors.newSingleThreadExecutor();
    private final ExecutorService readExecutor =
            Executors.newSingleThreadExecutor();
    private final ExecutorService writeExecutor =
            Executors.newSingleThreadExecutor();
    private ClientListener clientListener;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private volatile boolean connectedToHost = false;

    // ----------------------------
    // Connect / Disconnect
    // ----------------------------

    public void connect(
            String hostAddress,
            int port,
            String playerName,
            ClientListener clientListener) {
        this.clientListener = clientListener;

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
                connectedToHost = true;
                clientListener.onConnected();
                String line;
                while (connectedToHost && (line = in.readLine()) != null) {
                    clientListener.onMessageToClient(line);
                }
            } catch (Exception e) {
                clientListener.onError(e);
            } finally {
                disconnect();
            }
        });
    }
    public void disconnect() {
        if (connectedToHost) {
            connectedToHost = false;
            if (readExecutor != null) {
                readExecutor.shutdownNow();
            }
            if (writeExecutor != null) {
                writeExecutor.shutdownNow();
            }
            if (udpExecutor != null) {
                udpExecutor.shutdownNow();
            }
            clientListener.onDisconnected();
            try {
                if (socket != null) socket.close();
            } catch (IOException ignored) {}
        }
    }
    public boolean isConnectedToHost() {
        return connectedToHost;
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
    public void listenForHostBroadcast(WifiManager wifi, ClientListener callback) {
        this.clientListener = callback;
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
                socket = new DatagramSocket(TcpHostServer.UDP_PORT);
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
                    if (BuildConfig.DEBUG) {
                        Log.d("UDP_CLIENT", "Received: " + msg);
                    }
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
}

