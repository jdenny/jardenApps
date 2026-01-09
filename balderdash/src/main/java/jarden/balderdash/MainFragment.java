package jarden.balderdash;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

/**
 * Created by john.denny@gmail.com on 06/01/2026.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private EditText nameEditText;
    private EditText answerEditText;
    private TextView outputView;
    private String savedMessage;

    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        answerEditText = rootView.findViewById(R.id.answerEditText);
        outputView = rootView.findViewById(R.id.outputView);
        return rootView;
    }

    public String getPlayerName() {
        return nameEditText.getText().toString().trim();
    }

    public String getAnswerEditText() {
        return answerEditText.getText().toString();
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
