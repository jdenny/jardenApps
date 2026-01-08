package jarden.balderdash;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import jarden.tcp.TcpControllerServer;
import jarden.tcp.TcpPlayerClient;

import static android.view.View.GONE;

/** Design of application
 Message Protocol:
    QUESTION|3|Who was Gustav Vigeland?
    ANSWER|3|Centre forward for Liverpool
    ALL_ANSWERS|3|Norway's most famous sculptor|Centre forward for Liverpool
    VOTE|3|1 - index of players vote
    SCORES|3|2|John 2|Julie 4 - 3rd field is index of correct answer
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
 on receiving nextQuestion -> enable sendButton
 let players vote, etc.
 use a proper database of QA!
 only use Log.d(message) if in debug mode
 */
public class GameActivity extends AppCompatActivity implements
        TcpControllerServer.MessageListener, View.OnClickListener,
        AdapterView.OnItemClickListener, TcpPlayerClient.Listener {
    /*
    public static final String MULTICAST_IP = "239.255.0.1";
    public static final int MULTICAST_PORT = 50000;
    WifiManager.MulticastLock multicastLock;
     */
    private static final String TAG = "GameActivity";
    private static final String QUESTION = "QUESTION";
    private static final String ANSWER = "ANSWER";
    private static final String ALL_ANSWERS = "ALL_ANSWERS";
    private static final String MAIN = "MAIN";
    private static final String CORRECT = "CORRECT";
    private static final String VOTE = "VOTE";
    private static final String SCORES = "SCORES";

    private TcpControllerServer server;
    private TcpPlayerClient client;
    private String controllerAddress = "192.168.0.12"; // john's Moto g8 at home
    private final Map<String, String> namesAnswers =
            new ConcurrentHashMap<>();
    private boolean isHost;
    private Button hostButton;
    private Button joinButton;
    private Button sendButton;
    private Button nextQuestionButton;
    private TextView statusTextView;
    private String playerName;
    private int round = 0;
    private int answersCt = 0;
    private int votesCt = 0;
    private QuestionAnswer currentQuestionAnswer;
    private FragmentManager fragmentManager;
    private AnswersFragment answersFragment;
    private MainFragment mainFragment;
    private String currentFragmentTag = MAIN;
    private ScoresFragment scoresFragment;
    private List<String> shuffledNameList = new ArrayList<>();

    @Override // Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            this.mainFragment = new MainFragment();
            this.answersFragment = new AnswersFragment();
            this.scoresFragment = new ScoresFragment();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.fragmentContainerView, this.mainFragment, MAIN);
            ft.commit();
            answersFragment.setOnItemClickListener(this);
        } else {
            mainFragment = (MainFragment) fragmentManager.findFragmentByTag(MAIN);
            answersFragment = (AnswersFragment) fragmentManager.findFragmentByTag(ALL_ANSWERS);
            scoresFragment = (ScoresFragment) fragmentManager.findFragmentByTag(SCORES);
        }

        /*?
        isHost = getIntent().getBooleanExtra("HOST", false);
        if (isHost) {
            server = new TcpControllerServer(this);
            server.start();
        }
         */

        setContentView(R.layout.activity_game);
        /*??
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
        statusTextView = findViewById(R.id.statusView);
        /* later!
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
        // multicastLock.release();
    }

    @Override // TcpControllerServer.MessageListener
    // i.e. message sent from player to host
    public void onMessage(String playerId, String message) {
        Log.d(TAG, "from player: " + playerId + " message: " + message);
        if (message.startsWith(ANSWER)) {
            String answer = message.split("\\|", 3)[2];
            /*if (BuildConfig.DEBUG)*/
            namesAnswers.put(playerId, answer);
            answersCt++;
            if (answersCt >= (namesAnswers.size())) {
                Log.d(TAG, "all answers received for current question");
                String nextMessage = getAllAnswersMessage();
                server.sendToAll(nextMessage);
            }
        } else if (message.startsWith(VOTE)) {
            String answer = message.split("\\|", 3)[2];
            // TODO: send real data!
            votesCt++;
            if (votesCt >= (namesAnswers.size())) {
                Log.d(TAG, "all votes received for current question");
                String scoresMessage = getScoresMessage();
                server.sendToAll(scoresMessage);
            }
        } else {
            Log.d(TAG, "unrecognised message received by host: " + message);
        }
    }

    private String getScoresMessage() {
        // SCORES|3|2|John 2|Julie 4
        StringBuffer buffer = new StringBuffer(SCORES + '|' + 2); // 2 is wrong!
        int i = 1;
        for (String name: namesAnswers.values()) {
            buffer.append('|');
            buffer.append(name + i++);
        }
        return buffer.toString();
    }

    /*
    create a list of names, shuffle the list and then
     create message: String to hold the answers
     */
    private String getAllAnswersMessage() {
        Set<String> nameSet = namesAnswers.keySet();
        if (shuffledNameList.size() == 0) {
            for (String name : nameSet) {
                shuffledNameList.add(name);
            }
        }
        Collections.shuffle(shuffledNameList);
        StringBuffer buffer = new StringBuffer(ALL_ANSWERS + "|" + questionIndex);
        for (String name: shuffledNameList) {
            buffer.append("|" + namesAnswers.get(name));
        }
        return buffer.toString();
    }

    @Override // TcpControllerServer.MessageListener
    public void onPlayerConnected(String playerId) {
        Log.d(TAG, "Player joined: " + playerId);
        namesAnswers.put(playerId, "");
    }

    @Override // TcpControllerServer.MessageListener
    public void onPlayerDisconnected(String playerId) {
        Log.d(TAG, "Player left: " + playerId);
        namesAnswers.remove(playerId);
    }

    @Override // TcpControllerServer.MessageListener
    public void onServerStarted() {
        runOnUiThread(() -> {
            joinGame();
            nextQuestionButton.setEnabled(true);
        });

    }

    @Override // TcpPlayerClient.Listener
    public void onConnected() {
        setStatus("Now connected to the game host");
    }

    private void setStatus(String status) {
        Log.d(TAG, status);
        this.statusTextView.setText(status);
    }

    @Override // TcpPlayerClient.Listener
    // i.e. message sent from host to player
    public void onMessage(String message) {
        Log.d(TAG, "from host to player: " + playerName + " onMessage(" + message + ")");
        runOnUiThread(new Runnable() {
            public void run() {
                if (message.startsWith(ALL_ANSWERS)) {
                    // ALL_ANSWERS|2|dollop|a pig trough
                    int indexOf3rdField = ALL_ANSWERS.length() + 1;
                    int indexOfFirstAnswer = message.indexOf('|', indexOf3rdField) + 1;
                    String[] answers = message.substring(indexOfFirstAnswer).split("\\|");
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragmentContainerView, answersFragment, ANSWER);
                    transaction.commit();
                    currentFragmentTag = ANSWER;
                    answersFragment.showAnswers(answers);
                } else if (message.startsWith(QUESTION)) {
                    String question = message.split("\\|", 3)[2];
                    if (!currentFragmentTag.equals(MAIN)) {
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.fragmentContainerView, mainFragment, MAIN);
                        transaction.commit();
                        currentFragmentTag = MAIN;
                    }
                    mainFragment.setOutputView(question);
                } else if (message.startsWith(SCORES)) {
                    // SCORES|3|2|John 2|Julie 4 - 3rd field is index of correct answer
                    String question = message.split("\\|", 3)[10];
                    if (!currentFragmentTag.equals(SCORES)) {
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.fragmentContainerView, scoresFragment, SCORES);
                        transaction.commit();
                        currentFragmentTag = SCORES;
                    }
                    Results results = new Results("mild", "Guinness", tempPlayers);
                    scoresFragment.showScores(results);
                } else {
                    Log.d(TAG, "unrecognised message received by player: " + message);
                }
            }
        });
    }
    Player[] tempPlayers = {
            new Player(3, "mild", "john"),
            new Player(4, "bitter", "Joe"),
            new Player(2, "wine", "Julie")
    };

    @Override // View.OnClickListener
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.hostButton || viewId == R.id.joinButton) {
            playerName = mainFragment.getPlayerName();
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
                 if (!isHost) {
                     nextQuestionButton.setVisibility(GONE);
                 }
                 joinGame();
            }
        } else if (viewId == R.id.nextQuestionButton) {
            getNextQuestion();
        } else if (viewId == R.id.sendButton) {
            String answer = mainFragment.getAnswerEditText();
            client.sendAnswer(round, answer);
        } else {
            Toast.makeText(this, "unknown button pressed: " + view,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void joinGame() {
        hostButton.setEnabled(false);
        joinButton.setEnabled(false);
        String name = this.playerName;
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
                Log.i(TAG, "controllerAddress: " + controllerAddress);
            }
        }).start();
    }


    @Override // OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick(position=" + position);
        String name, answer;
        name = shuffledNameList.get(position);
        answer = namesAnswers.get(name);
        Log.d(TAG, "vote: name=" + name + ", answer=" + answer);
        Log.d(TAG, "correct answer=" + namesAnswers.get(CORRECT));
        client.sendVote(round, position);
        /*
        if answer is correct, add 1 to name's score
        else add 1 to score of person she voted for
         */
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
    private int questionIndex = 0;
    private void getNextQuestion() {
        // temporary until we get a proper database!
        if (questionIndex >= questions.length) questionIndex = 0;
        currentQuestionAnswer = questions[questionIndex++];
        // end of temporary
        String nextQuestion = QUESTION + "|" + round++ +"|" +currentQuestionAnswer.question;
        namesAnswers.clear();
        namesAnswers.put(CORRECT, currentQuestionAnswer.answer);
        answersCt = 1;
        server.sendToAll(nextQuestion);
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