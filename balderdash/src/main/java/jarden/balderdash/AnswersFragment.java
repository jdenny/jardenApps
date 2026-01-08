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

/**
 * Created by john.denny@gmail.com on 06/01/2026.
 */
public class AnswersFragment extends Fragment {

    private static final String TAG = "AnswersFragment";
    private ListView answersListView;
    private ArrayAdapter<String> answersAdapter;
    private String[] savedAnswers = null;
    private AdapterView.OnItemClickListener savedListener = null;

    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_answers, container, false);
        answersListView = rootView.findViewById(R.id.answersListView);
        this.answersAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1);
        answersListView.setAdapter(answersAdapter);
        if (savedAnswers != null) {
            showAnswers(savedAnswers);
            savedAnswers = null;
        }
        if (savedListener != null) {
            setOnItemClickListener(savedListener);
        }
        return rootView;
    }
    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        if (answersListView != null) {
            answersListView.setOnItemClickListener(listener);
        } else {
            savedListener = listener;
        }
    }
    public AnswersFragment() {
        Log.d(TAG, "AnswersFragment()");
    }

    public void showAnswers(String[] answers) {
        if (answersAdapter != null) {
            answersAdapter.setNotifyOnChange(false);
            answersAdapter.clear();
            for (String answer : answers) {
                answersAdapter.add(answer);
            }
            answersAdapter.notifyDataSetChanged();
        } else {
            // wait for fragment to initialise
            savedAnswers = answers;
        }
    }

}
