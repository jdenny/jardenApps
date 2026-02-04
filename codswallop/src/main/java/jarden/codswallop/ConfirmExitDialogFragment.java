package jarden.codswallop;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

/**
 * Created by john.denny@gmail.com on 04/02/2026.
 */
public class ConfirmExitDialogFragment extends DialogFragment {
    public interface ExitDialogListener {
        public void onExitDialogConfirmed();
    }
    private ExitDialogListener listener;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_confirm)
                .setPositiveButton(R.string.yesStr, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onExitDialogConfirmed();
                    }
                });
        return builder.create();
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        listener = (ExitDialogListener) context;
    }
}

// StartGameDialogFragment().show(supportFragmentManager, "GAME_DIALOG");
