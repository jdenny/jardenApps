package jarden;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class DoWorkInThread {
	private Activity activity;
	private TextView textView;
	private Handler handler;
	private String message;
	
	public DoWorkInThread(Activity act, TextView tv) {
		this.activity = act;
		this.textView = tv;
		textView.setText("10");
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 10; i >= 0; --i) {
					message = "count down: " + i;
					if (i == 0) message = "done";
					activity.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							textView.setText(message);
						}
					});
					try { Thread.sleep(1000); }
					catch (InterruptedException e) {
						Log.e("android course", "DoWorkInThread() InterruptedException");
					}
				}
			}
		}).start();
	}
	public DoWorkInThread(TextView tv) {
		this.textView = tv;
		this.handler = new Handler();
		new Thread(new Runnable() {
			@Override
			public void run() {
				for (int i = 1; i <= 10; ++i) {
					message = "count up: " + i;
					if (i == 10) message = "done";
					handler.post(new Runnable() {
						@Override
						public void run() {
							textView.setText(message);
						}
					});
					try { Thread.sleep(1000); }
					catch (InterruptedException e) {
						Log.e("android course", "DoWorkInThread() InterruptedException");
					}
				}
			}
		}).start();
	}
	public DoWorkInThread(Handler hler) {
		this.handler = hler;
		new Thread(new Runnable() {
			@Override
			public void run() {
		        Message msg = handler.obtainMessage(22, 52, 61);
		        Bundle bundle = new Bundle();
		        bundle.putString("name", "pops");
		        msg.setData(bundle);
		        msg.sendToTarget();
		        // handler.sendMessage(msg); // same as above
			}
		}).start();
	}
}
