package jarden.balderdash;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.Collection;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Lifecycle;

/**
 * Created by john.denny@gmail.com on 08/01/2026.
 */
public class ScoresDialogFragment extends DialogFragment {
    private static final String TAG = "ScoresDialogFragment";
    private ListView scoresListView;
    private Collection<Player> players;
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
        if (players != null) {
            showScores(players);
            players = null;
        }
    }
    public void showScores(Collection<Player> players) {
        Lifecycle.State state = getLifecycle().getCurrentState();
        if (state == Lifecycle.State.RESUMED || state == Lifecycle.State.STARTED) {
            scoresAdapter.setNotifyOnChange(false);
            scoresAdapter.clear();
            for (Player player : players) {
                scoresAdapter.add(player.getName() + " " + player.getScore());
            }
            scoresAdapter.notifyDataSetChanged();
        } else {
            this.players = players;
        }

    }

}
