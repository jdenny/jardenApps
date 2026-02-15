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

import static jarden.codswallop.Protocol.ALL_ANSWERS;
import static jarden.codswallop.Protocol.ANSWER;
import static jarden.codswallop.Protocol.CORRECT;
import static jarden.codswallop.Protocol.NAMED_ANSWERS;
import static jarden.codswallop.Protocol.QUESTION;
import static jarden.codswallop.Protocol.VOTE;

/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class GameViewModel extends ViewModel implements TcpControllerServer.MessageListener,
        TcpPlayerClient.Listener {
    private final static String TAG = "GameViewModel";
    private QuestionManager questionManager;
    private final MutableLiveData<String> currentFragmentTagLiveData =
            new MutableLiveData<>("");
    private final MutableLiveData<String> questionLiveData =
            new MutableLiveData<>("");
    private final MutableLiveData<String> answerLiveData =
            new MutableLiveData<>("");
    private final MutableLiveData<AnswersState> answersLiveData =
            new MutableLiveData<>(new AnswersState(null, null, false));
    private final MutableLiveData<Integer> selectedAnswerLiveData =
            new MutableLiveData<>(null);
    private final MutableLiveData<String> hostStatusLiveData =
            new MutableLiveData<>(null);

    private final List<String> shuffledNameList = new ArrayList<>();
    private String pendingFragmentTag;
    private final TcpPlayerClient tcpPlayerClient = new TcpPlayerClient();
    private TcpControllerServer tcpControllerServer;
    private Map<String, Player> players;
    private boolean voteCast;
    private int answersCt;
    private int votesCt;
    private int questionSequence;
    private String currentQuestion;
    private QuestionManager.QuestionAnswer currentQA;
    private String playerName;

    public void setCurrentFragmentTagLiveData(String currentFragmentTag) {
        currentFragmentTagLiveData.setValue(currentFragmentTag);
    }
    public LiveData<String> getCurrentFragmentTagLiveData() {
        return currentFragmentTagLiveData;
    }
    public void setQuestionLiveData(String question) {
        questionLiveData.setValue(question);
    }
    public LiveData<String> getQuestionLiveData() {
        return questionLiveData;
    }
    public void setAnswerLiveData(String answer) {
        if (answer != null && !answer.isEmpty()) {
            answerLiveData.setValue(answer);
            tcpPlayerClient.sendAnswer(questionSequence, answer);
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
    public void setHostStatusLiveData(String hostStatus) {
        new Handler(Looper.getMainLooper()).post(() -> {
            this.hostStatusLiveData.setValue(hostStatus);
        });
    }
    public MutableLiveData<String> getHostStatusLiveData() {
        return hostStatusLiveData;
    }
    public void startHost(Resources gameResources) {
        if (tcpControllerServer == null) {
            players = new ConcurrentHashMap<>();
            questionManager = new QuestionManager(gameResources);
            tcpControllerServer = new TcpControllerServer(this);
            tcpControllerServer.start();
        }
    }
    public TcpPlayerClient getTcpPlayerClient() {
        return tcpPlayerClient;
    }
    public TcpControllerServer getTcpControllerServer() {
        return tcpControllerServer;
    }
    public void setTcpControllerServer(TcpControllerServer server) {
        this.tcpControllerServer = server;
    }
    public Map<String, Player> getPlayers() {
        return players;
    }
    public void addPlayer(String name, Player player) {
        players.put(name, player);
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
            } else {
                setHostStatusLiveData(
                        "Waiting for " + (players.size() - answersCt) +
                        " other player(s) to answer");
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
                setHostStatusLiveData(
                        "Waiting for " + (players.size() - votesCt) +
                                " other player(s) to vote");
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
        playerName = name;
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Player joined: " + playerName);
        }
        Player player = new Player(playerName, "not supplied", 0);
        addPlayer(playerName, player);
        setHostStatusLiveData(playerName + " has joined; " + players.size() +
                " players so far");
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
            setHostStatusLiveData("Now connected to the game host; wait for the first question");
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
    public void listenForBroadcast(WifiManager wifi) {
        tcpPlayerClient.listenForHostBroadcast(wifi, this);
    }
}
