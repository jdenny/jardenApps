package jarden.bluetooth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

import com.jardenconsulting.androidcourse.R;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BluetoothActivity extends Activity implements OnItemClickListener {
	private static final int REQUEST_ENABLE_BT = 1101;
	private BluetoothAdapter mBluetoothAdapter;
	private boolean bluetoothEnabledByUs = false;
	private ListView deviceList;
	private BluetoothDevice serverDevice;
	private BluetoothServer bluetoothServer;
	private BluetoothClient bluetoothClient;
	private TextView clientMessageView;
	private TextView statusView;
	private TextView bluetoothServerAddressView;
	private ArrayList<BluetoothDevice> pairedDevices;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		this.deviceList = (ListView) this.findViewById(R.id.deviceList);
		this.deviceList.setOnItemClickListener(this);
		this.clientMessageView = (TextView) this.findViewById(R.id.clientMessage);
		this.statusView = (TextView) this.findViewById(R.id.status);
		this.bluetoothServerAddressView = (TextView) this.findViewById(R.id.bluetoothServerAddress);
		
		pairedDevices = new ArrayList<BluetoothDevice>();
		mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (mBluetoothAdapter == null) {
			logMessage("device does not support Bluetooth!");
			this.finish();
			return;
		}
		if (!mBluetoothAdapter.isEnabled()) {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
			//! logMessage("Bluetooth is not enabled on this device!");
		} else {
			refreshButton(null);
		}
	}
	
	/**
	 * Handle the results from the activity to turn on bluetooth.
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_ENABLE_BT) {
			logMessage("Bluetooth now enabled on this device");
			this.bluetoothEnabledByUs = true;
			refreshButton(null);
		} else {
			logMessage("Bluetooth NOT enabled on this device!");
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBluetoothAdapter != null && this.bluetoothEnabledByUs) {
			mBluetoothAdapter.disable();
			mBluetoothAdapter = null;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
		this.serverDevice = this.pairedDevices.get(position);
		this.bluetoothServerAddressView.setText(this.serverDevice.getAddress());
		try {
			bluetoothClient = new BluetoothClient(this.serverDevice, this);
			logMessage("client connection attempted");
		} catch (IOException e) {
			logMessage("exception starting client: " + e);
		}
	}

    /**
     * Get paired bluetooth devices, save and display in a ListView.
     * @param view
     */
	public void refreshButton(View view) {
		Set<BluetoothDevice> deviceSet = mBluetoothAdapter.getBondedDevices();
		pairedDevices.clear();
		pairedDevices.addAll(deviceSet);
		if (pairedDevices.size() > 0) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1);
			String deviceName;
		    for (BluetoothDevice device : deviceSet) {
		    	deviceName = device.getName();
		        adapter.add(deviceName + " " + device.getAddress());
		    }
		    deviceList.setAdapter(adapter);
		}
	}

	public void startServerButton(View view) {
		try {
			bluetoothServer = new BluetoothServer(mBluetoothAdapter, this);
			Thread thread = new Thread(bluetoothServer);
			thread.start();
			logMessage("server started");
		} catch (IOException e) {
			logMessage("exception starting server: " + e);
		} 
	}
	
	public void stopServerButton(View view) {
		bluetoothServer.stop();
		logMessage("client connection closed");
	}

	public void stopClientButton(View view) {
		bluetoothClient.close();
		logMessage("client connection closed");
	}

	public void sendMessageButton(View view) {
		String message = this.clientMessageView.getText().toString();
		bluetoothClient.sendMessage(message);
		logMessage("client thread started");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_bluetooth, menu);
		return true;
	}
	public void logMessage(String message) {
		Toast.makeText(this, message, Toast.LENGTH_LONG).show();
	}

	public void logMessageOnUI(String message) {
		LogOnUIRunnable logRunnable = new LogOnUIRunnable(message);
		runOnUiThread(logRunnable);
	}
	class LogOnUIRunnable implements Runnable {
		private String message;
		
		public LogOnUIRunnable(String message) {
			this.message = message;
		}

		@Override
		public void run() {
			statusView.setText(message);
			// logMessage(message);
		}
	}
}
