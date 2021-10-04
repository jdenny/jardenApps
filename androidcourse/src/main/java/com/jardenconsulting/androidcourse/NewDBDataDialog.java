package com.jardenconsulting.androidcourse;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.DialogFragment;
import android.util.Log;

public class NewDBDataDialog extends DialogFragment implements
		DialogInterface.OnClickListener {
	private UpdateDBListener updateDBListener;
	private AlertDialog dialog;

	public interface UpdateDBListener {
		void onUpdateDecision(boolean doUpdate);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.updateDBListener = (UpdateDBListener) activity;
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Install updated dictionary from server?");
        builder.setPositiveButton("Yes", this)
        		.setNegativeButton("No", this);
		this.dialog = builder.create();
		return dialog;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// Stop the user clicking again, just because it's taking a long
		// time and he thinks he didn't press it properly.
		this.dialog.getButton(Dialog.BUTTON_POSITIVE).setEnabled(false);
		Log.d("NewDBDataDialog", "onClick(" + which + ")");
		this.updateDBListener.onUpdateDecision(which == DialogInterface.BUTTON_POSITIVE);
	}
}
