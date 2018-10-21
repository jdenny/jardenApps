package jarden.app.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.jardenconsulting.jardenlib.R;

public class IntegerDialog extends DialogFragment
		implements DialogInterface.OnClickListener {
	private EditText userLevelEditText;
	private int userLevel = 1;
	private UserSettingsListener userSettingsListener;
	private AlertDialog alertDialog;
    private String title = "IntegerDialog";

    public interface UserSettingsListener {
        void onUpdateUserLevel(int userLevel);
	}
    public void setTitle(String title) {
        this.title = title;
    }
	public void setUserLevel(int userLevel) {
	    this.userLevel = userLevel;
    }

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		this.userSettingsListener = (UserSettingsListener) context;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = activity.getLayoutInflater();
		builder.setTitle(this.title);
		View view = inflater.inflate(R.layout.dialog_integer, null);
        this.userLevelEditText = view.findViewById(R.id.userLevelEditText);
        userLevelEditText.setText(String.valueOf(this.userLevel));
        builder.setNegativeButton(R.string.cancel, this);
		builder.setView(view);
		builder.setPositiveButton(R.string.update, this);
		this.alertDialog = builder.create();
		return alertDialog;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		if (which == DialogInterface.BUTTON_NEGATIVE) {
			dialog.cancel();
		} else if (which == DialogInterface.BUTTON_POSITIVE) {
			// stop user doing multiple clicks; if load is slow, she
			// may be tempted to click again
			Button positiveButton = this.alertDialog.getButton(which);
			positiveButton.setEnabled(false);
			String userLevelStr = userLevelEditText.getText().toString();
			int userLevel;
            try {
                userLevel = Integer.parseInt(userLevelStr);
            } catch (NumberFormatException nfe) {
                userLevel = -1;
            }
            this.userSettingsListener.onUpdateUserLevel(userLevel);
		}
	}
}
