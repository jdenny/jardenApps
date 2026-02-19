package jarden.codswallop;

import android.content.Context;
import android.content.res.Resources;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import jarden.quiz.EndOfQuestionsException;
import jarden.tcp.TcpControllerServer;
import jarden.tcp.TcpPlayerClient;

import static jarden.codswallop.Constants.ALL_ANSWERS;
import static jarden.codswallop.Constants.ANSWER;
import static jarden.codswallop.Constants.CORRECT;
import static jarden.codswallop.Constants.NAMED_ANSWERS;
import static jarden.codswallop.Constants.QUESTION;
import static jarden.codswallop.Constants.VOTE;
import static jarden.codswallop.Constants.HostState;
import static jarden.codswallop.Constants.PlayerState;
/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class GameViewModel extends ViewModel implements TcpControllerServer.MessageListener,
        TcpPlayerClient.Listener {
    private final MutableLiveData<String> answerLiveData =
            new MutableLiveData<>("");
    private int answersCt;
    private final MutableLiveData<AnswersState> answersLiveData =
            new MutableLiveData<>(new AnswersState(null, null, false));
    private final MutableLiveData<String> currentFragmentTagLiveData =
            new MutableLiveData<>("");
    private QuestionManager.QuestionAnswer currentQA;
    private String currentQuestion;
    private final MutableLiveData<Boolean> hasSentAnswerLiveData =
            new MutableLiveData<>(true); // initially, no question to answer

    private final MutableLiveData<HostState> hostStateLiveData =
            new MutableLiveData<>(HostState.AWAITING_PLAYERS);
    private boolean isHost;
    private String playerName;
    private String pendingFragmentTag;
    private Map<String, Player> players;
    private final MutableLiveData<PlayerState> playerStateLiveData =
            new MutableLiveData<>(PlayerState.AWAITING_HOST_IP);
    private final MutableLiveData<String> questionLiveData =
            new MutableLiveData<>("");
    private QuestionManager questionManager;
    private int questionSequence;
    private final MutableLiveData<Integer> selectedAnswerLiveData =
            new MutableLiveData<>(null);
    private final List<String> shuffledNameList = new ArrayList<>();
    private final static String TAG = "GameViewModel";
    private final TcpPlayerClient tcpPlayerClient = new TcpPlayerClient();
    private TcpControllerServer tcpControllerServer;
    private boolean voteCast;
    private int votesCt;
    private String lastJoinedPlayerName;

    public LiveData<Boolean> getHasSentAnswerLiveData() {
        return hasSentAnswerLiveData;
    }
    public void setHasSentAnswerLiveData(boolean sentAnswer) {
        hasSentAnswerLiveData.setValue(sentAnswer);
    }
    public LiveData<PlayerState> getPlayerStateLiveData() {
        return playerStateLiveData;
    }
    public void setPlayerStateLiveData(PlayerState playerState) {
        playerStateLiveData.setValue(playerState);
    }
    public void setPlayerName(String name) {
        playerName = name;
    }
    public void addPlayer(String name, Player player) {
        players.put(name, player);
    }
    public void setCurrentFragmentTagLiveData(String currentFragmentTag) {
        currentFragmentTagLiveData.setValue(currentFragmentTag);
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
    public void setAnswerLiveData(String answer) {
        if (answer != null && !answer.isEmpty()) {
            answerLiveData.setValue(answer);
            tcpPlayerClient.sendAnswer(questionSequence, answer);
            hasSentAnswerLiveData.setValue(true);
            setPlayerStateLiveData(PlayerState.AWAITING_ANSWERS);

        }
    }
    public MutableLiveData<String> getAnswerLiveData() {
        return answerLiveData;
    }
    public void setAnswersLiveData(AnswersState newAnswersState) {
        answersLiveData.setValue(newAnswersState);
    }
    public LiveData<AnswersState> getAnswersLiveData() {
        return answersLiveData;
    }
    public void setSelectedAnswerLiveData(Integer position) {
        if (position != null) {
            tcpPlayerClient.sendVote(questionSequence, String.valueOf(position));
        }
    }
    public LiveData<Integer> getSelectedAnswerLiveData() {
        return selectedAnswerLiveData;
    }
    public void setHostStateLiveData(HostState hostState) {
        new Handler(Looper.getMainLooper()).post(() -> {
            this.hostStateLiveData.setValue(hostState);
        });
    }
    public MutableLiveData<HostState> getHostStateLiveData() {
        return hostStateLiveData;
    }
    public void startHost(Resources gameResources) {
        if (tcpControllerServer == null) {
            players = new ConcurrentHashMap<>();
            questionManager = new QuestionManager(gameResources);
            tcpControllerServer = new TcpControllerServer(this);
            tcpControllerServer.start();
            isHost = true;
        }
    }
    public void setPendingFragmentTag(String pendingFragmentTag) {
        this.pendingFragmentTag = pendingFragmentTag;
    }
    public String getPendingFragmentTag() {
        return pendingFragmentTag;
    }
    public void setVoteCast(boolean voteCast) {
        this.voteCast = voteCast;
    }
    public boolean getVoteCast() {
        return voteCast;
    }
    public void setAnswersCt(int answersCt) {
        this.answersCt = answersCt;
    }
    public int incrementAnswersCt() {
        return ++answersCt;
    }
    public int getAnswersCt() {
        return answersCt;
    }
    public void setVotesCt(int votesCt) {
        this.votesCt = votesCt;
    }
    public int incrementVotesCt() {
        return ++votesCt;
    }
    public int getVotesCt() {
        return votesCt;
    }
    public void setQuestionSequence(int questionSequence) {
        this.questionSequence = questionSequence;
    }
    public int getQuestionSequence() {
        return questionSequence;
    }
    public void setCurrentQuestion(String currentQuestion) {
        this.currentQuestion = currentQuestion;
    }
    public String getCurrentQuestion() {
        return currentQuestion;
    }
    @Override
    protected void onCleared() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCleared()");
        }
        if (tcpControllerServer != null) {
            tcpControllerServer.stop();
        }
        if (tcpPlayerClient != null) {
            tcpPlayerClient.stopListening();
            tcpPlayerClient.disconnect();
        }
    }

    @Override  // TcpControllerServer.MessageListener
    // i.e. message sent from player to host
    public void onMessage(String playerName, String message) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "from player: " + playerName + " message: " + message);
        }
        if (message.startsWith(ANSWER)) {
            String answer = message.split("\\|", 3)[2];
            players.get(playerName).setAnswer(answer);
            answersCt = incrementAnswersCt();
            if (answersCt >= (players.size())) {
                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "all answers received for current question");
                }
                String nextMessage = getAllAnswersMessage();
                tcpControllerServer.sendToAll(nextMessage);
                hostStateLiveData.setValue(HostState.AWAITING_CT_VOTES);
            } else {
                hostStateLiveData.setValue(HostState.AWAITING_CT_ANSWERS);
                /*!!
                        "Waiting for " + (players.size() - answersCt) +
                        " other player(s) to answer");

                 */
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
                hostStateLiveData.setValue(HostState.READY_FOR_NEXT_QUESTION);
            } else {
                setHostStateLiveData(HostState.AWAITING_CT_VOTES);
                /*!!
                        "Waiting for " + (players.size() - votesCt) +
                                " other player(s) to vote");

                 */
            }
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "unrecognised message received by host: " + message);
            }
        }

    }
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
        return buffer.toString();    }

    @Override // TcpControllerServer.Listener
    public void onPlayerConnected(String name) {
        if (players.containsKey(name)) {
            name = name + "2";
        }
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Player joined: " + name);
        }
        Player player = new Player(name, "not supplied", 0);
        addPlayer(name, player);
        lastJoinedPlayerName = name;
        setHostStateLiveData(HostState.PLAYER_JOINED);
        /*!!
                name + " has joined; " + players.size() +
                " players so far");

         */
    }

    @Override
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
    public void sendHostBroadcast(Context context) {
        tcpControllerServer.sendHostBroadcast(context);
    }
    public void sendNextQuestion() {
        tcpControllerServer.sendToAll(getNextQuestion());
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
        answersCt = 0;
        votesCt = 0;
        return nextQuestion;
    }
    private void showAnswers(String message) {
        int index = message.indexOf('|');
        int indexOfFirstAnswer = message.indexOf('|', index + 1) + 1;
        String[] answers = message.substring(indexOfFirstAnswer).split("\\|");
        List<String> answersList = Arrays.asList(answers);
        setCurrentFragmentTagLiveData(ALL_ANSWERS);
        setAnswersLiveData(new AnswersState(currentQuestion, answersList,
                (message.startsWith(NAMED_ANSWERS))));
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
                voteCast = false;
            } else if (message.startsWith(NAMED_ANSWERS)) {
                // NAMED_ANSWERS|3|CORRECT: Norway's most famous sculptor|Joe (2): Centre forward for Liverpool
                showAnswers(message);
                voteCast = true;
            } else if (message.startsWith(QUESTION)) {
                String[] tqa = message.split("\\|", 4);
                currentQuestion = tqa[1] + ". " + tqa[2] + ": " + tqa[3];
                setCurrentFragmentTagLiveData(QUESTION);
                setQuestionLiveData(currentQuestion);
                hasSentAnswerLiveData.setValue(false);
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
            setPlayerStateLiveData(PlayerState.AWAITING_QUESTION);
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
    }
    @Override // TcpPlayerClient.HostFoundCallback
    public void onHostFound(String hostIp, int port) {
        if (BuildConfig.DEBUG) {
            String status = "onHostFound(" + hostIp + "' " + port + ')';
            Log.d(TAG, status);
        }
        tcpPlayerClient.connect(hostIp, TcpControllerServer.TCP_PORT,
                playerName, this);
    }
    public void listenForBroadcast(WifiManager wifi) {
        tcpPlayerClient.listenForHostBroadcast(wifi, this);
    }
    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }
    public boolean getIsHost() {
        return isHost;
    }
    public int getNotAnsweredCount() {
        return players.size() - answersCt;
    }
    public int getNotVotedCount() {
        return (players.size() - votesCt);
    }

    public int getPlayersCount() {
        return (players.size());
    }
    public String getLastJoinedPlayerName() {
        return lastJoinedPlayerName;
    }
}
