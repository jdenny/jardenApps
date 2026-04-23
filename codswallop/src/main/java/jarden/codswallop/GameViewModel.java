package jarden.codswallop;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import static jarden.codswallop.Constants.HostState;
import static jarden.codswallop.Constants.PlayerState;
import static jarden.codswallop.Constants.QUESTION;
/**
 * Created by john.denny@gmail.com on 11/02/2026.
 */
public class GameViewModel extends AndroidViewModel {

    public final static class PlayerJoinedData {
        public String joinedPlayerName;
        public int playerCount;
        public PlayerJoinedData(String joinedPlayerName, int playerCount) {
            this.joinedPlayerName = joinedPlayerName;
            this.playerCount = playerCount;
        }
    }
    private final MutableLiveData<PlayerJoinedData> playerJoiningEvent =
            new MutableLiveData<>();
    private final MutableLiveData<AllAnswers> answersLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<String> currentFragmentTagLiveData =
            new MutableLiveData<>(QUESTION);
    private final MutableLiveData<PlayerState> playerStateLiveData =
            new MutableLiveData<>(PlayerState.AWAITING_HOST_IP);
    private final MutableLiveData<String> questionLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> gameEndedEvent = new MutableLiveData<>();
    private final MutableLiveData<Integer> missingAnswerCtLiveData = new MutableLiveData<>();
    private final MutableLiveData<Integer> missingVoteCtLiveData = new MutableLiveData<>();
    private final MutableLiveData<Boolean> awaitingAnswerLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<Boolean> hostBroadcastSentLiveData =
            new MutableLiveData<>();
    private final MutableLiveData<HostState> hostStateLiveData =
            new MutableLiveData<>(HostState.AWAITING_PLAYERS);
    private final MutableLiveData<Exception> exceptionLiveData =
            new MutableLiveData<>(null);
    private boolean isHost;
    private final static String TAG = "GameViewModel";

    public GameViewModel(@NotNull Application application) {
        super(application);
    }
    public LiveData<Boolean> getHostBroadcastSentLiveData() {
        return hostBroadcastSentLiveData;
    }
    public void setHostBroadcastSentLiveData(Boolean sent) {
        hostBroadcastSentLiveData.setValue(sent);
    }
    public LiveData<Exception> getExceptionLiveData() {
        return exceptionLiveData;
    }
    public void setExceptionLiveData(Exception e) {
        exceptionLiveData.setValue(e);
    }
    public LiveData<PlayerJoinedData> getPlayerJoiningEvent() {
        return playerJoiningEvent;
    }
    public void setPlayerJoiningEvent(PlayerJoinedData playerData) {
        playerJoiningEvent.setValue(playerData);
    }
    public LiveData<Boolean> getAwaitingAnswerLiveData() {
        return awaitingAnswerLiveData;
    }
    public void setAwaitingAnswerLiveData(boolean value) {
        awaitingAnswerLiveData.setValue(value);
    }
    public LiveData<Integer> getMissingVoteCtLiveData() {
        return missingVoteCtLiveData;
    }
    public void setMissingVoteCtLiveData(Integer notVotedCt) {
        missingVoteCtLiveData.setValue(notVotedCt);
    }
    public LiveData<Integer> getMissingAnswerCtLiveData() {
        return missingAnswerCtLiveData;
    }
    public void setMissingAnswerCtLiveData(Integer notAnsweredCt) {
        missingAnswerCtLiveData.setValue(notAnsweredCt);
    }
    public LiveData<PlayerState> getPlayerStateLiveData() {
        return playerStateLiveData;
    }
    public void setPlayerStateLiveData(PlayerState state) {
        playerStateLiveData.setValue(state);
    }
    public LiveData<String> getCurrentFragmentTagLiveData() {
        return currentFragmentTagLiveData;
    }
    public void setCurrentFragmentTagLiveData(String tagLiveData) {
        currentFragmentTagLiveData.setValue(tagLiveData);
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
    /*//
    public LiveData<String> getSubmitAnswerEvent() {
        return submitAnswerEvent;
    }
    public LiveData<Integer> getSubmitVoteEvent() {
        return submitVoteEvent;
    }
     */
    public boolean getIsHost() {
        return isHost;
    }
    public void setIsHost(boolean isHost) {
        this.isHost = isHost;
    }
    public LiveData<Integer> getGameEndedEvent() {
        return gameEndedEvent;
    }
    public void setGameEndedEvent(int messageId) {
        gameEndedEvent.setValue(messageId);
    }
    public void answerSent() {
        awaitingAnswerLiveData.setValue(false);
        playerStateLiveData.setValue(PlayerState.AWAITING_ANSWERS);
    }
    public LiveData<AllAnswers> getAnswersLiveData() {
        return answersLiveData;
    }
    public void setAnswersLiveData(AllAnswers allAnwers){
        answersLiveData.setValue(allAnwers);
    }
    public void voteSent() {
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
    @Override
    protected void onCleared() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCleared()");
        }
    }
}
