package jarden.codswallop;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.IBinder;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import jarden.tcp.TcpService;

import static jarden.codswallop.Constants.ALL_ANSWERS;
import static jarden.codswallop.Constants.LOGIN_DIALOG;
import static jarden.codswallop.Constants.QUESTION;

/** Design of application
 Message Protocol: see class Constants
 Players agree who will host; all open the app; all login: name, host or join.
 Host selects “Send Host Address”; when each player receives the host address, it joins the game.

 Server gets next question from dictionary and sends to other devices
 All players, including Server, see the question; supply their answer, which is sent to Server
 When Server has the answers, including the real one, it sends them to all Clients, in random order
 Players give their votes; when all votes in, Server highlights the real answer
 Initial dialogue:
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
        LoginDialogFragment.LoginDialogListener, GameEndedDialog.Listener,
        GameServiceProvider {
    public static final String TAG = "GameActivity";

    // Host fields: ***************************
    private TextView hostPromptView;
    private TextView playerPromptView;
    private View hostViewsLayout;
    private Button nextQuestionButton;

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
    private String playerName;
    private SpannableStringBuilder scoresWaitText;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TcpService.LocalBinder binder = (TcpService.LocalBinder) service;
            tcpService = binder.getService();
            isBound = true;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            tcpService = null;
        }
    };
    private Constants.HostState hostState;

    @Override // Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) {
            String message = "onCreate(" + ((savedInstanceState == null) ? "null" : "not null") + ")";
            Log.d(TAG, message);
        }
        setContentView(R.layout.activity_game);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        nextQuestionButton = findViewById(R.id.nextQuestionButton);
        nextQuestionButton.setOnClickListener(this);
        Button sendHostAddressButton = findViewById(R.id.broadcastHostButton);
        sendHostAddressButton.setOnClickListener(this);
        hostPromptView = findViewById(R.id.hostPromptView);
        playerPromptView = findViewById(R.id.playerPromptView);
        hostViewsLayout = findViewById(R.id.hostLayout);
        makeScoresWaitText();
        backPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                int confirmMessage = gameViewModel.getIsHost() ?
                        R.string.confirm_host_leaving : R.string.confirm_leaving;
                showAlertDialog(confirmMessage,
                        new AlertDialogListener() {
                            @Override
                            public void onAlertDialogPositive() {
                                gameViewModel.onPlayerLeaving();
                                backPressedCallback.setEnabled(false); // Stops it being a recursive onBackPressed()!
                            }
                        }, R.drawable.leaving_fish_transparent);
            }
        };
        getOnBackPressedDispatcher().addCallback(this, backPressedCallback);
        makeObservations();
        LoginDialogFragment loginDialog;
        if (savedInstanceState == null) {
            loginDialog = new LoginDialogFragment();
            loginDialog.show(getSupportFragmentManager(), LOGIN_DIALOG);
        }
        setHostViews();
    }
    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, TcpService.class);
        startService(intent);
        bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        switch (item.getItemId()) {
            case R.id.setQuestionNumber:
                showQuestionNumberDialog();
                return true;
            case R.id.sendIPAddress:
                tcpService.sendMultipleHostBroadcasts(5);
                nextQuestionButton.setVisibility(View.VISIBLE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void showQuestionNumberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter question number");
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("question number");
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            String value = input.getText().toString().trim();
            if (!value.isEmpty()) {
                int questionNumber = Integer.parseInt(value);
                gameViewModel.setQuestionSequence(questionNumber);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }
    private void makeScoresWaitText() {
        String highlight = getString(R.string.scoresHighlighted);
        String fullText = getString(R.string.scores_wait_for_question, highlight);
        SpannableStringBuilder builder = new SpannableStringBuilder(fullText);
        int start = fullText.indexOf(highlight);
        int end = start + highlight.length();
        builder.setSpan(new BackgroundColorSpan(ContextCompat.getColor(this, R.color.voted_for_me)),
                start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        scoresWaitText = builder;
    }
    private void makeObservations() {
        gameViewModel = new ViewModelProvider(this).get(GameViewModel.class);
        gameViewModel.getQuestionsLoadedEvent().observe(this, qaCount -> {
            Toast.makeText(this, qaCount + " questions loaded", Toast.LENGTH_LONG).show();
        });
        gameViewModel.getHostStateLiveData().observe(
                this,
                hostState -> {
                    if (hostState == Constants.HostState.READY_FOR_NEXT_QUESTION) {
                        hostPromptView.setText(R.string.ready_for_next_question);
                    } else if (hostState == Constants.HostState.AWAITING_PLAYERS) {
                        hostPromptView.setText(R.string.wait_for_players_then_broadcast_host);
                    } else if (hostState == Constants.HostState.DUPLICATE_PLAYER_NAME) {
                        hostPromptView.setText(R.string.duplicatePlayerName);
                    } else if (hostState != Constants.HostState.AWAITING_CT_VOTES &&
                            hostState != Constants.HostState.AWAITING_CT_ANSWERS) {
                        hostPromptView.setText(getString(R.string.unknown_hoststate, hostState));
                    }
                    this.hostState = hostState;
                });
        gameViewModel.getPlayerJoiningEvent().observe(this, playerData -> {
            hostPromptView.setText(getString(R.string.player_joined, playerData.joinedPlayerName,
                    playerData.playerCount));
        });
        gameViewModel.getPlayerStateLiveData()
                .observe(this,
                        playerState -> {
                            if (playerState == Constants.PlayerState.AWAITING_NEXT_QUESTION) {
                                playerPromptView.setText(scoresWaitText);
                            } else {
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
                                } else {
                                    promptId = R.string.unrecognised_state;
                                }
                                playerPromptView.setText(promptId);
                            }
                        });
        gameViewModel.getCurrentFragmentTagLiveData().observe(this, this::requestShowFragment);
        gameViewModel.getExceptionLiveData().observe(this, exception -> {
            if (exception != null) {
                finishAffinity(); // close the app
            }
        });
        gameViewModel.getGameEndedEvent().observe(this, messageId -> {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, "gameEndedEvent.observe(" + messageId + ')');
            }
            tcpService.stopNetworking();
            stopService(new Intent(this, TcpService.class));
            String message = getString(messageId);
            GameEndedDialog dialog = new GameEndedDialog();
            Bundle b = new Bundle();
            b.putString("message", message);
            dialog.setArguments(b);
            dialog.show(getSupportFragmentManager(), "game_end");
        });
        gameViewModel.getHostFoundEvent().observe(this, hostIpAddress -> {
            if (isBound && tcpService != null && !tcpService.isConnectedToHost()) {
                tcpService.connect(hostIpAddress, playerName, gameViewModel);
            }
        });
        gameViewModel.getNextQuestionEvent().observe(this, question -> {
            if (question != null && tcpService != null) {
                tcpService.sendToAll(question);
            }
        });
        gameViewModel.getAnswersEvent().observe(this, answers -> {
            if (answers != null && tcpService != null) {
                tcpService.sendToAll(answers);
            }
        });
        gameViewModel.getMissingVoteCtLiveData().observe(this, missingVoteCt -> {
            if (missingVoteCt != null) {
                hostPromptView.setText(getString(R.string.waiting_for_ct_votes,
                        missingVoteCt));
            }
        });
        gameViewModel.getMissingAnswerCtLiveData().observe(this, missingAnswerCt -> {
            if (missingAnswerCt != null) {
                hostPromptView.setText(getString(R.string.waiting_for_ct_answers,
                        missingAnswerCt));
            }
        });
        gameViewModel.getHostLeavingEvent().observe(this, hostLeaving -> {
            if (hostLeaving != null && tcpService != null) {
                tcpService.sendToAll(Constants.Protocol.END_GAME.name());
            }
        });
        gameViewModel.getListenForHostBroadcastLiveData().observe(this, listen -> {
            if (listen != null && tcpService != null) {
                WifiManager wifi =
                        (WifiManager) getApplication().getSystemService(Context.WIFI_SERVICE);
                tcpService.listenForHostBroadcast(wifi, gameViewModel);
            }
        });
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
            if (hostState == Constants.HostState.AWAITING_CT_ANSWERS ||
                    hostState == Constants.HostState.AWAITING_CT_VOTES) {
                showAlertDialog(R.string.confirm_skip_question, this::sendNextQuestion,
                        R.drawable.next_question_fish_transparent);
            } else {
                sendNextQuestion();
            }
        } else if (viewId == R.id.broadcastHostButton) {
            tcpService.sendMultipleHostBroadcasts(5);
            nextQuestionButton.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(this, "unknown button pressed: " + view,
                    Toast.LENGTH_LONG).show();
        }
    }
    private void sendNextQuestion() {
        //! tcpService.sendToAll(gameViewModel.getNextQuestion());
        gameViewModel.sendNextQuestion();
    }
    @Override // LoginDialogListener
    public void onHostButton(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onHostButton(" + playerName + ')');
        }
        this.playerName = playerName;
        gameViewModel.onPlayerSignedIn(playerName, true);
        setHostViews();
        tcpService.startHosting(gameViewModel);
    }
    private void setHostViews() {
        if (gameViewModel.getIsHost()) {
            hostViewsLayout.setVisibility(View.VISIBLE);
            setTitle(R.string.host_control);
        }
    }
    @Override // LoginDialogListener
    public void onJoinButton(String playerName) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onJoinButton(" + playerName + ')');
        }
        this.playerName = playerName;
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
    @Override
    public void onGameEndedAcknowledged() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onGameEndedAcknowledged()");
        }
        finish();
    }
    @Override
    public boolean isServiceReady() {
        return isBound && tcpService != null;
    }
    @Override
    public void submitAnswer(String answer) {
        if (answer != null && isServiceReady()) {
            // note: questionSequence not used for this event, so sending zero
            // for consistency with other events; similarly sendVote()
            tcpService.sendAnswer(0, answer);
            gameViewModel.answerSent();
        }
    }
    @Override
    public void submitVote(int position) {
        if (isServiceReady()) {
            tcpService.sendVote(0, position);
            gameViewModel.voteSent();
        }
    }
    public interface AlertDialogListener {
        void onAlertDialogPositive();
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