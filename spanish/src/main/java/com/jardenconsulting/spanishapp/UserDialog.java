package com.jardenconsulting.spanishapp;

import jarden.engspa.EngSpaUser;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;

public class UserDialog extends DialogFragment
		implements DialogInterface.OnClickListener, OnCheckedChangeListener {
	private EditText userLevelEditText;
	private UserSettingsListener userSettingsListener;
	private AlertDialog alertDialog;

	public interface UserSettingsListener {
        void onUpdateUserLevel(int userLevel);
		EngSpaUser getEngSpaUser();
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.userSettingsListener = (UserSettingsListener) activity;
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater =activity.getLayoutInflater();
		builder.setTitle(R.string.userSettingsStr);
		View view = inflater.inflate(R.layout.dialog_user, null);
		this.userLevelEditText = (EditText) view.findViewById(R.id.userLevelEditText);
		EngSpaUser user = userSettingsListener.getEngSpaUser();
		if (user == null) {
			// if it's a new engSpaUser, the user must supply the values
			setCancelable(false);
		} else {
			int userLevel = user.getLearnLevel();
			userLevelEditText.setText(String.valueOf(userLevel));
			// cancel button provided only for updates
			builder.setNegativeButton(R.string.cancel, this);
		}
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

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		this.userLevelEditText.setEnabled(!isChecked);
	}
}
