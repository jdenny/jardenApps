package jarden.app.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
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
	private EditText intValueEditText;
	private int intValue = 1;
	private IntValueListener intValueListener;
	private AlertDialog alertDialog;
    private String title = "IntegerDialog";

    public interface IntValueListener {
        void onUpdateIntValue(int intValue);
	}
    public void setTitle(String title) {
        this.title = title;
    }
	public void setIntValue(int intValue) {
	    this.intValue = intValue;
    }
    public void setIntValueListener(IntValueListener listener) {
        intValueListener = listener;
    }

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Activity activity = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		LayoutInflater inflater = activity.getLayoutInflater();
		builder.setTitle(this.title);
		View view = inflater.inflate(R.layout.dialog_integer, null);
        this.intValueEditText = view.findViewById(R.id.intValueEditText);
        intValueEditText.setText(String.valueOf(this.intValue));
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
			String intValueStr = intValueEditText.getText().toString();
			int intValue;
            try {
                intValue = Integer.parseInt(intValueStr);
            } catch (NumberFormatException nfe) {
                intValue = -1;
            }
            this.intValueListener.onUpdateIntValue(intValue);
		}
	}
}
