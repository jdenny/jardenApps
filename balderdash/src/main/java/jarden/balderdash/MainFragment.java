package jarden.balderdash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * Created by john.denny@gmail.com on 06/01/2026.
 */
public class MainFragment extends Fragment {

    private static final String TAG = "MainFragment";
    private EditText nameEditText;
    private EditText answerEditText;
    private TextView outputView;

    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        nameEditText = rootView.findViewById((R.id.nameEditText));
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

    public void setOutputView(String message) {
        outputView.setText(message);
    }
}
