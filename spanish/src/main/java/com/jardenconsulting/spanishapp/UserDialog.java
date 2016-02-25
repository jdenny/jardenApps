package com.jardenconsulting.spanishapp;

import jarden.engspa.EngSpaQuiz;
import jarden.engspa.EngSpaUser;
import jarden.provider.engspa.EngSpaContract;
import jarden.provider.engspa.EngSpaContract.QAStyle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.Spinner;

public class UserDialog extends DialogFragment
		implements DialogInterface.OnClickListener, OnCheckedChangeListener {
	private EditText userNameEditText;
	private EditText userLevelEditText;
	private Spinner qaStyleSpinner;
	private UserSettingsListener userSettingsListener;
	private AlertDialog alertDialog;
	private CheckBox allCheckBox;
	
	public interface UserSettingsListener {
		void onUpdateUser(String userName, int userLevel, QAStyle qaStyle);
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
		this.userNameEditText = (EditText) view.findViewById(R.id.userNameEditText);
		this.userLevelEditText = (EditText) view.findViewById(R.id.userLevelEditText);
		this.allCheckBox = (CheckBox) view.findViewById(R.id.allCheckBox);
		allCheckBox.setOnCheckedChangeListener(this);
		this.qaStyleSpinner = (Spinner) view.findViewById(R.id.qaStyleSpinner);
		ArrayAdapter<String> qaStyleAdapter = new ArrayAdapter<String>(activity,
				android.R.layout.simple_spinner_item,
				EngSpaContract.qaStyleNames);
		qaStyleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		qaStyleSpinner.setAdapter(qaStyleAdapter);
		EngSpaUser user = userSettingsListener.getEngSpaUser();
		if (user == null) {
			// if it's a new engSpaUser, the user must supply the values
			setCancelable(false);
		} else {
			userNameEditText.setText(user.getUserName());
			int userLevel = user.getUserLevel();
			userLevelEditText.setText(String.valueOf(userLevel));
			if (userLevel == EngSpaQuiz.USER_LEVEL_ALL) this.allCheckBox.setChecked(true);
			int position = user.getQAStyle().ordinal();
			this.qaStyleSpinner.setSelection(position);
			// cancel button provided only for updates
			builder.setNegativeButton(R.string.cancelStr, this);
		}
		builder.setView(view);
		builder.setPositiveButton(R.string.updateStr, this);
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
			String userName = userNameEditText.getText().toString().trim();
			String userLevelStr = userLevelEditText.getText().toString();
			int userLevel;
			if (allCheckBox.isChecked()) userLevel = EngSpaQuiz.USER_LEVEL_ALL;
			else {
				try {
					userLevel = Integer.parseInt(userLevelStr);
				} catch (NumberFormatException nfe) {
					userLevel = -1;
				}
			}
			String qaStyleStr = (String) qaStyleSpinner.getSelectedItem();
			QAStyle qaStyle = QAStyle.valueOf(qaStyleStr);
			this.userSettingsListener.onUpdateUser(userName, userLevel, qaStyle);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		this.userLevelEditText.setEnabled(!isChecked);
	}
}
