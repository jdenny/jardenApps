package jarden.balderdash;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import jarden.net.ChatListener;
import jarden.net.ChatNet;
import jarden.net.ChatNetIF;
import jarden.net.User;

public class MainActivity extends AppCompatActivity implements ChatListener, OnItemClickListener {
    public static final String MULTICAST_IP = "239.255.0.1";
    public static final int MULTICAST_PORT = 50000;
    public static final int CONTROLLER_PORT = 50001;
    private ChatNetIF chat;
    private EditText editText;
    private TextView outputView;
    private ListView usersListView;
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<String> userListStr = new ArrayList<>();
    WifiManager.MulticastLock multicastLock;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*!!
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         */
        editText = findViewById(R.id.editText);
        outputView = findViewById(R.id.outputView);
        usersListView = findViewById(R.id.usersListView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                userListStr);
        usersListView.setAdapter(adapter);
        usersListView.setOnItemClickListener(this);
        try {
            WifiManager wifiManager =
                    (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            multicastLock =
                    wifiManager.createMulticastLock("game_multicast");
            multicastLock.setReferenceCounted(true);
            multicastLock.acquire();
            chat = new ChatNet(this, "John", 8002);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        multicastLock.release();
    }

    @Override // ChatListener
    public void addUser(User user) {
        userList.add(user);
        userListStr.add(user.toString());
    }

    @Override // ChatListener
    public void showMessage(String message) {

    }

    @Override // ChatListener
    public void setChat(ChatNetIF chat) {

    }

    @Override // OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override // OnItemClickListener
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
    public static void sendMulticast(Context context, String message) {

        DatagramSocket socket = null;

        try {
            // Acquire multicast lock (required on Android)
            WifiManager wifiManager =
                    (WifiManager) context.getApplicationContext()
                            .getSystemService(Context.WIFI_SERVICE);

            WifiManager.MulticastLock multicastLock =
                    wifiManager.createMulticastLock("balderdash_multicast");
            multicastLock.acquire();

            socket = new DatagramSocket();
            socket.setReuseAddress(true);

            byte[] data = message.getBytes(StandardCharsets.UTF_8);

            InetAddress group = InetAddress.getByName("239.255.0.1");
            int port = 50000;

            DatagramPacket packet =
                    new DatagramPacket(data, data.length, group, port);

            socket.send(packet);

            multicastLock.release();

        } catch (Exception e) {
            Log.e("NET", "sendMulticast failed", e);
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }

}