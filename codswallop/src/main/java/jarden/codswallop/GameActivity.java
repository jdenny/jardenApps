package jarden.codswallop;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import static jarden.codswallop.Constants.ALL_ANSWERS;
import static jarden.codswallop.Constants.LOGIN_DIALOG;
import static jarden.codswallop.Constants.QUESTION;

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
        LoginDialogFragment.LoginDialogListener {
    public static final String TAG = "GameActivity";

    // Host fields: ***************************
    private Button nextQuestionButton;
    private TextView hostStatusTextView;
    private View hostViewsLayout;

    // Host & Client fields ***************************
    private String currentFragmentTag = null;
    /*
    pendingFragmentTag is used when a network/game event requires navigation to a
    different Fragment, but the Activity is not currently in a safe lifecycle state
    to perform a FragmentTransaction (e.g. FragmentManager has already saved state
    because the Activity is stopping, backgrounded, or being recreated after a
    configuration change).

    In this case we store the requested fragment tag here and perform the
    navigation later in onResume(), when FragmentManager.isStateSaved() == false
    and it is safe to commit the transaction.
     */
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
        hostStatusTextView = findViewById(R.id.hostStatusView);
        hostViewsLayout = findViewById(R.id.hostLayout);
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                showAlertDialog(R.string.dialog_confirm,
                        new AlertDialogListener() {
                            @Override
                            public void onAlertDialogPositive() {
                                gameViewModel.onPlayerLeavingGame();
                                backPressedCallback.setEnabled(false); // Stops it being a recursive onBackPressed()!
                                getOnBackPressedDispatcher().onBackPressed();
                            }
                        }, R.drawable.leaving_fish_transparent);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        gameViewModel.getHostStateLiveData().observe(
                this,
                hostState -> {
                    if (hostState == Constants.HostState.AWAITING_PLAYERS) {
                        hostStatusTextView.setText(R.string.wait_for_players_then_broadcast_host);
                    } else if (hostState == Constants.HostState.PLAYER_JOINED) {
                        int ct = gameViewModel.getPlayersCount();
                        String playerName = gameViewModel.getLastJoinedPlayerName();
                        hostStatusTextView.setText(getString(R.string.player_joined, playerName, ct));
                    } else if (hostState == Constants.HostState.AWAITING_CT_ANSWERS) {
                        int ct = gameViewModel.getNotAnsweredCount();
                        hostStatusTextView.setText(getString(R.string.waiting_for_ct_answers, ct));
                    } else if (hostState == Constants.HostState.AWAITING_CT_VOTES) {
                        int ct = gameViewModel.getNotVotedCount();
                        hostStatusTextView.setText(getString(R.string.waiting_for_ct_votes, ct));
                    } else if (hostState == Constants.HostState.READY_FOR_NEXT_QUESTION) {
                        hostStatusTextView.setText(R.string.ready_for_next_question);
                    } else if (hostState == Constants.HostState.DUPLICATE_PLAYER_NAME) {
                        hostStatusTextView.setText(R.string.duplicatePlayerName);
                    } else {
                        hostStatusTextView.setText("Unknown hostState: " + hostState);
                    }
                });
        final LiveData<String> currentFragmentTagLiveData =
                gameViewModel.getCurrentFragmentTagLiveData();
        currentFragmentTagLiveData.observe(this, this::requestShowFragment);
        LoginDialogFragment loginDialog;
        if (savedInstanceState == null) {
            loginDialog = new LoginDialogFragment();
            loginDialog.show(getSupportFragmentManager(), LOGIN_DIALOG);
        } else {
        }
        setHostViews();
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
    @Override // View.OnClickListener; action host buttons
    public void onClick(View view) {
        int viewId = view.getId();
        if (viewId == R.id.nextQuestionButton) {
            Constants.HostState state = gameViewModel.getHostStateLiveData().getValue();
            if (state == Constants.HostState.AWAITING_CT_ANSWERS ||
                    state == Constants.HostState.AWAITING_CT_VOTES) {
                showAlertDialog(R.string.confirm_skip_question, new AlertDialogListener() {
                    @Override
                    public void onAlertDialogPositive() {
                        gameViewModel.sendNextQuestion();
                    }
                }, R.drawable.next_question_fish_transparent);
            } else {
                gameViewModel.sendNextQuestion();
            }
        } else if (viewId == R.id.broadcastHostButton) {
            gameViewModel.sendHostBroadcast(this);
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
        gameViewModel.onPlayerJoined(playerName, true);
        setHostViews();
    }
    private void setHostViews() {
        if (gameViewModel.getIsHost()) {
            hostViewsLayout.setVisibility(View.VISIBLE);
        }
    }
    @Override // LoginDialogListener
    public void onJoinButton(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onJoinButton(" + playerName + ')');
        }
        gameViewModel.onPlayerJoined(playerName, false);
    }
    @Override // Activity
    protected void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDestroy()");
        }
        super.onDestroy();
    }
    public interface AlertDialogListener {
        public void onAlertDialogPositive();
    }
    private void showAlertDialog(int message, AlertDialogListener listener, int iconResource) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_image, null);

        ImageView image = view.findViewById(R.id.dialogImage);
        image.setImageResource(iconResource);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Codswallop")
                .setMessage(message)
                .setView(view)
                .setPositiveButton(R.string.yesStr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onAlertDialogPositive();
                    }})
                .setNegativeButton(R.string.noStr, null)
                .create();
        dialog.show();
        /*!!
        new AlertDialog.Builder(this)
                .setTitle("Codswallop!")
                .setMessage(message)
                .setIcon(iconResource)
                .setPositiveButton(R.string.yesStr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onAlertDialogPositive();
                    }})
                .setNegativeButton(R.string.noStr, null).show();

         */
    }
}