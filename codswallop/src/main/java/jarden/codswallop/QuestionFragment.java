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
                if (Boolean.FALSE.equals(gameViewModel.getHasSubmittedAnswer().getValue())) {
                    //!! sendButton.setEnabled(false);
                    gameViewModel.setAnswerLiveData(answer);
                    promptView.setText("waiting for other players to answer");
                    gameViewModel.setHasSubmittedAnswer(true);
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
                    if (question != null && !question.isEmpty()) {
                        // if (!questionView.getText().equals(question)) {
                        int currentQuestionId = getQuestionSequence(question.toString());
                        // if (!questionRendered) {
                        if (currentQuestionId != lastRenderedQuestionId) {
                            questionView.setText(question);
                            //!! sendButton.setEnabled(true);
                            // questionLiveData.setValue(question); //?? suggested by ChatGpt
                            gameViewModel.setHasSubmittedAnswer(false);
                            if (lastRenderedQuestionId != -1) {
                                answerEditText.setText("");
                            }
                            promptView.setText("supply answer and Send");
                            // questionRendered = true;
                            lastRenderedQuestionId = currentQuestionId;
                        }
                    }
                });
        gameViewModel.getHasSubmittedAnswer()
                .observe(getViewLifecycleOwner(),
                        submitted -> {
                            boolean hasAnswered = Boolean.TRUE.equals(submitted);
                            sendButton.setEnabled(!hasAnswered);
                        });

        /*!!
        if (savedInstanceState != null) {
            sendButton.setEnabled(
                    savedInstanceState.getBoolean(SEND_BUTTON_ENABLED));
            answerEditText.setText(savedInstanceState.getCharSequence(ANSWER));
        }

         */
    }
    private static int getQuestionSequence(String question) {
        int i = question.indexOf('.');
        return Integer.valueOf(question.substring(0, i));
    }
    /*!!
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putCharSequence(ANSWER, answerEditText.getText());
        outState.putBoolean(SEND_BUTTON_ENABLED, sendButton.isEnabled());
        super.onSaveInstanceState(outState);
    }

     */
}
