package jarden.codswallop;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import jarden.quiz.EndOfQuestionsException;
import jarden.tcp.TcpControllerServer;
import jarden.tcp.TcpPlayerClient;
import jarden.tcp.TcpService;

import static jarden.codswallop.Constants.ALL_ANSWERS;
import static jarden.codswallop.Constants.ANSWER;
import static jarden.codswallop.Constants.CORRECT;
import static jarden.codswallop.Constants.GAME_PREFS;
import static jarden.codswallop.Constants.HostState;
import static jarden.codswallop.Constants.NAMED_ANSWERS;
import static jarden.codswallop.Constants.PlayerState;
import static jarden.codswallop.Constants.QUESTION;
import static jarden.codswallop.Constants.QUESTION_SEQUENCE_KEY;
import static jarden.codswallop.Constants.VOTE;
/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class GameViewModel extends AndroidViewModel implements TcpControllerServer.ServerListener,
        TcpPlayerClient.Listener {

    private final MutableLiveData<AllAnswers> answersLiveData =
            new MutableLiveData<>(new AllAnswers(null, null, false));
    private final MutableLiveData<String> currentFragmentTagLiveData =
            new MutableLiveData<>(QUESTION);
    private QuestionManager.QuestionAnswer currentQA;
    private String currentQuestion;
    private final MutableLiveData<Boolean> awaitingAnswerLiveData =
            new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> awaitingVoteLiveData =
            new MutableLiveData<>(false);
    private final MutableLiveData<HostState> hostStateLiveData =
            new MutableLiveData<>(HostState.AWAITING_PLAYERS);
    private boolean isHost;
    private String playerName;
    private Map<String, Player> players;
    private Map<String, Player> leftPlayers;
    private final MutableLiveData<PlayerState> playerStateLiveData =
            new MutableLiveData<>(PlayerState.AWAITING_HOST_IP);
    private final MutableLiveData<String> questionLiveData =
            new MutableLiveData<>("");
    private QuestionManager questionManager;
    private int questionSequence;
    private final List<String> shuffledNameList = new ArrayList<>();
    private int correctShuffledIndex;
    private final static String TAG = "GameViewModel";
    //!! private final TcpPlayerClient tcpPlayerClient = new TcpPlayerClient();
    //!! private TcpControllerServer tcpControllerServer;
    private String lastJoinedPlayerName;
    private final SharedPreferences prefs;
    private final MutableLiveData<Exception> exceptionLiveData =
            new MutableLiveData<>(null);
    private TcpService tcpService;

    public GameViewModel(@NotNull Application application) {
        super(application);
        prefs = application.getSharedPreferences(
                GAME_PREFS, Context.MODE_PRIVATE);
    }
    public void attachService(TcpService tcpService) {
        this.tcpService = tcpService;
    }
    public LiveData<Exception> getExceptionLiveData() {
        return exceptionLiveData;
    }
    public LiveData<Boolean> getAwaitingAnswerLiveData() {
        return awaitingAnswerLiveData;
    }
    public LiveData<PlayerState> getPlayerStateLiveData() {
        return playerStateLiveData;
    }
    public void setPlayerStateLiveData(PlayerState playerState) {
        playerStateLiveData.setValue(playerState);
    }
    public void addPlayer(String name, Player player) {
        players.put(name, player);
    }
    public LiveData<String> getCurrentFragmentTagLiveData() {
        return currentFragmentTagLiveData;
    }
    public void setQuestionLiveData(String question) {
        if (question != null && !question.isEmpty()) {
            questionLiveData.setValue(question);
            playerStateLiveData.setValue(PlayerState.SUPPLY_ANSWER);
        } else {
            Log.e(TAG, "setQuestion(" + question + ')');
        }
    }
    public LiveData<String> getQuestionLiveData() {
        return questionLiveData;
    }
    public void setAnswer(String answer) {
        if (answer != null && !answer.isEmpty()) {
            /*!!tcpPlayerClient*/tcpService.sendAnswer(questionSequence, answer);
            awaitingAnswerLiveData.setValue(false);
            setPlayerStateLiveData(PlayerState.AWAITING_ANSWERS);
        }
    }
    public void setAnswersLiveData(AllAnswers allAnswers) {
        answersLiveData.setValue(allAnswers);
    }
    public LiveData<AllAnswers> getAnswersLiveData() {
        return answersLiveData;
    }
    public void setSelectedAnswer(int position) {
        /*!!tcpPlayerClient*/tcpService.sendVote(questionSequence, position);
        playerStateLiveData.setValue(PlayerState.AWAITING_VOTES);
    }
    public void setHostStateLiveData(HostState hostState) {
        new Handler(Looper.getMainLooper()).post(() -> {
            this.hostStateLiveData.setValue(hostState);
        });
    }
    public MutableLiveData<HostState> getHostStateLiveData() {
        return hostStateLiveData;
    }
    public void startHost() {
        tcpService.startHosting(playerName, this, this);
        //!! if (tcpControllerServer == null) {
            players = new ConcurrentHashMap<>();
            leftPlayers = new ConcurrentHashMap<>();
            questionManager = new QuestionManager(getApplication().getResources());
            //!! tcpControllerServer = new TcpControllerServer(this);
            //!! tcpControllerServer.start();
            isHost = true;
            questionSequence = prefs.getInt(QUESTION_SEQUENCE_KEY, 0);
        //!! }
    }
    @Override  // TcpControllerServer.MessageListener
    // i.e. message sent from player to host
    public void onMessage(String playerName, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "from player: " + playerName + " message: " + message);
        }
        Player player = players.get(playerName);
        if (message.startsWith(ANSWER)) {
            String answer = message.split("\\|", 3)[2];
            player.setAnswer(answer);
            checkForAllAnswers();
        } else if (message.startsWith(VOTE)) {
            String index = message.split("\\|", 3)[2];
            int indexOfVotedItem = Integer.parseInt(index);
            String votedForName = shuffledNameList.get(indexOfVotedItem);
            if (votedForName.equals(CORRECT)) {
                players.get(playerName).incrementScore();
            } else if (!votedForName.equals(playerName)) {
                players.get(votedForName).incrementVotedForScore();
            }
            player.setVotedIndex(indexOfVotedItem);
            checkForAllVotes();
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "unrecognised message received by host: " + message);
            }
        }
    }
    private void checkForAllVotes() {
        if (getVotesCt() >= (players.size())) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "all votes received for current question");
            }
            /*!!tcpControllerServer*/tcpService.sendToAll(getNamedAnswersMessage());
            setHostStateLiveData(HostState.READY_FOR_NEXT_QUESTION);
        } else {
            setHostStateLiveData(HostState.AWAITING_CT_VOTES);
        }
    }
    private void checkForAllAnswers() {
        if (getAnswersCt() >= (players.size())) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "all answers received for current question");
            }
            String nextMessage = getAllAnswersMessage();
            /*!!tcpControllerServer*/tcpService.sendToAll(nextMessage);
            setHostStateLiveData(HostState.AWAITING_CT_VOTES);
        } else {
            setHostStateLiveData(HostState.AWAITING_CT_ANSWERS);
        }
    }
    private int getVotesCt() {
        int votesCt = 0;
        for (Player playerN : players.values()) {
            if (playerN.getVotedIndex() >= 0) {
                votesCt++;
            }
        }
        return votesCt;
    }
    private int getAnswersCt() {
        int answersCt = 0;
        for (Player playerN : players.values()) {
            if (playerN.getAnswer() != null) {
                answersCt++;
            }
        }
        return answersCt;
    }
    private String getAllAnswersMessage() {
        shuffledNameList.clear();
        shuffledNameList.add(CORRECT);
        shuffledNameList.addAll(players.keySet());
        Collections.shuffle(shuffledNameList);
        StringBuffer buffer = new StringBuffer(ALL_ANSWERS + '|' + questionSequence);
        String name;
        for (int i = 0; i < shuffledNameList.size(); i++) {
            name = shuffledNameList.get(i);
            if (name.equals(CORRECT)) {
                this.correctShuffledIndex = i;
                buffer.append('|' + currentQA.answer);
            } else {
                buffer.append('|' + players.get(name).getAnswer());
            }
        }
        return buffer.toString();
    }
    private String getNamedAnswersMessage() {
        // NAMED_ANSWERS|3|CORRECT|Hungarian physician|Joe|N,1,4|Centre forward for Man Utd|John|Y,0,3|Russian politician
        StringBuffer buffer = new StringBuffer(NAMED_ANSWERS + '|' + questionSequence);
        buffer.append('|' + CORRECT + '|' + currentQA.answer);
        if (currentQA.comment != null) {
            buffer.append(". " + currentQA.comment);
        }
        char isCorrect;
        for (Player player: players.values()) {
            isCorrect = (player.getVotedIndex() == this.correctShuffledIndex) ? 'Y' : 'N';
            buffer.append('|' + player.getName() + '|' + isCorrect + ',' + player.getVotedForCt() + ',' +
                    player.getScore() + '|' + player.getAnswer());
        }
        return buffer.toString();
    }
    public boolean getIsHost() {
        return isHost;
    }
    public int getNotAnsweredCount() {
        return players.size() - getAnswersCt();
    }
    public int getNotVotedCount() {
        return (players.size() - getVotesCt());
    }
    public int getPlayersCount() {
        return (players.size());
    }
    public String getLastJoinedPlayerName() {
        return lastJoinedPlayerName;
    }
    @Override // TcpControllerServer.Listener
    public void onPlayerConnected(String name) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPlayerConnected(" + name + ')');
        }
        if (players.containsKey(name)) {
            Log.d(TAG, "Player name already used: " + name);
            setHostStateLiveData(HostState.DUPLICATE_PLAYER_NAME);
        } else {
            if (leftPlayers.containsKey(name)) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "player " + name + " re-connecting");
                }
                players.put(name, leftPlayers.put(name, leftPlayers.get(name)));
                leftPlayers.remove(name);
            } else {
                Player player = new Player(name);
                addPlayer(name, player);
            }
            lastJoinedPlayerName = name;
            setHostStateLiveData(HostState.PLAYER_JOINED);
        }
    }
    @Override
    public void onPlayerDisconnected(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Player left: " + playerName);
        }
        leftPlayers.put(playerName, players.get(playerName));
        players.remove(playerName);
        HostState hostState = hostStateLiveData.getValue();
        if (hostState == HostState.AWAITING_CT_ANSWERS) {
            checkForAllAnswers();
        } else if (hostState == HostState.AWAITING_CT_VOTES) {
            checkForAllVotes();
        }
    }
    @Override // TcpControllerServer.MessageListener
    public void onServerStarted() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onServerStarted()");
        }
    }
    public void stopGame() {
        if (tcpService != null) {
            tcpService.stopNetworking();
        }
    }
    public void sendHostBroadcast(Context context) {
        /*!!tcpControllerServer*/tcpService.sendMultipleHostBroadcasts(context, 5);
        this.getApplication();
    }
    public void sendNextQuestion() {
        /*!!tcpControllerServer*/tcpService.sendToAll(getNextQuestion());
        setHostStateLiveData(HostState.AWAITING_CT_ANSWERS);
    }
    private String getNextQuestion() {
        try {
            currentQA = questionManager.getQuestionAnswer(questionSequence);
        } catch (EndOfQuestionsException e) {
            try {
                questionSequence = 0;
                currentQA = questionManager.getQuestionAnswer(questionSequence);
            } catch (EndOfQuestionsException e2) {
                throw new RuntimeException(e2);
            }
        }
        String nextQuestion = QUESTION + '|' + questionSequence + '|' + currentQA.type +
                '|' + currentQA.question;
        ++questionSequence;
        prefs.edit()
                .putInt(QUESTION_SEQUENCE_KEY, questionSequence)
                .apply();
        for (Player player: players.values()) {
            player.reset();
        }
        return nextQuestion;
    }
    private void showAnswers(String message) {
        // ALL_ANSWERS|2|a pig trough|dollop|
        int index = message.indexOf('|');
        int indexOfFirstAnswer = message.indexOf('|', index + 1) + 1;
        String[] answers = message.substring(indexOfFirstAnswer).split("\\|");
        List<String> answersList = Arrays.asList(answers);
        currentFragmentTagLiveData.setValue(ALL_ANSWERS);
        setAnswersLiveData(new AllAnswers(currentQuestion, answersList, false));
    }
    private void showNamedAnswers(String message) {
        // NAMED_ANSWERS|3|CORRECT|Hungarian physician|Joe|N,1,4|Centre forward for Man Utd|John|Y,0,3|Russian politician
        String[] tokens = message.split("\\|");
        List<String> answersList = new ArrayList<>();
        answersList.add(tokens[2] + ": " + tokens[3]);
        int tokenIndex = 4;
        String[] details;
        boolean isCorrect = false;
        while ((tokenIndex + 2) < tokens.length) {
            details = tokens[tokenIndex + 1].split(",");
            if (tokens[tokenIndex].equals(playerName)) {
                isCorrect = details[0].equals("Y");
            }
            answersList.add(tokens[tokenIndex] + " (" + details[1] + ',' +
                    details[2] + "): " + tokens[tokenIndex + 2]);
            tokenIndex += 3;
        }
        currentFragmentTagLiveData.setValue(ALL_ANSWERS);
        setAnswersLiveData(new AllAnswers(currentQuestion, answersList, true, isCorrect));
    }
    @Override // TcpPlayerClient.Listener; message sent from host to player
    public void onMessage(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "from host to player: " + playerName + " onMessage(" + message + ")");
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            if (message.startsWith(ALL_ANSWERS)) {
                // ALL_ANSWERS|2|a pig trough|dollop|
                showAnswers(message);
                awaitingVoteLiveData.setValue(true);
                playerStateLiveData.setValue(PlayerState.SUPPLY_VOTE);
            } else if (message.startsWith(NAMED_ANSWERS)) {
                // NAMED_ANSWERS|3|CORRECT|Hungarian physician|Joe|N,1,4|Centre forward for Man Utd|John|Y,0,3|Russian politician
                showNamedAnswers(message);
                awaitingVoteLiveData.setValue(false);
                playerStateLiveData.setValue(PlayerState.AWAITING_NEXT_QUESTION);
            } else if (message.startsWith(QUESTION)) {
                String[] tqa = message.split("\\|", 4);
                currentQuestion = tqa[1] + ". " + tqa[2] + ": " + tqa[3];
                currentFragmentTagLiveData.setValue(QUESTION);
                setQuestionLiveData(currentQuestion);
                awaitingAnswerLiveData.setValue(true);
                playerStateLiveData.setValue(PlayerState.SUPPLY_ANSWER);
            } else {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "unrecognised message received by player: " + message);
                }
            }
        });
    }
    @Override // TcpPlayerClient.Listener
    public void onConnected() {
        Log.d(TAG, "Now connected to the game host");
        new Handler(Looper.getMainLooper()).post(() -> {
            setPlayerStateLiveData(PlayerState.AWAITING_FIRST_QUESTION);
        });
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
        new Handler(Looper.getMainLooper()).post(() -> {
            exceptionLiveData.setValue(e);
        });
    }
    @Override // TcpPlayerClient.Listener
    public void onHostFound(String hostIp, int port) {
        if (BuildConfig.DEBUG) {
            String status = "onHostFound(" + hostIp + "' " + port + ')';
            Log.d(TAG, status);
        }
        /*!!tcpPlayerClient*/tcpService.connect(hostIp, /*!!TcpControllerServer.TCP_PORT,*/
                playerName, this);
    }
    @Override
    protected void onCleared() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCleared()");
        }
        onPlayerLeavingGame();
    }
    public void onPlayerSignedIn(String playerName, boolean host) {
        this.playerName = playerName;
        if (host) {
            startHost();
        }
        WifiManager wifi =
                (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);
        /*!!tcpPlayerClient*/tcpService.listenForHostBroadcast(wifi, this);
    }
    public void onPlayerLeavingGame() {
        tcpService.stopNetworking();
        /*!!
        if (isHost) {
            if (tcpControllerServer != null) {
                tcpControllerServer.stop();
            }
        }
        if (tcpPlayerClient != null) {
            tcpPlayerClient.stopListening();
            tcpPlayerClient.disconnect();
        }

         */
    }
}
