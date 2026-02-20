package jarden.codswallop;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import static jarden.codswallop.Constants.ALL_ANSWERS;
import static jarden.codswallop.Constants.LOGIN_DIALOG;
import static jarden.codswallop.Constants.QUESTION;
import static jarden.codswallop.Constants.QUESTION_SEQUENCE_KEY;

/** Design of application
Message Protocol:
 Host to all Players using broadcast:
    HOST_ANNOUNCE|192.168.0.12|50001
 Host to all Players using Tcp:
    QUESTION|3|PEOPLE|Ignaz Semmelveis
    ALL_ANSWERS|3|Centre forward for Man Utd and England|Hungarian physician and scientist
    NAMED_ANSWERS|3|CORRECT|Hungarian physician and scientist, known as the saviour of mothers|Joe|Centre forward for Man Utd and England
    Not used: SCORES|3|John 2|Julie 4
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
public class GameActivity extends AppCompatActivity implements View.OnClickListener,
        LoginDialogFragment.LoginDialogListener, ConfirmExitDialogFragment.ExitDialogListener {
    public static final String TAG = "GameActivity";

    // Host fields: ***************************
    private Button nextQuestionButton;
    private TextView statusTextView;
    private View hostViewsLayout;

    // Host & Client fields ***************************
    private String currentFragmentTag = null;
    private String pendingFragmentTag = null;
    private OnBackPressedCallback backPressedCallback;
    private GameViewModel gameViewModel;

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
        statusTextView = findViewById(R.id.hostStatusView);
        hostViewsLayout = findViewById(R.id.hostLayout);
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                ConfirmExitDialogFragment dialog = new ConfirmExitDialogFragment();
                dialog.show(getSupportFragmentManager(), "ConfirmExitDialogFragment");
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
        LoginDialogFragment loginDialog;
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        gameViewModel.getHostStateLiveData().observe(
                this,
                hostState -> {
                    if (hostState == Constants.HostState.AWAITING_PLAYERS) {
                        statusTextView.setText(R.string.wait_for_players_then_broadcast_host);
                    } else if (hostState == Constants.HostState.PLAYER_JOINED) {
                        int ct = gameViewModel.getPlayersCount();
                        String playerName = gameViewModel.getLastJoinedPlayerName();
                        statusTextView.setText(getString(R.string.player_joined, playerName, ct));
                    } else if (hostState == Constants.HostState.AWAITING_CT_ANSWERS) {
                        int ct = gameViewModel.getNotAnsweredCount();
                        statusTextView.setText(getString(R.string.waiting_for_ct_answers, ct));
                    } else if (hostState == Constants.HostState.AWAITING_CT_VOTES) {
                        int ct = gameViewModel.getNotVotedCount();
                        statusTextView.setText(getString(R.string.waiting_for_ct_votes, ct));
                    } else if (hostState == Constants.HostState.READY_FOR_NEXT_QUESTION) {
                        statusTextView.setText(R.string.ready_for_next_question);
                    } else if (hostState == Constants.HostState.DUPLICATE_PLAYER_NAME) {
                        statusTextView.setText(R.string.duplicatePlayerName);
                    } else {
                        statusTextView.setText("Unknown hostState: " + hostState);
                    }
                });
        final LiveData<String> currentFragmentTagLiveData =
                gameViewModel.getCurrentFragmentTagLiveData();
        currentFragmentTagLiveData.observe(this, this::requestShowFragment);
        String fragmentTag;
        if (savedInstanceState == null) {
            fragmentTag = QUESTION;
            loginDialog = new LoginDialogFragment();
            loginDialog.show(getSupportFragmentManager(), LOGIN_DIALOG);
        } else {
            fragmentTag = currentFragmentTagLiveData.getValue();
            currentFragmentTag = fragmentTag;
            pendingFragmentTag = gameViewModel.getPendingFragmentTag();
            if (gameViewModel.getIsHost()) {
                setHostViews();
            }
        }
        gameViewModel.setCurrentFragmentTagLiveData(fragmentTag);
        int qs = getPreferences(Context.MODE_PRIVATE).getInt(QUESTION_SEQUENCE_KEY, -1);
        gameViewModel.setQuestionSequence(qs);
    }

    private void requestShowFragment(String fragmentTag) {
        if (fragmentTag != null) {
            // Already showing it?
            if (!fragmentTag.equals(currentFragmentTag)) {
                if (getSupportFragmentManager().isStateSaved()) {
                    // Can't do it now — queue it
                    pendingFragmentTag = fragmentTag;
                } else {
                    performShowFragment(fragmentTag);
                }
            }
        }
    }
    private void performShowFragment(String fragmentTag) {
        if (!fragmentTag.equals(currentFragmentTag)) {
            currentFragmentTag = fragmentTag;
            pendingFragmentTag = null;
            Fragment fragment =
                    getSupportFragmentManager()
                            .findFragmentByTag(fragmentTag);
            if (fragment == null) {
                if (QUESTION.equals(fragmentTag)) {
                    fragment = new QuestionFragment();
                } else if (ALL_ANSWERS.equals(fragmentTag)) {
                    fragment = new AnswersFragment();
                } else {
                    throw new RuntimeException(
                            "Unknown fragmentTag: " + fragmentTag);
                }
            }
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragmentContainerView,
                            fragment,
                            fragmentTag)
                    .commit();
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        if (pendingFragmentTag != null &&
                !getSupportFragmentManager().isStateSaved()) {
            performShowFragment(pendingFragmentTag);
        }
    }
    @Override // ConfirmExitDialogFragment.ExitDialogListener
    public void onExitDialogConfirmed() {
        backPressedCallback.setEnabled(false); // DON'T FORGET THIS!
        getOnBackPressedDispatcher().onBackPressed();
    }
    @Override // View.OnClickListener; action host buttons
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.nextQuestionButton) {
            gameViewModel.sendNextQuestion();
        } else if (viewId == R.id.broadcastHostButton) {
            gameViewModel.sendHostBroadcast(this);
            nextQuestionButton.setEnabled(true);
        } else {
            Toast.makeText(this, "unknown button pressed: " + view,
                    Toast.LENGTH_LONG).show();
        }
    }
    @Override // LoginDialogListener
    public void onHostButton(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onHostButton(" + playerName + ')');
        }
        setHostViews();
        waitForHostBroadcast(playerName);
        gameViewModel.startHost(getResources());
        hostViewsLayout.setVisibility(View.VISIBLE);
    }
    private void setHostViews() {
        hostViewsLayout.setVisibility(View.VISIBLE);
    }
    private void waitForHostBroadcast(String playerName) {
        gameViewModel.setPlayerName(playerName);
        WifiManager wifi =
                (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        gameViewModel.listenForBroadcast(wifi);
    }
    @Override // LoginDialogListener
    public void onJoinButton(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onJoinButton(" + playerName + ')');
        }
        gameViewModel.setIsHost(false);
        waitForHostBroadcast(playerName);
    }
    @Override // Activity
    public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onSaveInstanceState(); currentFragmentTag=" +
                    currentFragmentTag);
        }
        super.onSaveInstanceState(savedInstanceState);
    }
    @Override // Activity
    protected void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDestroy()");
        }
        super.onDestroy();
        SharedPreferences.Editor editor = getPreferences(Context.MODE_PRIVATE).edit();
        editor.putInt(QUESTION_SEQUENCE_KEY, gameViewModel.getQuestionSequence());
        editor.apply();
    }
}