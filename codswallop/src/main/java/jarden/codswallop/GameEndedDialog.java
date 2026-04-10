package jarden.codswallop;

import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Created by john.denny@gmail.com on 10/04/2026.
 */
public class GameEndedDialog extends DialogFragment {
    public interface Listener {
        void onGameEndedAcknowledged();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new AlertDialog.Builder(requireContext())
                .setTitle("Game ended")
                .setMessage(getArguments().getString("message"))
                .setIcon(R.drawable.thumbs_up_fish_transparent)
                .setPositiveButton("OK", (d, w) -> {
                    Listener listener = (Listener) requireActivity();
                    listener.onGameEndedAcknowledged();
                })
                .create();
    }
}


