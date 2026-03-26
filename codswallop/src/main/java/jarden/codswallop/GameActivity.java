package jarden.codswallop;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
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
import jarden.tcp.TcpService;

import static jarden.codswallop.Constants.ALL_ANSWERS;
import static jarden.codswallop.Constants.LOGIN_DIALOG;
import static jarden.codswallop.Constants.QUESTION;

/** Design of application
 Message Protocol: see class Constants
 Players agree who will be host; all open the app; all login: name, host or join.
 Host selects “Send Host Address”; when each player receives the host address, it joins the game.

 Server gets next question from dictionary and sends to other devices
 All players, including Server, see the question; supply their answer, which is sent to Server
 When Server has the answers, including the real one, it sends them to all Clients, in random order
 Players give their votes; when all votes in, Server highlights the real answer
 Initial dialog:
    PlayerNameEditText; HostButton; JoinButton
 two screens (Fragments):
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

 */
public class GameActivity extends AppCompatActivity implements View.OnClickListener,
        LoginDialogFragment.LoginDialogListener {
    public static final String TAG = "GameActivity";

    // Host fields: ***************************
    private Button nextQuestionButton;
    private TextView hostPromptView;
    private TextView playerPromptView;
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
    private TcpService tcpService;
    private boolean isBound = false;
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TcpService.LocalBinder binder = (TcpService.LocalBinder) service;
            tcpService = binder.getService();
            isBound = true;
            gameViewModel.attachService(tcpService);
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            tcpService = null;
        }
    };

    @Override // Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            String message = "onCreate(" + ((savedInstanceState == null) ? "null" : "not null") + ")";
            Log.d(TAG, message);
        }
        Intent intent = new Intent(this, TcpService.class);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        setContentView(R.layout.activity_game);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        nextQuestionButton.setOnClickListener(this);
        Button sendHostAddressButton = findViewById(R.id.broadcastHostButton);
        sendHostAddressButton.setOnClickListener(this);
        hostPromptView = findViewById(R.id.hostPromptView);
        playerPromptView = findViewById(R.id.playerPromptView);
        hostViewsLayout = findViewById(R.id.hostLayout);
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                String s = getString(R.string.confirm_host_leaving);
                int confirmMessage = gameViewModel.getIsHost() ?
                        R.string.confirm_host_leaving : R.string.confirm_leaving;
                showAlertDialog(confirmMessage,
                        new AlertDialogListener() {
                            @Override
                            public void onAlertDialogPositive() {
                                gameViewModel.onPlayerLeavingGame();
                                backPressedCallback.setEnabled(false); // Stops it being a recursive onBackPressed()!
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
                        hostPromptView.setText(R.string.wait_for_players_then_broadcast_host);
                    } else if (hostState == Constants.HostState.PLAYER_JOINED) {
                        int ct = gameViewModel.getPlayersCount();
                        String playerName = gameViewModel.getLastJoinedPlayerName();
                        hostPromptView.setText(getString(R.string.player_joined, playerName, ct));
                    } else if (hostState == Constants.HostState.AWAITING_CT_ANSWERS) {
                        int ct = gameViewModel.getNotAnsweredCount();
                        hostPromptView.setText(getString(R.string.waiting_for_ct_answers, ct));
                    } else if (hostState == Constants.HostState.AWAITING_CT_VOTES) {
                        int ct = gameViewModel.getNotVotedCount();
                        hostPromptView.setText(getString(R.string.waiting_for_ct_votes, ct));
                    } else if (hostState == Constants.HostState.READY_FOR_NEXT_QUESTION) {
                        hostPromptView.setText(R.string.ready_for_next_question);
                    } else if (hostState == Constants.HostState.DUPLICATE_PLAYER_NAME) {
                        hostPromptView.setText(R.string.duplicatePlayerName);
                    } else {
                        hostPromptView.setText(getString(R.string.unknown_hoststate, hostState));
                    }
                });
        gameViewModel.getPlayerStateLiveData()
                .observe(this,
                        playerState -> {
                            int promptId;
                            if (playerState == Constants.PlayerState.AWAITING_HOST_IP) {
                                promptId = R.string.waiting_for_host_address;
                            } else if (playerState == Constants.PlayerState.AWAITING_FIRST_QUESTION) {
                                promptId = R.string.connectedWaitForQuestion;
                            } else if (playerState == Constants.PlayerState.SUPPLY_ANSWER) {
                                promptId = R.string.supply_answer_and_send;
                            } else if (playerState == Constants.PlayerState.AWAITING_ANSWERS) {
                                promptId = R.string.waiting_for_more_answers;
                            } else if (playerState == Constants.PlayerState.SUPPLY_VOTE) {
                                    promptId = R.string.voteNow;
                            } else if (playerState == Constants.PlayerState.AWAITING_VOTES) {
                                promptId = R.string.waiting_for_more_votes;
                            } else if (playerState == Constants.PlayerState.AWAITING_NEXT_QUESTION) {
                                promptId = R.string.scores_wait_for_question;
                            } else if (playerState == Constants.PlayerState.GAME_ENDED) {
                                promptId = R.string.game_ended;
                                String message = gameViewModel.getIsHost() ? "The host has ended the game" :
                                        "You have left the game";
                                new AlertDialog.Builder(this)
                                        .setTitle("Game ended")
                                        .setMessage(message)
                                        .setIcon(R.drawable.thumbs_up_fish_transparent)
                                        .setPositiveButton("OK", (d, w) -> finish())
                                        .show();
                            } else {
                                promptId = R.string.unrecognised_state;
                            }
                            playerPromptView.setText(promptId);
                        });
        final LiveData<String> currentFragmentTagLiveData =
                gameViewModel.getCurrentFragmentTagLiveData();
        currentFragmentTagLiveData.observe(this, this::requestShowFragment);
        gameViewModel.getExceptionLiveData().observe(this,exception -> {
            if (exception != null) {
                gameViewModel.onPlayerLeavingGame();
                finishAffinity(); // close the app
            }
        });
        LoginDialogFragment loginDialog;
        if (savedInstanceState == null) {
            loginDialog = new LoginDialogFragment();
            loginDialog.show(getSupportFragmentManager(), LOGIN_DIALOG);
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
        gameViewModel.onPlayerSignedIn(playerName, true);
        setHostViews();
    }
    private void setHostViews() {
        if (gameViewModel.getIsHost()) {
            hostViewsLayout.setVisibility(View.VISIBLE);
            int qaCount = gameViewModel.getQuestionCount();
            Toast.makeText(this, qaCount + " questions loaded", Toast.LENGTH_LONG).show();
        }
    }
    @Override // LoginDialogListener
    public void onJoinButton(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onJoinButton(" + playerName + ')');
        }
        gameViewModel.onPlayerSignedIn(playerName, false);
    }
    @Override // Activity
    protected void onDestroy() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onDestroy()");
        }
        super.onDestroy();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
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
    }
}