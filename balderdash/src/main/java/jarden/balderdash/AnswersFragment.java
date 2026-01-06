package jarden.balderdash;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.fragment.app.Fragment;

/**
 * Created by john.denny@gmail.com on 06/01/2026.
 */
public class AnswersFragment extends Fragment implements AdapterView.OnItemClickListener  {

    private static final String TAG = "AnswersFragment";
    private ListView answersListView;
    private ArrayAdapter<String> answersAdapter;

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
    @Override // OnItemClickListener
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "onItemClick(position=" + position);
    }

    private String[] answersArray = {
        "silly moo",
        "you cannot be serious",
        "one flew over the cookoo's next",
        "Play it Sam!"
    };
    private ArrayList<String> answersList;
    public void showAnswers(ArrayList<String> answers) {
        // the following is temporary code!
        if (answersList == null) {
            answersList = new ArrayList<>(Arrays.asList(answersArray));
        }
        answers = answersList;
        // end of temporary code
        answersAdapter.setNotifyOnChange(false);
        answersAdapter.clear();
        for (String answer : answers) {
            answersAdapter.add(answer);
        }
        answersAdapter.notifyDataSetChanged();
    }

}
