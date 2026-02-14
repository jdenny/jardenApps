package jarden.codswallop;

import android.util.Log;

import java.util.Map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import jarden.tcp.TcpControllerServer;
import jarden.tcp.TcpPlayerClient;

/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class GameViewModel extends ViewModel {
    private final static String TAG = "GameViewModel";
    private final MutableLiveData<String> currentFragmentTagLiveData =
            new MutableLiveData<>("");
    private final MutableLiveData<String> questionLiveData =
            new MutableLiveData<>("");
    private final MutableLiveData<String> answerLiveData =
            new MutableLiveData<>("");
    private final MutableLiveData<AnswersState> answersLiveData =
            new MutableLiveData<>(new AnswersState(null, null));
    private final MutableLiveData<Integer> selectedAnswerLiveData =
            new MutableLiveData<>(null);
    private final MutableLiveData<String> statusTextLiveData =
            new MutableLiveData<>("");
    private String pendingFragmentTag;
    private final TcpPlayerClient tcpPlayerClient = new TcpPlayerClient();
    private TcpControllerServer tcpControllerServer;
    private Map<String, Player> players;
    private boolean voteCast;
    private int answersCt;
    private int votesCt;
    private int questionSequence;
    private String currentQuestion;

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
            tcpPlayerClient.sendAnswer(questionSequence, answer);
            setStatusTextLiveData("waiting for other players to answer");
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
    public void setStatusTextLiveData(String statusText) {
        statusTextLiveData.setValue(statusText);
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
    public void setPlayers(Map<String, Player> players) {
        this.players = players;
    }
    public Map<String, Player> getPlayers() {
        return players;
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
    public int getAnswersCt() {
        return answersCt;
    }
    public void setVotesCt(int votesCt) {
        this.votesCt = votesCt;
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

}
