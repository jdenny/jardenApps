package jarden.codswallop;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by john.denny@gmail.com on 08/01/2026.
 */
public class ScoresDialogFragment extends DialogFragment {
    public class ScoresViewModel extends ViewModel {
        private final MutableLiveData<List<String>> scores =
                new MutableLiveData<>(new ArrayList<>());
        public void setScores(List<String> newScores) {
            scores.setValue(newScores);
        }
        public LiveData<List<String>> getScores() {
            return scores;
        }
    }
    private static final String TAG = "ScoresDialogFragment";
    private ListView scoresListView;
    private String[] scores;
    private ArrayAdapter<Object> scoresAdapter;
    private Button closeButton;
    private AlertDialog alertDialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        scoresAdapter = new ArrayAdapter<>(
                getActivity(), android.R.layout.simple_list_item_1);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.scores);
        builder.setAdapter(scoresAdapter, null);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_scores, null);
        closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        // scoresListView = view.findViewById(R.id.scoresListView);
        // scoresListView.setAdapter(scoresAdapter);
        builder.setView(view);
        alertDialog = builder.create();
        return alertDialog;
    }
    @Override
    public void onResume() {
        super.onResume();
        if (scores != null) {
            showScores(scores);
            scores = null;
        }
    }
    public void showScores(String[] scores) {
        Lifecycle.State state = getLifecycle().getCurrentState();
        if (state == Lifecycle.State.RESUMED || state == Lifecycle.State.STARTED) {
            scoresAdapter.setNotifyOnChange(false);
            scoresAdapter.clear();
            for (String score : scores) {
                scoresAdapter.add(score);
            }
            scoresAdapter.notifyDataSetChanged();
        } else {
            this.scores = scores;
        }

    }

}
