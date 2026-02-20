package jarden.codswallop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by john.denny@gmail.com on 06/01/2026.
 */
public class QuestionFragment extends Fragment {
    private static final String TAG = "QuestionFragment";
    private static final String SEND_BUTTON_ENABLED = "SEND_BUTTON_ENABLED";
    private TextView questionView;
    private EditText answerEditText;
    private Button sendButton;
    private GameViewModel gameViewModel;
    private TextView promptView;
    //?? private boolean questionRendered = false;
    private int lastRenderedQuestionId = -1;

    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "onCreateView(" + (savedInstanceState == null ? "" : "not null"));
        }
        View rootView = inflater.inflate(R.layout.fragment_question, container, false);
        questionView = rootView.findViewById(R.id.questionView);
        answerEditText = rootView.findViewById(R.id.answerEditText);
        sendButton = rootView.findViewById(R.id.sendButton);
        promptView = rootView.findViewById(R.id.promptView);
        gameViewModel = new ViewModelProvider(requireActivity()).get(GameViewModel.class);
        sendButton.setOnClickListener(view -> {
            String answer = answerEditText.getText().toString().trim();
            if (answer.isEmpty()) {
                Toast.makeText(getContext(), "supply an answer first!",
                        Toast.LENGTH_LONG).show();
            } else {
                if (Boolean.TRUE.equals(gameViewModel.getAwaitingAnswerLiveData().getValue())) {
                    gameViewModel.setAnswerLiveData(answer);
                }
            }
        });
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gameViewModel.getQuestionLiveData().observe(getViewLifecycleOwner(),
                question -> {
                    int currentQuestionId = getQuestionSequence(question);
                    questionView.setText(question);
                    if (currentQuestionId != lastRenderedQuestionId) {
                        if (lastRenderedQuestionId != -1) {
                            answerEditText.setText("");
                        }
                        lastRenderedQuestionId = currentQuestionId;
                    }
                });
        gameViewModel.getAwaitingAnswerLiveData()
                .observe(getViewLifecycleOwner(),
                        awaitingAnswer -> {
                            sendButton.setEnabled(awaitingAnswer);
                        });
        gameViewModel.getPlayerStateLiveData()
                .observe(getViewLifecycleOwner(),
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
                            } else {
                                promptId = R.string.play_on;
                            }
                            promptView.setText(promptId);
                        });
    }
    private static int getQuestionSequence(String question) {
        int i = question.indexOf('.');
        return  (i < 0) ? -1 : Integer.valueOf(question.substring(0, i));
    }
}
