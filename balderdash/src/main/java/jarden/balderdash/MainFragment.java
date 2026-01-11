package jarden.balderdash;

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
    private TextView outputView;
    private EditText answerEditText;
    private Button sendButton;
    private OnClickListener gameActivity;

    private String savedMessage;

    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        outputView = rootView.findViewById(R.id.outputView);
        answerEditText = rootView.findViewById(R.id.answerEditText);
        sendButton = rootView.findViewById(R.id.sendButton);
        //!! sendButton.setOnClickListener((GameActivity)getActivity());
        gameActivity = (GameActivity)getActivity();
        sendButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (answerEditText.getText().toString().trim().length() == 0) {
                    Toast.makeText(getContext(), "supply an answer first!",
                            Toast.LENGTH_LONG).show();
                } else {
                    gameActivity.onClick(view);
                }
            }
        });
        return rootView;

    }

    public String getAnswerEditText() {
        return answerEditText.getText().toString();
    }

    public void enableSendButton(boolean enabled) {
        sendButton.setEnabled(enabled);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (savedMessage != null) {
            setOutputView(savedMessage);
            savedMessage = null;
        }
    }

    public void setOutputView(String message) {
        Lifecycle.State state = getLifecycle().getCurrentState();
        if (state == Lifecycle.State.RESUMED || state == Lifecycle.State.STARTED) {
            outputView.setText(message);
        } else {
            savedMessage = message;
        }
    }
}
