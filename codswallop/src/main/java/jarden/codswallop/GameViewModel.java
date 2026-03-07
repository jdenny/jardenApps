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
            new MutableLiveData<>(new AllAnswers(null, null, false, false, null));
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
    private String thisPlayerName;
    private Map<String, Player> players;
    private Map<String, Player> leftPlayers;
    private final MutableLiveData<PlayerState> playerStateLiveData =
            new MutableLiveData<>(PlayerState.AWAITING_HOST_IP);
    private final MutableLiveData<String> questionLiveData =
            new MutableLiveData<>("");
    private QuestionManager questionManager;
    private int questionSequence;
    private final List<String> shuffledNameList = new ArrayList<>();
    //!! private int correctShuffledIndex;
    private final static String TAG = "GameViewModel";
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
        /* Advice from ChatGPT: If tcpService is null at the point of joinGame(), startHosting(),
         // sendMessage(), add this instance variable:
         // private final List<Runnable> pendingServiceActions = new ArrayList<>();
         // add the code below to attachService():
        for (Runnable action : pendingServiceActions) {
            action.run();
        }
        pendingServiceActions.clear();
         */
        // then, delay calls if tcpService is null, e.g.
        /*
        public void sendMessage(String msg) {
            Runnable action = () -> tcpService.sendMessage(msg);
            if (tcpService != null) {
                action.run();
            } else {
                pendingServiceActions.add(action);
            }
        }
         */
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
            tcpService.sendAnswer(questionSequence, answer);
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
        tcpService.sendVote(questionSequence, position);
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
        players = new ConcurrentHashMap<>();
        leftPlayers = new ConcurrentHashMap<>();
        questionManager = new QuestionManager(getApplication().getResources());
        isHost = true;
        questionSequence = prefs.getInt(QUESTION_SEQUENCE_KEY, 0);
        tcpService.startHosting(this);
    }
    @Override  // TcpControllerServer.MessageListener
    // i.e. message sent from player to host
    public void onMessageToServer(String playerName, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "from player: " + playerName + " message: " + message);
        }
        Player currentPlayer = players.get(playerName);
        if (message.startsWith(ANSWER)) {
            String answer = message.split("\\|", 3)[2];
            currentPlayer.setAnswer(answer);
            checkForAllAnswers();
        } else if (message.startsWith(VOTE)) {
            String index = message.split("\\|", 3)[2];
            int indexOfVotedItem = Integer.parseInt(index);
            String nameVotedFor = shuffledNameList.get(indexOfVotedItem);
            currentPlayer.setNameVotedFor(nameVotedFor);
            if (nameVotedFor.equals(CORRECT)) {
                currentPlayer.incrementScore();
            } else {
                if (!nameVotedFor.equals(playerName)) {
                    players.get(nameVotedFor).incrementScore();
                }
            }
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
            tcpService.sendToAll(getNamedAnswersMessage());
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
            tcpService.sendToAll(getAllAnswersMessage());
            setHostStateLiveData(HostState.AWAITING_CT_VOTES);
        } else {
            setHostStateLiveData(HostState.AWAITING_CT_ANSWERS);
        }
    }
    private int getVotesCt() {
        int votesCt = 0;
        for (Player playerN : players.values()) {
            if (playerN.getNameVotedFor() != null) {
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
        //!! String name;
        //!! for (int i = 0; i < shuffledNameList.size(); i++) {
        for (String name : shuffledNameList) {
            //!! name = shuffledNameList.get(i);
            if (name.equals(CORRECT)) {
                //!! this.correctShuffledIndex = i;
                buffer.append('|' + currentQA.answer);
            } else {
                buffer.append('|' + players.get(name).getAnswer());
            }
        }
        return buffer.toString();
    }
    private String getNamedAnswersMessage() {
        List<Player> playerList = new ArrayList<>(players.values());
        playerList.sort((p1, p2) ->
                Integer.compare(p2.getScore(), p1.getScore()));
        StringBuffer buffer = new StringBuffer(NAMED_ANSWERS + '|' + questionSequence);
        buffer.append('|' + CORRECT + '|' + currentQA.answer);
        if (currentQA.comment != null) {
            buffer.append(". " + currentQA.comment);
        }
        for (Player player: playerList) {
            buffer.append('|' + player.getName() + '|' + player.getNameVotedFor() +
                    '|' + player.getScore() + '|' + player.getAnswer());
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
    public void sendHostBroadcast(Context context) {
        tcpService.sendMultipleHostBroadcasts(context, 5);
    }
    public void sendNextQuestion() {
        tcpService.sendToAll(getNextQuestion());
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
        String[] tokens = message.split("\\|");
        List<String> answersList = new ArrayList<>();
        List<Integer> linesVotedForMe = new ArrayList<>();
        answersList.add(tokens[2] + ": " + tokens[3]);
        int tokenIndex = 4;
        boolean isCorrect = false;
        String currentPlayerName;
        String nameVotedFor;
        while ((tokenIndex + 3) < tokens.length) {
            currentPlayerName = tokens[tokenIndex];
            nameVotedFor = tokens[tokenIndex + 1];
            if (currentPlayerName.equals(thisPlayerName)) {
                isCorrect = CORRECT.equals(nameVotedFor);
            }
            answersList.add(currentPlayerName + " (" +
                    tokens[tokenIndex + 2] + "): " + tokens[tokenIndex + 3]);
            if (nameVotedFor.equals(thisPlayerName)) {
                linesVotedForMe.add(answersList.size() - 1);
            }
            tokenIndex += 4;
        }
        currentFragmentTagLiveData.setValue(ALL_ANSWERS);
        setAnswersLiveData(new AllAnswers(currentQuestion, answersList, true, isCorrect,
                linesVotedForMe));
    }
    @Override // TcpPlayerClient.Listener; message sent from host to player
    public void onMessageToClient(String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "from host to player: " + thisPlayerName + " onMessageToClient(" + message + ")");
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            if (message.startsWith(ALL_ANSWERS)) {
                showAnswers(message);
                awaitingVoteLiveData.setValue(true);
                playerStateLiveData.setValue(PlayerState.SUPPLY_VOTE);
            } else if (message.startsWith(NAMED_ANSWERS)) {
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
            } else if (message.startsWith(Constants.Protocol.GAME_OVER.name())) {
                tcpService.stopNetworking();
                playerStateLiveData.setValue(PlayerState.GAME_ENDED);
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
        tcpService.connect(hostIp, thisPlayerName, this);
    }
    @Override
    protected void onCleared() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCleared()");
        }
        //??!! onPlayerLeavingGame();
    }
    public void onPlayerSignedIn(String playerName, boolean host) {
        thisPlayerName = playerName;
        if (host) {
            startHost();
        }
        WifiManager wifi =
                (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);
        tcpService.listenForHostBroadcast(wifi, this);
    }
    public void onPlayerLeavingGame() {
        if (tcpService != null) {
            if (isHost) {
                tcpService.sendToAll(Constants.Protocol.GAME_OVER.name());
                // TODO: maybe use callback to wait for above broadcast to finish
            }
            stopNetworking();
        }
    }

    public void stopNetworking() {
        if (tcpService != null) {
            tcpService.stopNetworking();
            //??!! tcpService = null;
        }
    }
}
