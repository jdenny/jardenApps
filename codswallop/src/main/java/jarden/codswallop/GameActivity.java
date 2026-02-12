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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import jarden.quiz.EndOfQuestionsException;
import jarden.tcp.TcpControllerServer;
import jarden.tcp.TcpPlayerClient;

/* TODO next:
 can the views go in the middle of the screen, and expand as necessary?
 separate classes Activity.player; Activity.host
 in landscape mode, show question and answer side by side
 */

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
 Host selects “Send Host Address”; when each player receives the host address, it joins the game.

 Server gets next question from dictionary and sends to other devices
 All players, including Server, see the question; supply their answer, which is sent to Server
 When Server has the answers, including the real one, it sends them to all Clients, in random order
 Players give their votes; when all votes in, Server highlights the real answer
 Initial dialog:
    PlayerNameEditText; HostButton; JoinButton
 three screens (Fragments):
 1  NextButton, SendHostAddressButton (host only)
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
 */
public class GameActivity extends AppCompatActivity implements
        TcpControllerServer.MessageListener, View.OnClickListener,
        AdapterView.OnItemClickListener, TcpPlayerClient.Listener,
        LoginDialogFragment.LoginDialogListener, ConfirmExitDialogFragment.ExitDialogListener {
    public static final String TAG = "GameActivity";
    private static final String QUESTION = "QUESTION";
    private static final String ANSWER = "ANSWER";
    private static final String ALL_ANSWERS = "ALL_ANSWERS";
    private static final String NAMED_ANSWERS = "NAMED_ANSWERS";
    /*!!
    private static final String MAIN = "MAIN";

     */
    private static final String CORRECT = "CORRECT";
    private static final String VOTE = "VOTE";
    private static final String LOGIN_DIALOG = "LOGIN_DIALOG";
    private static final String QUESTION_SEQUENCE_KEY = "QUESTION_SEQUENCE_KEY";

    // Host fields: ***************************
    private Map<String, Player> players;
    private QuestionManager questionManager;
    QuestionManager.QuestionAnswer currentQA;
    private Button nextQuestionButton;
    private TextView statusTextView;
    private TcpControllerServer tcpControllerServer;
    private int answersCt;
    private int votesCt;
    private final List<String> shuffledNameList = new ArrayList<>();
    private View hostButtonsLayout;
    private SharedPreferences sharedPreferences;
    private int questionSequence;
    private boolean voteCast;

    // Player fields ***************************
    private TcpPlayerClient tcpPlayerClient;
    private String currentQuestion;
    // Host & Client fields ***************************
    private String currentFragmentTag = QUESTION;
    private String playerName;
    private FragmentManager fragmentManager;
    private AnswersFragment answersFragment;
    private QuestionFragment questionFragment;
    private boolean isHost;
    private OnBackPressedCallback backPressedCallback;
    private GameViewModel gameViewModel;
    private AnswersViewModel answersViewModel;
    private QuestionViewModel questionViewModel;

    @Override // Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            String message = "onCreate(" + ((savedInstanceState == null) ? "null" : "not null") + ")";
            Log.d(TAG, message);
        }
        setContentView(R.layout.activity_game);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        nextQuestionButton.setOnClickListener(this);
        Button sendHostAddressButton = findViewById(R.id.broadcastHostButton);
        sendHostAddressButton.setOnClickListener(this);
        statusTextView = findViewById(R.id.statusView);
        hostButtonsLayout = findViewById(R.id.hostButtonsLayout);
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ConfirmExitDialogFragment dialog = new ConfirmExitDialogFragment();
                dialog.show(getSupportFragmentManager(), "ConfirmExitDialogFragment");
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
        this.fragmentManager = getSupportFragmentManager();
        LoginDialogFragment loginDialog;
        questionViewModel = new ViewModelProvider(this).get(QuestionViewModel.class);
        answersViewModel = new ViewModelProvider(this).get(AnswersViewModel.class);
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        tcpControllerServer = gameViewModel.getTcpControllerServer();
        if (tcpControllerServer != null) {
            isHost = true;
            players = gameViewModel.getPlayers();
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "isHost=" + isHost);
        }
        if (savedInstanceState == null) {
            loginDialog = new LoginDialogFragment();
            questionFragment = new QuestionFragment();
            answersFragment = new AnswersFragment();
            loginDialog.show(fragmentManager, LOGIN_DIALOG);
        } else {
            questionFragment = (QuestionFragment) fragmentManager.findFragmentByTag(QUESTION);
            answersFragment = (AnswersFragment) fragmentManager.findFragmentByTag(ALL_ANSWERS);
            if (answersFragment == null) {
                answersFragment = new AnswersFragment();
            }
            currentFragmentTag = gameViewModel.getCurrentFragmentTag();
            voteCast = gameViewModel.getVoteCast();
            if (isHost) {
                answersCt = gameViewModel.getAnswersCt();
                votesCt = gameViewModel.getVotesCt();
                questionSequence = gameViewModel.getQuestionSequence();
                currentQuestion = gameViewModel.getCurrentQuestion();
                hostButtonsLayout.setVisibility(View.VISIBLE);
            }
        }
        Fragment currentFragment = (currentFragmentTag == QUESTION) ? questionFragment : answersFragment;
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.fragmentContainerView, currentFragment, currentFragmentTag);
        ft.commit();
        questionManager = new QuestionManager(this);
        sharedPreferences = getPreferences(Context.MODE_PRIVATE);
        tcpPlayerClient = gameViewModel.getTcpPlayerClient();
        tcpPlayerClient.listenForHostBroadcast(this, this);
        playerName = tcpPlayerClient.getPlayerName();
    }
    @Override // Activity
    protected void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDestroy()");
        }
        super.onDestroy();
    }
    @Override // ConfirmExitDialogFragment.ExitDialogListener
    public void onExitDialogConfirmed() {
        backPressedCallback.setEnabled(false); // DON'T FORGET THIS!
        getOnBackPressedDispatcher().onBackPressed();
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
                setStatus("waiting for " + (players.size() - answersCt) + " player(s) to answer");
            }
        } else if (message.startsWith(VOTE)) {
            String index = message.split("\\|", 3)[2];
            int indexOfVotedItem = Integer.parseInt(index);
            String votedForName = shuffledNameList.get(indexOfVotedItem);
            if (votedForName.equals(CORRECT)) {
                players.get(playerName).incrementScore();
            } else if (!votedForName.equals(playerName)) {
                players.get(votedForName).incrementScore();
            }
            votesCt++;
            if ((votesCt) >= (players.size())) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "all votes received for current question");
                }
                String allAnswers2Message = getNamedAnswersMessage();
                tcpControllerServer.sendToAll(allAnswers2Message);
            } else {
                setStatus("waiting for " + (players.size() - votesCt) + " player(s) to vote");
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "unrecognised message received by host: " + message);
            }
        }
    }

    /*
    create a list of names, shuffle the list and then
     create message: String to hold the answers
     */
    private String getAllAnswersMessage() {
        shuffledNameList.clear();
        shuffledNameList.add(CORRECT);
        shuffledNameList.addAll(players.keySet());
        Collections.shuffle(shuffledNameList);
        StringBuffer buffer = new StringBuffer(ALL_ANSWERS + '|' + questionSequence);
        for (String name: shuffledNameList) {
            if (name.equals(CORRECT)) {
                buffer.append('|' + currentQA.answer);
            } else {
                buffer.append('|' + players.get(name).getAnswer());
            }
        }
        return buffer.toString();
    }
    private String getNamedAnswersMessage() {
        // NAMED_ANSWERS|3|CORRECT: Norway's most famous sculptor|Joe (2): Centre forward for Liverpool
        StringBuffer buffer = new StringBuffer(NAMED_ANSWERS + '|' + questionSequence);
        buffer.append('|' + CORRECT + ": " + currentQA.answer);
        if (currentQA.comment != null) {
            buffer.append(". " + currentQA.comment);
        }
        for (Player player: players.values()) {
            buffer.append('|' + player.getName() + " (" + player.getScore() + ')' +
                    ": " + player.getAnswer());
        }
        return buffer.toString();
    }
    @Override // TcpControllerServer.MessageListener
    public void onPlayerConnected(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Player joined: " + playerName);
        }
        players.put(playerName, new Player(playerName, "not supplied", 0));
        runOnUiThread(() -> statusTextView.setText(playerName + " has joined; " + players.size() +
                " players so far"));
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
        runOnUiThread(() -> {
            Log.d(TAG, "Now connected to the game host");
            statusTextView.setText("Now connected to the game host; wait for the first question");
        });
    }

    private void setStatus(String status) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, status);
        }
        runOnUiThread(() -> statusTextView.setText(status));
    }
    private void showAnswers(String message) {
        int index = message.indexOf('|');
        int indexOfFirstAnswer = message.indexOf('|', index + 1) + 1;
        String[] answers = message.substring(indexOfFirstAnswer).split("\\|");
        List<String> answersList = Arrays.asList(answers);
        setFragment(answersFragment, ALL_ANSWERS);
        answersViewModel.setAnswersState(
                new AnswersState(currentQuestion, answersList));
    }
    @Override // TcpPlayerClient.Listener
    // i.e. message sent from host to player
    public void onMessage(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "from host to player: " + playerName + " onMessage(" + message + ")");
        }
        runOnUiThread(() -> {
            if (message.startsWith(ALL_ANSWERS)) {
                // ALL_ANSWERS|2|a pig trough|dollop|
                showAnswers(message);
                voteCast = false;
                statusTextView.setText("tap on the answer you think is correct");
            } else if (message.startsWith(NAMED_ANSWERS)) {
                // NAMED_ANSWERS|3|CORRECT: Norway's most famous sculptor|Joe (2): Centre forward for Liverpool
                showAnswers(message);
                statusTextView.setText("Who said what");
            } else if (message.startsWith(QUESTION)) {
                String[] tqa = message.split("\\|", 4);
                currentQuestion = tqa[1] + ". " + tqa[2] + ": " + tqa[3];
                setFragment(questionFragment, QUESTION);
                questionViewModel.setAnswerState(currentQuestion);
                statusTextView.setText("supply answer and Send");
            } else {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "unrecognised message received by player: " + message);
                }
            }
        });
    }
    private void setFragment(Fragment fragment, String fragmentTag) {
        if (!currentFragmentTag.equals(fragmentTag)) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainerView, fragment, fragmentTag);
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
            String answer = questionFragment.getAnswerEditText();
            tcpPlayerClient.sendAnswer(questionSequence, answer);
            statusTextView.setText("waiting for other players to answer");
        } else if (viewId == R.id.broadcastHostButton) { // Host only
            tcpControllerServer.sendHostBroadcast(this);
            nextQuestionButton.setEnabled(true);
            statusTextView.setText("wait for all players to join, then 'Next'");
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
        if (!voteCast) {
            tcpPlayerClient.sendVote(questionSequence, String.valueOf(position));
            voteCast = true;
            statusTextView.setText("Waiting for other players to vote");
        } else {
            statusTextView.setText("You have already cast your vote; you can't change your mind!");
        }
    }
    @Override // LoginDialogListener
    public void onHostButton(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onHostButton(" + playerName + ')');
        }
        players = new ConcurrentHashMap<>();
        gameViewModel.setPlayers(players);
        this.playerName = playerName;
        tcpControllerServer = gameViewModel.getTcpControllerServer();
        if (tcpControllerServer == null) {
            tcpControllerServer = new TcpControllerServer(this);
            gameViewModel.setTcpControllerServer(tcpControllerServer);
            tcpControllerServer.start();
        }
        isHost = true;
        hostButtonsLayout.setVisibility(View.VISIBLE);
        statusTextView.setText("when all players have logged in (using 'Join'), Broadcast Host");
    }
    @Override // LoginDialogListener
    public void onJoinButton(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onJoinButton(" + playerName + ')');
        }
        this.playerName = playerName;
    }
    public int getQuestionSequence(boolean reset) {
        questionSequence = reset ? -1 : sharedPreferences.getInt(QUESTION_SEQUENCE_KEY, -1);
        if (questionSequence == -1 && BuildConfig.DEBUG) {
            Log.w(TAG, "getQuestionSequence() returning -1");
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(QUESTION_SEQUENCE_KEY, ++questionSequence);
        editor.apply();
        return questionSequence;
    }
    private void getNextQuestion() {
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
        answersCt = 0;
        votesCt = 0;
        tcpControllerServer.sendToAll(nextQuestion);
    }
    @Override // TcpPlayerClient.Listener
    public void onDisconnected() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Now disconnected from the game server");
        }
        //?? waitForHost(); // await a new host!
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
        tcpPlayerClient.connect(hostIp, TcpControllerServer.TCP_PORT,
                playerName, this);
    }
    @Override // Activity
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onSaveInstanceState(); currentFragmentTag=" +
                    currentFragmentTag);
        }
        gameViewModel.setCurrentFragmentTag(currentFragmentTag);
        gameViewModel.setVoteCast(voteCast);
        if (isHost) {
            gameViewModel.setAnswersCt(answersCt);
            gameViewModel.setVotesCt(votesCt);
            gameViewModel.setQuestionSequence(questionSequence);
            gameViewModel.setCurrentQuestion(currentQuestion);
        }
        super.onSaveInstanceState(savedInstanceState);
    }
}