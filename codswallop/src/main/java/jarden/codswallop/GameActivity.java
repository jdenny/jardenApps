package jarden.codswallop;

import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.concurrent.ConcurrentHashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import jarden.quiz.EndOfQuestionsException;
import jarden.tcp.TcpControllerServer;
import jarden.tcp.TcpPlayerClient;

/** Design of application
Message Protocol:
 Host to all Players using broadcast:
    HOST_ANNOUNCE|192.168.0.12|50001
 Host to all Players using Tcp:
    QUESTION|3|Who was Gustav Vigeland?
    ALL_ANSWERS|3|Norway's most famous sculptor|Centre forward for Liverpool
    NAMED_ANSWERS|3|CORRECT|Norway's most famous sculptor|Joe|Centre forward for Liverpool
    SCORES|3|John 2|Julie 4
 Player to Host
    ANSWER|3|Centre forward for Liverpool
    VOTE|3|indexOfSelectedAnswer

 Players agree who will be host; all open the app; all login: name, host or join.
 Host selects “send IP Address”; when each player receives the host address, it joins the game.

 Server gets next question from dictionary and sends to other devices
 All players, including Server, see the question; supply their answer, which is sent to Server
 When Server has the answers, including the real one, it sends them to all Clients, in random order
 Players give their votes; when all votes in, Server highlights the real answer
 Initial dialog:
    PlayerNameEditText; HostButton; JoinButton
 three screens (Fragments):
 1  NextButton, ScoresButton, SendIPAddressButton (host only)
    QuestionTextView
    YourAnswerEditText
    SendButton (initially disabled)
 HostGame: disable HostGame; startServer(); after serverStarted: joinGame(); enable Next
 JoinGame: disable HostGame & JoinGame; enable Send.

 2  list of:
        optionNumber, answer (players click row to vote)
    when all votes in, list changes to
        "Correct" correct answer
        playerName answer

 3  list of:
        playerName, score (goes to first screen when host types NextButton)


 TODO next:
 Use statusText to talk players through what is happening
 On Back pressed: are you sure you want to exit?
 Show extra field on named answers
 On host, on click of "Send Ip Address" button, call TcpControllerServer.sendHostBroadcast()
 On join, wait callback from TcpPlayerClient.listenForHostBroadcast()
 can the views go in the middle of the screen, and expand as necessary?
 separate classes Activity.client; Activity host
 in landscape mode, show question and answer side by side
 */
public class GameActivity extends AppCompatActivity implements
        TcpControllerServer.MessageListener, View.OnClickListener,
        AdapterView.OnItemClickListener, TcpPlayerClient.Listener,
        LoginDialogFragment.LoginDialogListener {
    private static final String TAG = "GameActivity";
    private static final String QUESTION = "QUESTION";
    private static final String ANSWER = "ANSWER";
    private static final String ALL_ANSWERS = "ALL_ANSWERS";
    private static final String NAMED_ANSWERS = "NAMED_ANSWERS";
    private static final String MAIN = "MAIN";
    private static final String CORRECT = "CORRECT";
    private static final String VOTE = "VOTE";
    private static final String SCORES = "SCORES";
    private static final String LOGIN_DIALOG = "LOGIN_DIALOG";

    // Host fields: ***************************
    private final Map<String, Player> players =
            new ConcurrentHashMap<>();
    private QuestionManager questionManager;
    private Button nextQuestionButton;
    private Button scoresButton;
    private TextView statusTextView;
    private TcpControllerServer tcpControllerServer;
    private int answersCt;
    private int votesCt;
    private final List<String> shuffledNameList = new ArrayList<>();
    private View hostButtonsLayout;
    private SharedPreferences sharedPreferences;
    private final String questionSequenceKey = "QUESTION_SEQUENCE_KEY";
    private int questionSequence;
    // Player fields ***************************
    private TcpPlayerClient tcpPlayerClient;
    // Host & Client fields ***************************
    private String currentFragmentTag = MAIN;
    private String playerName;
    private FragmentManager fragmentManager;
    private AnswersFragment answersFragment;
    private MainFragment mainFragment;
    private ScoresDialogFragment scoresFragment;
    private boolean isHost;

    @Override // Activity
    public void onResume() {
        super.onResume();
    }

    @Override // Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.fragmentManager = getSupportFragmentManager();
        if (savedInstanceState == null) {
            mainFragment = new MainFragment();
            answersFragment = new AnswersFragment();
            scoresFragment = new ScoresDialogFragment();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.add(R.id.fragmentContainerView, this.mainFragment, MAIN);
            ft.commit();
        } else {
            mainFragment = (MainFragment) fragmentManager.findFragmentByTag(MAIN);
            answersFragment = (AnswersFragment) fragmentManager.findFragmentByTag(ALL_ANSWERS);
            scoresFragment = (ScoresDialogFragment) fragmentManager.findFragmentByTag(SCORES);
        }
        /*?
        isHost = getIntent().getBooleanExtra("HOST", false);
        if (isHost) {
            server = new TcpControllerServer(this);
            server.start();
        }
         */
        setContentView(R.layout.activity_game);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        nextQuestionButton.setOnClickListener(this);
        scoresButton = findViewById(R.id.scoresButton);
        scoresButton.setOnClickListener(this);
        Button sendIpAddressButton = findViewById(R.id.sendIPButton);
        sendIpAddressButton.setOnClickListener(this);
        statusTextView = findViewById(R.id.statusView);
        LoginDialogFragment loginDialog = new LoginDialogFragment();
        loginDialog.show(fragmentManager, LOGIN_DIALOG);
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "isHost=" + isHost);
        }
        hostButtonsLayout = findViewById(R.id.hostButtons);
        questionManager = new QuestionManager(this);
        sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);
    }
    @Override // Activity
    protected void onDestroy() {
        super.onDestroy();
        if (tcpControllerServer != null) tcpControllerServer.stop();
        if (tcpPlayerClient != null) tcpPlayerClient.disconnect();
        // multicastLock.release();
    }

    @Override // TcpControllerServer.MessageListener
    // i.e. message sent from player to host
    public void onMessage(String playerName, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "from player: " + playerName + " message: " + message);
        }
        if (message.startsWith(ANSWER)) {
            String answer = message.split("\\|", 3)[2];
            players.get(playerName).setAnswer(answer);
            answersCt++;
            if (answersCt >= (players.size())) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "all answers received for current question");
                }
                String nextMessage = getAllAnswersMessage();
                tcpControllerServer.sendToAll(nextMessage);
            } else {
                setStatus("waiting for " + (players.size() - answersCt) + " players to answer");
            }
        } else if (message.startsWith(VOTE)) {
            String index = message.split("\\|", 3)[2];
            int indexOfVotedItem = Integer.parseInt(index);
            String votedForName = shuffledNameList.get(indexOfVotedItem);
            if (votedForName.equals(CORRECT)) {
                players.get(playerName).incrementScore();
            } else if (!votedForName.equals(playerName)) { // I only vote for myself during development!
                players.get(votedForName).incrementScore();
            }
            votesCt++;
            if ((votesCt) >= (players.size())) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "all votes received for current question");
                }
                String allAnswers2Message = getAllAnswers2Message();
                tcpControllerServer.sendToAll(allAnswers2Message);
            } else {
                setStatus("waiting for " + (players.size() - votesCt) + " players to vote");
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "unrecognised message received by host: " + message);
            }
        }
    }

    private String getAllAnswers2Message() {
        // NAMED_ANSWERS|3|CORRECT|Norway's most famous sculptor|Joe|Centre forward for Liverpool
        StringBuffer buffer = new StringBuffer(NAMED_ANSWERS + "|" + questionSequence);
        for (Player player: players.values()) {
            buffer.append("|" + player.getName() + "|" + player.getAnswer());
        }
        return buffer.toString();
    }

    private String getScoresMessage() {
        // SCORES|3|John 2|Julie 4
        int correctAnswerIndex = 2; // bodge!
        StringBuffer buffer = new StringBuffer(SCORES + '|' + questionSequence);
        for (Player player : players.values()) {
            buffer.append('|' + player.getName() + ": " + player.getScore());
        }
        return buffer.toString();
    }

    /*
    create a list of names, shuffle the list and then
     create message: String to hold the answers
     */
    private String getAllAnswersMessage() {
        shuffledNameList.clear();
        for (String name : players.keySet()) {
            shuffledNameList.add(name);
        }
        Collections.shuffle(shuffledNameList);
        StringBuffer buffer = new StringBuffer(ALL_ANSWERS + "|" + questionSequence);
        for (String name: shuffledNameList) {
            buffer.append("|" + players.get(name).getAnswer());
        }
        return buffer.toString();
    }

    @Override // TcpControllerServer.MessageListener
    public void onPlayerConnected(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Player joined: " + playerName);
        }
        players.put(playerName, new Player(playerName, "not supplied", 0));
        runOnUiThread(() -> {
            statusTextView.setText(playerName + " has joined; " + players.size() +
                    " players so far");
        });
    }

    @Override // TcpControllerServer.MessageListener
    public void onPlayerDisconnected(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Player left: " + playerName);
        }
        players.remove(playerName);
    }

    @Override // TcpControllerServer.MessageListener
    public void onServerStarted() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onServerStarted()");
        }
    }

    @Override // TcpPlayerClient.Listener
    public void onConnected() {
//        Toast.makeText(this, "Now connected to the game host",
//                Toast.LENGTH_LONG).show();
        Log.d(TAG, "Now connected to the game host");
    }

    private void setStatus(String status) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, status);
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                statusTextView.setText(status);
            }
        });
    }

    @Override // TcpPlayerClient.Listener
    // i.e. message sent from host to player
    public void onMessage(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "from host to player: " + playerName + " onMessage(" + message + ")");
        }
        runOnUiThread(new Runnable() {
            public void run() {
                if (message.startsWith(ALL_ANSWERS)) {
                    // ALL_ANSWERS|2|dollop|a pig trough
                    int indexOf3rdField = ALL_ANSWERS.length() + 1;
                    int indexOfFirstAnswer = message.indexOf('|', indexOf3rdField) + 1;
                    String[] answers = message.substring(indexOfFirstAnswer).split("\\|");
                    setFragment(answersFragment, ANSWER);
                    answersFragment.setOnItemClickListener(GameActivity.this);
                    answersFragment.showAnswers(answers, false);
                } else if (message.startsWith(NAMED_ANSWERS)) {
                    // NAMED_ANSWERS|3|CORRECT|Norway's most famous sculptor|Joe|Centre forward for Liverpool
                    int indexOf3rdField = NAMED_ANSWERS.length() + 1;
                    int indexOfFirstAnswer = message.indexOf('|', indexOf3rdField) + 1;
                    String[] answers = message.substring(indexOfFirstAnswer).split("\\|");
                    setFragment(answersFragment, ANSWER);
                    answersFragment.showAnswers(answers, true);
                } else if (message.startsWith(QUESTION)) {
                    String[] tqa = message.split("\\|", 4);
                    String question = tqa[2] + ": " + tqa[3];
                    setFragment(mainFragment, MAIN);
                    mainFragment.setOutputView(question);
                    mainFragment.enableSendButton(true);
                } else if (message.startsWith(SCORES)) {
                    // SCORES|3|John 2|Julie 4
                    int indexOf3rdField = SCORES.length() + 1;
                    int indexOfFirstScore = message.indexOf('|', indexOf3rdField) + 1;
                    String[] scores = message.substring(indexOfFirstScore).split("\\|");
                    scoresFragment.show(fragmentManager, SCORES);
                    scoresFragment.showScores(scores);
                } else {
                    if (BuildConfig.DEBUG) {
                        Log.d(TAG, "unrecognised message received by player: " + message);
                    }
                }
            }
        });
    }

    private void setFragment(Fragment answersFragment, String fragmentTag) {
        if (!currentFragmentTag.equals(fragmentTag)) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainerView, answersFragment, fragmentTag);
            transaction.commit();
            currentFragmentTag = fragmentTag;
        }
    }

    @Override // View.OnClickListener
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.nextQuestionButton) { // Host only
            getNextQuestion();
            statusTextView.setText("waiting for all players to answer");
        } else if (viewId == R.id.sendButton) { // Player
            String answer = mainFragment.getAnswerEditText();
            tcpPlayerClient.sendAnswer(questionSequence, answer);
            mainFragment.enableSendButton(false);
        } else if (viewId == R.id.scoresButton) { // Host only
            String scoresMessage = getScoresMessage();
            tcpControllerServer.sendToAll(scoresMessage);
        } else if (viewId == R.id.sendIPButton) {
            tcpControllerServer.sendHostBroadcast(this);
            nextQuestionButton.setEnabled(true);
            scoresButton.setEnabled(true);
        } else {
            Toast.makeText(this, "unknown button pressed: " + view,
                    Toast.LENGTH_LONG).show();
        }
    }

    @Override // OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onItemClick(position=" + position + ')');
        }
        tcpPlayerClient.sendVote(questionSequence, String.valueOf(position));
    }

    @Override // LoginDialogListener
    public void onHostButton(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onHostButton(" + playerName + ')');
        }
        this.playerName = playerName;
        tcpControllerServer = new TcpControllerServer(this);
        tcpControllerServer.start();
        isHost = true;
        hostButtonsLayout.setVisibility(View.VISIBLE);
        statusTextView.setText("when all players have joined, click Next");
        TcpPlayerClient.listenForHostBroadcast(this, this);
    }

    @Override // LoginDialogListener
    public void onJoinButton(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onJoinButton(" + playerName + ')');
        }
        this.playerName = playerName;
        TcpPlayerClient.listenForHostBroadcast(this, this);
    }
    public int getQuestionSequence(boolean reset) {
        questionSequence = reset ? -1 : sharedPreferences.getInt(questionSequenceKey, -1);
        if (questionSequence == -1 && BuildConfig.DEBUG) {
            Log.w(TAG, "getQuestionSequence() returning -1");
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(questionSequenceKey, ++questionSequence);
        editor.apply();
        return questionSequence;
    }

    private void getNextQuestion() {
        QuestionManager.QuestionAnswer currentQA;
        try {
            currentQA = questionManager.getNext(getQuestionSequence(false));
        } catch (EndOfQuestionsException e) {
            try {
                currentQA = questionManager.getNext(getQuestionSequence(true));
            } catch (EndOfQuestionsException ex) {
                throw new RuntimeException(ex);
            }
        }
        String nextQuestion = QUESTION + '|' + questionSequence + '|' + currentQA.type + '|' + currentQA.question;
        players.put(CORRECT, new Player(CORRECT, currentQA.answer, 0));
        answersCt = 1;
        votesCt = 1;
        tcpControllerServer.sendToAll(nextQuestion);
    }

    @Override // TcpPlayerClient.Listener
    public void onDisconnected() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Now disconnected from the game server");
        }
    }

    @Override // TcpPlayerClient.Listener
    public void onError(Exception e) {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, e.toString());
        }
    }

    @Override // TcpPlayerClient.HostFoundCallback
    public void onHostFound(String hostIp, int port) {
        String status = "onHostFound(" + hostIp + "' " + port + ')';
        if (BuildConfig.DEBUG) {
            Log.d(TAG, status);
        }
        runOnUiThread(() -> {
            Toast.makeText(this, status, Toast.LENGTH_LONG).show();
            tcpPlayerClient = new TcpPlayerClient(hostIp, TcpControllerServer.TCP_PORT,
                    playerName, this);
            tcpPlayerClient.connect();

            if (isHost) {
                nextQuestionButton.setEnabled(true);
            }
        });
    }
}