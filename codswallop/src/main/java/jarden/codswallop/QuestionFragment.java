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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by john.denny@gmail.com on 06/01/2026.
 */
public class QuestionFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private TextView questionView;
    private EditText answerEditText;
    private Button sendButton;
    private OnClickListener gameActivity;
    private QuestionViewModel questionViewModel;

    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        questionView = rootView.findViewById(R.id.questionView);
        answerEditText = rootView.findViewById(R.id.answerEditText);
        sendButton = rootView.findViewById(R.id.sendButton);
        try {
            gameActivity = (GameActivity) getActivity();
        } catch (ClassCastException cce) {
            if (BuildConfig.DEBUG) {
                Log.d(TAG, cce.toString());
            }
        }
        sendButton.setOnClickListener(view -> {
            if (answerEditText.getText().toString().trim().length() == 0) {
                Toast.makeText(getContext(), "supply an answer first!",
                        Toast.LENGTH_LONG).show();
            } else {
                sendButton.setEnabled(false);
                if (gameActivity != null) { gameActivity.onClick(view); }
            }
        });
        questionViewModel = new ViewModelProvider(requireActivity()).get(QuestionViewModel.class);
        return rootView;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        questionViewModel.getQuestionLD().observe(
                getViewLifecycleOwner(),
                question -> {
                    questionView.setText(question);
                    answerEditText.setText("");
                    sendButton.setEnabled(true);
                });
    }
    public String getAnswerEditText() {
        return answerEditText.getText().toString();
    }
}
