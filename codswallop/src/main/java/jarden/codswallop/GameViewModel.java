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
public class GameViewModel extends AndroidViewModel /*!! implements TcpHostServer.ServerListener,
        QuestionManager.QuestionListener*/ {
    /*!!
    public Player getPlayer(String playerName) {
        return players.get(playerName);
    } */

    public final static class PlayerJoinedData {
        public String joinedPlayerName;
        public int playerCount;
        public PlayerJoinedData(String joinedPlayerName, int playerCount) {
            this.joinedPlayerName = joinedPlayerName;
            this.playerCount = playerCount;
        }
    }
    //!! private final MutableLiveData<Integer> questionsLoadedEvent = new MutableLiveData<>();
    private final MutableLiveData<PlayerJoinedData> playerJoiningEvent =
            new MutableLiveData<>();
    //!! private final MutableLiveData<Boolean> hostLeavingEvent = new MutableLiveData<>();
    //!! private final MutableLiveData<Boolean> listenForHostBroadcastLiveData = new MutableLiveData<>();
    //!! private final MutableLiveData<String> nextQuestionEvent = new MutableLiveData<>();
    private final MutableLiveData<AllAnswers> answersLiveData =
            new MutableLiveData<>();
    //!! private final MutableLiveData<String> answersEventLiveData = new MutableLiveData<>();
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
    private final MutableLiveData<HostState> hostStateLiveData =
            new MutableLiveData<>(HostState.AWAITING_PLAYERS);
    private final MutableLiveData<Exception> exceptionLiveData =
            new MutableLiveData<>(null);
    /*!!
    private QuestionManager.QuestionAnswer currentQA;
    private String currentQuestion;
     */
    //!! private boolean isHost;
    //!! private boolean gameEnding = false;
    //!! private boolean iChoseToLeave = false;
    //!! private String thisPlayerName;
    //!! private boolean isPlayerLeaving = false;
    //!! private Map<String, Player> players;
    //!! private Map<String, Player> leftPlayers;
    //!! private QuestionManager questionManager;
    //!! private int questionSequence = 21;
    //!! private final List<String> shuffledNameList = new ArrayList<>();
    private final static String TAG = "GameViewModel";
    //!! private String lastJoinedPlayerName;
    //!! private final SharedPreferences prefs;

    public GameViewModel(@NotNull Application application) {
        super(application);
        //!! prefs = application.getSharedPreferences(GAME_PREFS, Context.MODE_PRIVATE);
    }
    /*!!
    public LiveData<Integer> getQuestionsLoadedEvent() {
        return questionsLoadedEvent;
    }
     */
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
    /*!!
    public LiveData<String> getNextQuestionEvent() {
        return nextQuestionEvent;
    }
     */
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
    public LiveData<Integer> getGameEndedEvent() {
        return gameEndedEvent;
    }
    public void setGameEndedEvent(int messageId) {
        gameEndedEvent.setValue(messageId);
    }
    /*!!
    public LiveData<String> getAnswersEvent() {
        return answersEventLiveData;
    }
    public void setAnswersEvent(String answersMessage) {
        answersEventLiveData.setValue(answersMessage);
    }
    public LiveData<Boolean> getListenForHostBroadcastLiveData() {
        return listenForHostBroadcastLiveData;
    }
    public LiveData<Boolean> getHostLeavingEvent() {
        return hostLeavingEvent;
    }
     */
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
    /*!!
    public void startHost() {
        players = new ConcurrentHashMap<>();
        leftPlayers = new ConcurrentHashMap<>();
        questionManager = new QuestionManager(getApplication().getResources(), this);
        isHost = true;
        questionSequence = prefs.getInt(QUESTION_SEQUENCE_KEY, 0);
    }
    public void setQuestionSequence(int questionSequence) {
        this.questionSequence = questionSequence;
    }
    @Override  // TcpHostServer.ServerListener
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
                try {
                    if (!nameVotedFor.equals(playerName)) {
                        players.get(nameVotedFor).incrementScore();
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, nameVotedFor + " no longer connected");
                }
            }
            checkForAllVotes();
        } else {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "unrecognised message received by host: " + message);
            }
        }
    }
    private void checkForAllAnswers() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "checkForAllAnswers");
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            if (getAnswersCt() >= (players.size())) {
                answersEventLiveData.setValue(getAllAnswersMessage());
                waitingForVotes();
            } else {
                waitingForAnswers();
            }
        });
    }
    private void checkForAllVotes() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "checkForAllVotes");
        }
        new Handler(Looper.getMainLooper()).post(() -> {
            if (getVotesCt() >= (players.size())) {
                answersEventLiveData.setValue(getNamedAnswersMessage());
                hostStateLiveData.setValue(HostState.READY_FOR_NEXT_QUESTION);
            } else {
                waitingForVotes();
            }
        });
    }
    private void waitingForAnswers() {
        missingAnswerCtLiveData.setValue(getNotAnsweredCount());
        hostStateLiveData.setValue(HostState.AWAITING_CT_ANSWERS);
    }
    private void waitingForVotes() {
        missingVoteCtLiveData.setValue(getNotVotedCount());
        hostStateLiveData.setValue(HostState.AWAITING_CT_VOTES);
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
        for (String name : shuffledNameList) {
            if (name.equals(CORRECT)) {
                buffer.append('|' + currentQA.answer);
            } else {
                buffer.append('|' + players.get(name).getAnswer());
            }
        }
        return buffer.toString();
    }
    public String getNamedAnswersMessage() {
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
     */

    /*!!
    @Override // TcpHostServer.ServerListener
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
                players.put(name, player);
            }
            lastJoinedPlayerName = name;
            new Handler(Looper.getMainLooper()).post(() -> {
                playerJoiningEvent.setValue(
                        new PlayerJoinedData(lastJoinedPlayerName, players.size()));
            });
        }
    }
    @Override // TcpHostServer.ServerListener
    public void onPlayerDisconnected(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPlayerDisconnected(" + playerName + ")");
        }
        if (!gameEnding && playerName != null) {
            if (players.containsKey(playerName)) { // check not already removed
                leftPlayers.put(playerName, players.get(playerName));
                players.remove(playerName);
                HostState hostState = hostStateLiveData.getValue();
                if (hostState == HostState.AWAITING_CT_ANSWERS) {
                    checkForAllAnswers();
                } else if (hostState == HostState.AWAITING_CT_VOTES) {
                    checkForAllVotes();
                }
            }
        }
    }
    @Override // TcpHostServer.ServerListener
    public void onServerStarted() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onServerStarted()");
        }
    }
     */
    /*
    public void sendNextQuestion() {
        nextQuestionEvent.setValue(getNextQuestion());
        waitingForAnswers();
    }
    public String getNextQuestion() {
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
    public void endGame() {
        isPlayerLeaving = true;
        int messageId;
        if (iChoseToLeave) {
            if (isHost) {
                messageId = R.string.youEndedGame;
            } else {
                messageId = R.string.playerLeft;
            }
        } else {
            messageId = R.string.endedByHost;
        }
        gameEndedEvent.setValue(messageId);
    }
    public String getPlayerName() {
        return thisPlayerName;
    }
    */
    @Override
    protected void onCleared() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCleared()");
        }
    }
    /*!!
    public void onPlayerSignedIn(String playerName, boolean host) {
        thisPlayerName = playerName;
        if (host) {
            startHost();
        }
        listenForHostBroadcastLiveData.setValue(true);
    }
     */

    /*!!
    public void onPlayerLeaving() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onPlayerLeaving(); isPlayerLeaving=" + isPlayerLeaving);
        }
        if (!isPlayerLeaving) {
            isPlayerLeaving = true;
            iChoseToLeave = true;
            if (isHost) {
                this.gameEnding = true;
                hostLeavingEvent.setValue(true);
            } else {
                endGame();
            }
        }
    }
    @Override // QuestionManager.QuestionListener
    public void onError(String message) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_LONG).show();
    }
    @Override // QuestionManager.QuestionListener
    public void onQuestionsLoaded(int questionCount) {
        questionsLoadedEvent.setValue(questionCount);
    }
     */
}
