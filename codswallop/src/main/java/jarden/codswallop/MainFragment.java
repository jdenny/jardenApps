package jarden.codswallop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

/**
 * Created by john.denny@gmail.com on 06/01/2026.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private TextView questionView;
    private EditText answerEditText;
    private Button sendButton;
    private OnClickListener gameActivity;

    private String savedQuestion;
    private boolean sendButtonEnabled;

    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        questionView = rootView.findViewById(R.id.questionView);
        answerEditText = rootView.findViewById(R.id.answerEditText);
        sendButton = rootView.findViewById(R.id.sendButton);
        gameActivity = (GameActivity)getActivity();
        sendButton.setOnClickListener(view -> {
            if (answerEditText.getText().toString().trim().length() == 0) {
                Toast.makeText(getContext(), "supply an answer first!",
                        Toast.LENGTH_LONG).show();
            } else {
                gameActivity.onClick(view);
            }
        });
        return rootView;
    }
    public String getAnswerEditText() {
        return answerEditText.getText().toString();
    }

    public void enableSendButton(boolean enabled) {
        sendButtonEnabled = enabled;
        sendButton.setEnabled(enabled);
    }
    @Override
    public void onResume() {
        super.onResume();
        if (savedQuestion != null) {
            setQuestionView(savedQuestion);
            savedQuestion = null;
        }
        sendButton.setEnabled(sendButtonEnabled);
    }
    public void setQuestionView(String question) {
        Lifecycle.State state = getLifecycle().getCurrentState();
        if (state == Lifecycle.State.RESUMED || state == Lifecycle.State.STARTED) {
            questionView.setText(question);
            answerEditText.setText("");
        } else {
            savedQuestion = question;
        }
    }
}
