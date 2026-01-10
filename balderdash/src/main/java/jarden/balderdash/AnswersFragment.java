package jarden.balderdash;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

/**
 * Created by john.denny@gmail.com on 06/01/2026.
 */
public class AnswersFragment extends Fragment {
    private static final String TAG = "AnswersFragment";
    private ListView answersListView;
    private ArrayAdapter<String> answersAdapter;
    private String[] savedAnswers = null;
    private AdapterView.OnItemClickListener savedListener = null;
    private AnswersFragment previousThis;
    private boolean showPlayerNames;

    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_answers, container, false);
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
        Log.d(TAG, "AnswersFragment()");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (savedListener != null) {
            setOnItemClickListener(savedListener);
            savedListener = null;
        }
        if (savedAnswers != null) {
            showAnswers(savedAnswers, showPlayerNames);
            savedAnswers = null;
        }
    }

    public void showAnswers(String[] answers, boolean showPlayerNames) {
        Lifecycle.State state = getLifecycle().getCurrentState();
        if (state == Lifecycle.State.RESUMED || state == Lifecycle.State.STARTED) {
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
            savedAnswers = answers;
            this.showPlayerNames = showPlayerNames;
        }
    }

}
