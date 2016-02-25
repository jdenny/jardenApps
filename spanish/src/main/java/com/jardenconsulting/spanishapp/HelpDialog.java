package com.jardenconsulting.spanishapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class HelpDialog extends DialogFragment {
	private TextView helpTextView;
	private AlertDialog alertDialog;
	
	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = activity.getLayoutInflater();
		builder.setTitle(R.string.spanishHelpStr);
		View view = inflater.inflate(R.layout.help_layout, null);
		this.helpTextView = (TextView) view.findViewById(R.id.helpTextView);
		helpTextView.setText(R.string.helpHomePage);
		builder.setView(view);
		this.alertDialog = builder.create();
		return alertDialog;
	}
}
