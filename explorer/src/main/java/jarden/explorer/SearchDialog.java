package jarden.explorer;

import com.jardenconsulting.explorer.BuildConfig;
import com.jardenconsulting.explorer.ExplorerActivity;
import com.jardenconsulting.explorer.R;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.EditText;

public class SearchDialog extends DialogFragment {
	private EditText searchEditText;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG,
					"SearchDialog.onCreate(savedInstanceState" +
					(savedInstanceState==null?"":"!") + "=null)");
		}
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		if (BuildConfig.DEBUG) {
			Log.d(ExplorerActivity.TAG,
					"SearchDialog.onCreateDialog(savedInstanceState" +
					(savedInstanceState==null?"":"!") + "=null)");
		}
		Activity activity = getActivity();
		OnClickListener onClickListener = (OnClickListener) activity;
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		this.searchEditText= new EditText(activity);
		searchEditText.setText("*.mp4"); // default value
		builder.setMessage(R.string.searchStr)
				.setPositiveButton(R.string.okStr, onClickListener)
				.setView(searchEditText)
				.setNegativeButton(R.string.cancelStr, onClickListener);
		return builder.create();
	}

	public String getSearchExpr() {
		return this.searchEditText.getText().toString();
	}

}
