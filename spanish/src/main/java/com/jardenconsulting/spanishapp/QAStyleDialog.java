package com.jardenconsulting.spanishapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import jarden.provider.engspa.EngSpaContract;
import jarden.provider.engspa.EngSpaContract.QAStyle;

public class QAStyleDialog extends DialogFragment implements DialogInterface.OnClickListener {
	
	public interface QAStyleListener {
		void onQAStyleSelected(QAStyle qaStyle);
	}

	private QAStyleListener qaStyleListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.qaStyleListener = (QAStyleListener) activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.chooseQAStyleStr)
	           .setItems(EngSpaContract.qaStyleNames, this);
	    return builder.create();
	}
	@Override
	public void onClick(DialogInterface dialog, int which) {
        QAStyle qaStyle = QAStyle.values()[which];
		qaStyleListener.onQAStyleSelected(qaStyle);
	}
}
