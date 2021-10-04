package com.jardenconsulting.androidcourse;

import java.io.File;

import jarden.DoWorkInThread;
import jarden.bluetooth.BluetoothActivity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
MainActivity
	top line shows number of times onCreate() is called
		e.g. on screen rotation
	Threads and Threads2 buttons:
		call DoWorkInThread constructor
	select activity from list (see below): invoke that activity
	Go: call DisplayMessageActivity with supplied string
		DisplayMessageActivity programmatically adds a TextView
	Connect: call BluetoothActivity
		send messages between 2 devices
	Phone: pass supplied number to device telephone activity
	New (contact): this is embedded ContractsFragment
		create new contact and add to listView
	
AutoCompleteActivity - uses AutoCompleteTextView to auto-complete an input field
PhotoActivity - nothing yet!
ExplorerApp - separate App.

AirplaneModeReceiver - BroadcastReceiver that is notified if airplane mode
	is changed on the device 

 * @author john.denny@gmail.com
 *
 */
/*
 * TODO: have two projects, one at latest API version and other
 * at low API. Get code working on high API, then port it to low.
 * Note: normally MainActivity would extend android.app.Activity
 * but because we want to use Fragments AND we want to support API versions
 * below 11, we extend FragmentActivity. Similarly, fragments from API 11 up
 * extend android.app.Fragment, but because we want to support old APIs, our
 * fragments extend android.support.v4.app.Fragment
 * @author john.denny@gmail.com
 */
public class MainActivity extends AppCompatActivity implements OnItemClickListener {
	public static final String TAG = "AndroidCourse";
	public static final String EXTRA_MESSAGE =
			"com.jardenconsulting.androidcourse.MESSAGE";
	// TODO: make this an enum! link name with class
	private static final String[] activityNames = {
			"Quiz",
			"Explorer",
			"Progress",
			"Layouts",
			"Dynamic Layout",
			"Video",
			"AutoComplete",
			"TextToSpeech"
	};
	private static final int CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE = 1001;
	private static final String COUNT_NAME = "createCount";
	private int createCount = 0;
	private TextView threadView;
	private Handler handler;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState != null) {
			createCount = savedInstanceState.getInt(COUNT_NAME);
		}
		++createCount;
		setContentView(R.layout.activity_main);
		TextView createCountView = (TextView) findViewById(R.id.createCount);
		createCountView.setText(String.valueOf(createCount));
		threadView = (TextView) findViewById(R.id.threadView);
		handler = new MyHandler(threadView);
		ListView activityList = (ListView) findViewById(R.id.activityList);
		ArrayAdapter<String> sla = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1,
				activityNames);
		activityList.setAdapter(sla);
		activityList.setOnItemClickListener(this);
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(COUNT_NAME, createCount);
	}

	public void goButton(View view) {
		String logMessage = "go button pressed";
		if (BuildConfig.DEBUG) {
			Log.i(TAG, logMessage);
		}
		Intent intent = new Intent(this, DisplayMessageActivity.class);
		EditText editText = (EditText) this.findViewById(R.id.editMessage);
		String message = editText.getText().toString();
		intent.putExtra(EXTRA_MESSAGE, message);
		startActivity(intent);
	}

	public void threadButton(View view) {
		new DoWorkInThread(handler);
	}

	public void threadButton2(View view) {
		new DoWorkInThread(threadView);
	}

	public void connectButton(View view) {
		Intent intent = new Intent(this, BluetoothActivity.class);
		startActivity(intent);
	}
	
	public void phoneButton(View view) {
		EditText phoneEdit = (EditText) findViewById(R.id.phoneEdit);
		String phone = "tel:" + phoneEdit.getText();
		Uri number = Uri.parse(phone);
		Intent phoneIntent = new Intent(Intent.ACTION_DIAL, number);
		startActivity(phoneIntent);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
    private static class MyHandler extends Handler {
    	private TextView textView;
    	
		public MyHandler(TextView textView) {
    		this.textView = textView;
    	}
        @Override
        public void handleMessage(Message msg) {
        	textView.setText("what=" + msg.what + "; arg1=" + msg.arg1 +
        			"; arg2=" + msg.arg2 +
        			"; name=" + msg.getData().getString("name"));
        }
    	
    }

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view,
			int position, long id) {
		String activityName = activityNames[position];
		if (activityName.equals("Video")) {
			Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			File sdcardDir = Environment.getExternalStorageDirectory();
			File mediaFile = new File(sdcardDir, "john.mp4");
			Uri fileUri = Uri.fromFile(mediaFile);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
			startActivityForResult(intent, CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE);
		} else if (activityName.equals("AutoComplete")) {
			Intent intent = new Intent(this, AutoCompleteActivity.class);
			startActivity(intent);
		} else if (activityName.equals("Quiz")) {
			Intent intent = new Intent(this, QuizActivity.class);
			startActivity(intent);
		} else if (activityName.equals("Explorer")) {
			Intent intent = new Intent("com.jardenconsulting.intent.action.EXPLORER");
			startActivity(intent);
		} else if (activityName.equals("Progress")) {
			Intent intent = new Intent(this, ProgressActivity.class);
			startActivity(intent);
		} else if (activityName.equals("Layouts")) {
			Intent intent = new Intent(this, LayoutsActivity.class);
			startActivity(intent);
		} else if (activityName.equals("Dynamic Layout")) {
			Intent intent = new Intent(this, DynamicFormActivity.class);
			String[] fieldNames = {
				"name", "email", "phone", "mobile"
			};
			intent.putExtra("fieldNames", fieldNames);
			startActivity(intent);
		} else if (activityName.equals("TextToSpeech")) {
			Intent intent = new Intent(this, TextToSpeechActivity.class);
			startActivity(intent);
		} else {
			Toast.makeText(this, "unexpected item clicked, position: " + position,
					Toast.LENGTH_LONG).show();
		}
	}
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "requestCode=" + requestCode + "; resultCode=" + resultCode);
		if (requestCode == CAPTURE_VIDEO_ACTIVITY_REQUEST_CODE) {
	        if (resultCode == RESULT_OK) {
	            // Video captured and saved to fileUri specified in the Intent
	            Toast.makeText(this, "Video saved", Toast.LENGTH_LONG).show();
	        } else if (resultCode == RESULT_CANCELED) {
	            Toast.makeText(this, "video canceled", Toast.LENGTH_LONG).show();
	        } else {
	            Toast.makeText(this, "video failed", Toast.LENGTH_LONG).show();
	        }
	    }
		
	}
}
