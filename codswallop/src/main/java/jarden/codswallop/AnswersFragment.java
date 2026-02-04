package jarden.codswallop;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

/**
 * Created by john.denny@gmail.com on 06/01/2026.
 */
public class AnswersFragment extends Fragment {
    private static final String TAG = "AnswersFragment";
    private TextView questionView;
    private ListView answersListView;
    private ArrayAdapter<String> answersAdapter;
    private AdapterView.OnItemClickListener savedListener = null;
    private AnswersFragment previousThis;
    private String[] savedAnswers = null;
    private String savedCurrentQuestion = null;
    private boolean savedShowPlayerNames;

    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_answers, container, false);
        questionView = rootView.findViewById(R.id.questionView);
        answersListView = rootView.findViewById(R.id.answersListView);
        this.answersAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1);
        answersListView.setAdapter(answersAdapter);
        return rootView;
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        Lifecycle.State state = getLifecycle().getCurrentState();
        if (state == Lifecycle.State.RESUMED || state == Lifecycle.State.STARTED) {
            answersListView.setOnItemClickListener(listener);
        } else {
            savedListener = listener;
        }
    }
    public AnswersFragment() {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "AnswersFragment()");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (savedListener != null) {
            setOnItemClickListener(savedListener);
            savedListener = null;
        }
        if (savedAnswers != null) {
            showAnswers(savedCurrentQuestion, savedAnswers, savedShowPlayerNames);
            savedAnswers = null;
        }
    }

    public void showAnswers(String currentQuestion, String[] answers, boolean showPlayerNames) {
        Lifecycle.State state = getLifecycle().getCurrentState();
        if (state == Lifecycle.State.RESUMED || state == Lifecycle.State.STARTED) {
            questionView.setText(currentQuestion);
            answersAdapter.setNotifyOnChange(false);
            answersAdapter.clear();
            if (showPlayerNames) {
                for (int i = 0; i < answers.length; i++) {
                    answersAdapter.add(answers[i++] + ' ' + answers[i]); // namei answeri
                }
            } else {
                for (String answer : answers) {
                    answersAdapter.add(answer);
                }
            }
            answersAdapter.notifyDataSetChanged();
        } else {
            savedCurrentQuestion = currentQuestion;
            savedAnswers = answers;
            savedShowPlayerNames = showPlayerNames;
        }
    }

}
