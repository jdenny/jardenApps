package jarden.codswallop;

import android.util.Log;

import java.util.Map;

import androidx.lifecycle.ViewModel;
import jarden.tcp.TcpControllerServer;
import jarden.tcp.TcpPlayerClient;

/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class GameViewModel extends ViewModel {
    private final static String TAG = "GameViewModel";
    private final TcpPlayerClient tcpPlayerClient = new TcpPlayerClient();
    private TcpControllerServer tcpControllerServer;
    private Map<String, Player> players;
    private String currentFragmentTag;
    private boolean voteCast;
    private int answersCt;
    private int votesCt;
    private int questionSequence;
    private String currentQuestion;

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
    public void setCurrentFragmentTag(String currentFragmentTag) {
        this.currentFragmentTag = currentFragmentTag;
    }
    public String getCurrentFragmentTag() {
        return currentFragmentTag;
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
