package jarden;

import com.jardenconsulting.androidcourse.BuildConfig;
import com.jardenconsulting.androidcourse.MainActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AirplaneModeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String message;
		if (intent.getAction().equals("android.intent.action.AIRPLANE_MODE")) {
			message = "Airplane mode changed";
		} else {
			message = "unrecognised intent action:" + intent.getAction();
		}
		Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		if (BuildConfig.DEBUG) {
			Log.d(MainActivity.TAG, "AirplaneModeReceiver.onReceive(); " +
					message);
		}
	}
}
