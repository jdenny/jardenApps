package jarden.balderdash;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import jarden.tcp.TcpControllerServer;
import jarden.tcp.TcpPlayerClient;

public class GameActivity extends AppCompatActivity implements
        TcpControllerServer.MessageListener, /*AdapterView.OnItemClickListener,*/ View.OnClickListener, TcpPlayerClient.Listener {
    /*!!
    public static final String MULTICAST_IP = "239.255.0.1";
    public static final int MULTICAST_PORT = 50000;
    public static final int CONTROLLER_PORT = 50001;
    private ChatNetIF chat;
    WifiManager.MulticastLock multicastLock;
     */
    private static final String TAG = "Balderdash";
    private TcpControllerServer server;
    private TcpPlayerClient client;
    private String controllerAddress = "127.0.0.1";
    private boolean isHost;
    private EditText nameEditText;
    private EditText answerEditText;
    private TextView outputView;
    /*!!
    private ListView usersListView;
    private ArrayList<User> userList = new ArrayList<>();
    private ArrayList<String> userListStr = new ArrayList<>();

     */
    @Override // Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*?
        isHost = getIntent().getBooleanExtra("HOST", false);
        if (isHost) {
            server = new TcpControllerServer(this);
            server.start();
        }

         */


        setContentView(R.layout.activity_game);
        /*!!
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
         */
        Button hostButton = findViewById(R.id.hostButton);
        hostButton.setOnClickListener(this);
        Button joinButton = findViewById(R.id.joinButton);
        joinButton.setOnClickListener(this);
        Button nextQuestionButton = findViewById(R.id.nextQuestionButton);
        nextQuestionButton.setOnClickListener(this);
        nameEditText = findViewById(R.id.nameEditText);
        answerEditText = findViewById(R.id.answerEditText);
        outputView = findViewById(R.id.outputView);
        /*!! usersListView = findViewById(R.id.usersListView);
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

         */
    }
    @Override // Activity
    protected void onDestroy() {
        super.onDestroy();
        if (server != null) server.stop();
        if (client != null) client.disconnect();
        //!! multicastLock.release();
    }

    /*!!
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
     */

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

    @Override // View.OnClickListener
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.hostButton) {
            server = new TcpControllerServer(this);
            server.start();
            //? sendMulticast("HOST_ANNOUNCE|" + localIp + "|50001");
        } else if (viewId == R.id.joinButton) {
            String name = nameEditText.getText().toString();
            client = new TcpPlayerClient(controllerAddress, 50001, name, this);
            client.connect();
        } else if (viewId == R.id.nextQuestionButton) {
            getNextQuestion();
        } else {
            Toast.makeText(this, "unknown button pressed: " + view,
                    Toast.LENGTH_LONG).show();
        }

    }

    private String[] questions = {
            "A Swiss teenager has made a fully functional submarine out of...",
            "What are 'Pooks'?",
            "In Ssan Francisco, California, it is illegal to dance...",
            "Who was Gustav Vigeland?"
    };
    private int questionIndex = -1;
    private void getNextQuestion() {
        questionIndex++;
        if (questionIndex >= questions.length) questionIndex = 0;
        String nextQuestion = questions[questionIndex];
        server.sendToAll(nextQuestion);
    }

    @Override // TcpPlayerClient.Listener
    public void onConnected() {
        Log.d(TAG, "Now connected to the game server");
    }

    @Override // TcpPlayerClient.Listener
    public void onMessage(String message) {
        Log.d(TAG, "onMessage(" + message + ")");
        runOnUiThread(new Runnable() {
            public void run() {
                outputView.setText(message);
            }
        });
    }

    @Override // TcpPlayerClient.Listener
    public void onDisconnected() {
        Log.d(TAG, "Now disconnected from the game server");
    }

    @Override // TcpPlayerClient.Listener
    public void onError(Exception e) {
        Log.e(TAG, e.toString());
    }
}