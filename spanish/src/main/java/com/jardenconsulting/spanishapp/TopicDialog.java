package com.jardenconsulting.spanishapp;

import jarden.provider.engspa.EngSpaContract;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class TopicDialog extends DialogFragment implements DialogInterface.OnClickListener {
	
	public interface TopicListener {
		void onTopicSelected(String topic);
	}

	private TopicListener topicListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.topicListener = (TopicListener) activity;
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
	    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	    builder.setTitle(R.string.chooseTopicStr)
	           .setItems(EngSpaContract.attributeNames, this);
	    return builder.create();
	}
	@Override
	public void onClick(DialogInterface dialog, int which) {
		topicListener.onTopicSelected(EngSpaContract.attributeNames[which]);
	}

}
