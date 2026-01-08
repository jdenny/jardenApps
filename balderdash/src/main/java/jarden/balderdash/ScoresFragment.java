package jarden.balderdash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;

/**
 * Created by john.denny@gmail.com on 08/01/2026.
 */
public class ScoresFragment extends Fragment {
    private static final String TAG = "AnswersFragment";
    private TextView yourAnswerTextView;
    private TextView correctAnswerTextView;
    private ListView scoresListView;
    private Results savedResults;
    private ArrayAdapter<Object> scoresAdapter;

    @Override // Fragment
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scores, container, false);
        yourAnswerTextView.findViewById(R.id.yourAnswer);
        correctAnswerTextView.findViewById(R.id.correctAnswer);
        scoresListView = rootView.findViewById(R.id.scoresListView);
        this.scoresAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1);
        scoresListView.setAdapter(scoresAdapter);
        return rootView;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (savedResults != null) {
            showScores(savedResults);
            savedResults = null;
        }
    }

    public void showScores(Results results) {
        Lifecycle.State state = getLifecycle().getCurrentState();
        if (state == Lifecycle.State.RESUMED || state == Lifecycle.State.STARTED) {
            yourAnswerTextView.setText(results.getPlayerAnswer());
            correctAnswerTextView.setText(results.getCorrectAnswer());
            scoresAdapter.setNotifyOnChange(false);
            scoresAdapter.clear();
            for (Player player : results.getPlayers()) {
                scoresAdapter.add(player.getName() + " " + player.getScore());
            }
            scoresAdapter.notifyDataSetChanged();
        } else {
            savedResults = results;
        }

    }

}
