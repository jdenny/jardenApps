package jarden.balderdash;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import jarden.tcp.TcpControllerServer;
import jarden.tcp.TcpPlayerClient;

/** Design of application
 Message Protocol:
    ANSWER|3|My fake definition
    ALL_ANSWERS|3|1 dollop|2 doofer
    VOTE|3|2
 One device is selected as the Server; other devices connect to the Server
 Server gets next question from dictionary and sends to other devices
 All players, including Server, see the question; supply their answer, which is sent to Server
 When Server has the answers, including the real one, it sends them to all Clients, in random order
 Players give their votes; when all votes in, Server highlights the real answer
 three screens:
 1  HostButton, JoinButton, NextButton, SendButton
    PlayerNameEditText
    QuestionTextView
 buttons: initially disable Next and Send
 HostGame: disable HostGame; startServer(); after serverStarted: joinGame(); enable Next
 JoinGame: disable HostGame & JoinGame; enable Send.

 2  list of:
        optionNumber, answer (players click row to vote)
    when all votes in, list changes to
        playerName/correct, answer (correct answer highlighted)
    ScoresButton -> next screen

 3  list of:
        playerName, score (goes to first screen when host types NextButton)


 TODO next:
 onSend -> disable sendButton
 random order for allAnswers
 on receiving nextQuestion -> enable sendButton
 process Send! When all players sent answers, show all the answers; let players vote, etc.
 use a proper database of QA!
 only use Log.d(message) if in debug mode
 */
public class GameActivity extends AppCompatActivity implements
        TcpControllerServer.MessageListener, View.OnClickListener, TcpPlayerClient.Listener {
    /*
    public static final String MULTICAST_IP = "239.255.0.1";
    public static final int MULTICAST_PORT = 50000;
    WifiManager.MulticastLock multicastLock;
     */
    private static final String TAG = "Balderdash";
    private static final String ALL_ANSWERS = "ALL_ANSWERS";
    private TcpControllerServer server;
    private TcpPlayerClient client;
    private String controllerAddress = "192.168.0.12"; // john's Moto g8 at home
    private final Map<String, String> answers =
            new ConcurrentHashMap<>();
    private boolean isHost;
    private EditText nameEditText;
    private EditText answerEditText;
    private TextView outputView;
    private Button hostButton;
    private Button joinButton;
    private Button sendButton;
    private Button nextQuestionButton;
    private String playerName;
    private int round = 0;
    private int answersCt = 0;
    private QuestionAnswer currentQuestionAnswer;
    private FragmentManager fragmentManager;
    private AnswersFragment answersFragment;

    @Override // Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            this.answersFragment = new AnswersFragment();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(this.answersFragment, ALL_ANSWERS);
            ft.commit();
        } else {
            answersFragment = (AnswersFragment) fragmentManager.findFragmentByTag(ALL_ANSWERS);
        }


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
        hostButton = findViewById(R.id.hostButton);
        hostButton.setOnClickListener(this);
        joinButton = findViewById(R.id.joinButton);
        joinButton.setOnClickListener(this);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        nextQuestionButton.setOnClickListener(this);
        nextQuestionButton.setEnabled(false);
        sendButton = findViewById(R.id.sendButton);
        sendButton.setOnClickListener(this);
        sendButton.setEnabled(false);
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


    @Override // TcpControllerServer.MessageListener
    // i.e. message sent from player to host
    public void onMessage(String playerId, String message) {
        if (message.startsWith("ANSWER")) {
            Log.d(TAG, playerId + ": " + message);
            String answer = message.split("\\|", 3)[2];
            /*if (BuildConfig.DEBUG)*/
            answers.put(playerId, answer);
            answersCt++;
            if (answersCt == answers.size()) {
                /*
                Put them into a collection:
                    A answerJohn
                    B answerJulie
                    C correctAnswer
                New collection in random order
                Show:
                    1 answerJulie
                    2 correctAnswer
                    3 answerJohn
                 */
                Log.d(TAG, "all answers received for current question");
                StringBuffer buffer = new StringBuffer(ALL_ANSWERS + "\\|" + questionIndex);
                String answerI;
                for (String nameI : answers.keySet()) {
                    answerI = answers.get(nameI);
                    buffer.append("\\|" + nameI + " " + answerI);
                }
                buffer.append("\\|" + currentQuestionAnswer.answer);
                server.sendToAll(buffer.toString());
            }
        } else {
            Toast.makeText(this, "unrecognised message received: " + message,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override // TcpControllerServer.MessageListener
    public void onPlayerConnected(String playerId) {
        Log.d(TAG, "Player joined: " + playerId);
        answers.put(playerId, "");
    }

    @Override // TcpControllerServer.MessageListener
    public void onPlayerDisconnected(String playerId) {
        Log.d(TAG, "Player left: " + playerId);
    }

    @Override // TcpControllerServer.MessageListener
    public void onServerStarted() {
        runOnUiThread(new Runnable() {
            public void run() {
                joinGame();
                nextQuestionButton.setEnabled(true);
            }
        });

    }

    @Override // View.OnClickListener
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.hostButton || viewId == R.id.joinButton) {
            playerName = nameEditText.getText().toString().trim();
            if (playerName.length() == 0) {
                Toast.makeText(this, "Supply your name first!", Toast.LENGTH_LONG).show();
            } else if (viewId == R.id.hostButton) {
                hostButton.setEnabled(false);
                getControllerAddress();
                server = new TcpControllerServer(this);
                server.start();
                isHost = true;
                //? sendMulticast("HOST_ANNOUNCE|" + localIp + "|50001");
            } else { // must be joinButton
                joinGame();
            }
        } else if (viewId == R.id.nextQuestionButton) {
            getNextQuestion();
        } else if (viewId == R.id.sendButton) {
            String answer = this.answerEditText.getText().toString();
            client.sendAnswer(round, answer);
        } else {
            Toast.makeText(this, "unknown button pressed: " + view,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void joinGame() {
        hostButton.setEnabled(false);
        joinButton.setEnabled(false);
        String name = nameEditText.getText().toString();
        client = new TcpPlayerClient(controllerAddress, 50001, playerName, this);
        client.connect();
        sendButton.setEnabled(true);
    }

    private void getControllerAddress() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "about to get WifiManager");
                Context context = getApplicationContext();
                WifiManager wifiManager =
                        (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                int ipInt = wifiInfo.getIpAddress();
                controllerAddress = String.format(
                        "%d.%d.%d.%d",
                        (ipInt & 0xff),
                        (ipInt >> 8 & 0xff),
                        (ipInt >> 16 & 0xff),
                        (ipInt >> 24 & 0xff));
    //            runOnUiThread(new Runnable() {
    //                public void run() {
                        Log.i(TAG, "controllerAddress: " + controllerAddress);
    //                }
    //            });
            }
        }).start();
    }

    private class QuestionAnswer {
        String question;
        String answer;
        QuestionAnswer(String q, String a) {
            question = q;
            answer = a;
        }
    }
    private final QuestionAnswer[] questions = {
            new QuestionAnswer("A Swiss teenager has made a fully functional submarine out of... what?", "a pig trough"),
            new QuestionAnswer("What are 'Pooks'?","small piles of hay"),
            new QuestionAnswer("In San Francisco, California, it is illegal to dance...", "to the Star Spangled Banner"),
            new QuestionAnswer("What does F.E.F.O. an abbreviation of?", "Petrified Forest National Park"),
            new QuestionAnswer("Who was Gustav Vigeland?", "Norway's most famous sculptor, known for his giant sculpture park in Oslo")
    };
    private int questionIndex = -1;
    private void getNextQuestion() {
        questionIndex++;
        if (questionIndex >= questions.length) questionIndex = 0;
        currentQuestionAnswer = questions[questionIndex];
        server.sendToAll(currentQuestionAnswer.question);
        answersCt = 0;
    }

    @Override // TcpPlayerClient.Listener
    public void onConnected() {
        Log.d(TAG, "Now connected to the game server");
    }

    @Override // TcpPlayerClient.Listener
    // i.e. message sent from host to  player
    public void onMessage(String message) {
        Log.d(TAG, "TcpPlayerClient.Listener.onMessage(" + message + ")");
        runOnUiThread(new Runnable() {
            public void run() {
                String mess2 = message;
                if (mess2.startsWith(ALL_ANSWERS)) {
                    mess2 = mess2.substring(ALL_ANSWERS.length() + 3).replace("\\|", "\n");
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragmentContainerView, answersFragment);
                    transaction.commit();
                    answersFragment.showAnswers(null);
                } else {
                    outputView.setText(mess2);
                }
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

        /*!!


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

}