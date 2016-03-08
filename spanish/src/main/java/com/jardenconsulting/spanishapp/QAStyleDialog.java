package com.jardenconsulting.spanishapp;

import jarden.provider.engspa.EngSpaContract;
import jarden.provider.engspa.EngSpaContract.QAStyle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

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
		/*!!
        String qaStyleStr = EngSpaContract.qaStyleNames[which];
		QAStyle qaStyle = QAStyle.valueOf(qaStyleStr);
		*/
        QAStyle qaStyle = QAStyle.values()[which];
		qaStyleListener.onQAStyleSelected(qaStyle);
	}
}
