package com.jardenconsulting.bluetooth;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.jardenconsulting.bluetoothapplib.BuildConfig;
import com.jardenconsulting.bluetoothapplib.R;

public class BluetoothFragment extends Fragment
		implements OnClickListener, DeviceListListener {
    public static final String TAG = "BluetoothService";
    // preferences key
    private static final String PLAYER_NAME_KEY = "playerName";

	// tag for DeviceListDialog
	private static final String DLD = "dld";

    // Intent request codes:
    private static final int REQUEST_ENABLE_BT = 2;
    
	// private TextView hintText; // may use this later for more hints
	private BluetoothAdapter bluetoothAdapter;
	private BluetoothService bluetoothService = null;
	private boolean bluetoothEnabledByUs = false;
	private BluetoothListener bluetoothListener;
	private DeviceListDialog deviceListDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		bluetoothListener = (BluetoothListener) getActivity();
        if(BuildConfig.DEBUG) {
        	Log.d(TAG, "BluetoothFragment.onCreateView(bundle=" +
        			(savedInstanceState==null?"":"not ") + "null)");
        }
        this.deviceListDialog = (DeviceListDialog) getFragmentManager().findFragmentByTag(DLD);
        if (this.deviceListDialog == null) {
        	this.deviceListDialog = new DeviceListDialog();
        }
		this.deviceListDialog.setListener(this);
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (bluetoothAdapter == null) {
			bluetoothListener.setStatusMessage("Bluetooth not supported on this device");
			bluetoothListener.setBluetoothService(null);
            return null;
		}
		View view = inflater.inflate(R.layout.btfragment_layout, container, false);
		Button connectButton = view.findViewById(R.id.connectButton);
		Button discoverableButton = view.findViewById(R.id.discoverableButton);
		connectButton.setOnClickListener(this);
		discoverableButton.setOnClickListener(this);
		TextView helpTextView = view.findViewById(R.id.helpText);
		String helpString = bluetoothListener.getHelpString();
		helpTextView.setText(helpString);
		return view;
	}
	/**
	 * Try to retrieve playerName from SharedPreferences.
	 * If not there, return default name
	 * @param activity
	 * @return
	 */
	public static String getPlayerName(Activity activity) {
		SharedPreferences preferences = activity.getPreferences(Activity.MODE_PRIVATE);
		return preferences.getString(PLAYER_NAME_KEY, "");
	}
	public static void setPlayerName(Activity activity, String playerName) {
        SharedPreferences prefs = activity.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PLAYER_NAME_KEY, playerName);
        editor.apply();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(BuildConfig.DEBUG) Log.d(TAG, "BluetoothFragment.onStart()");
		if (this.bluetoothAdapter == null) return;
        if (bluetoothAdapter.isEnabled()) {
			if (bluetoothService == null) setupBluetooth();
		} else {
		    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
		    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}
    }

    @Override
	public void onResume() {
		super.onResume();
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "BluetoothFragment.onResume()");
		}
		if (this.bluetoothAdapter == null) return;

		// Performing this check in onResume() covers the case in which BT was
		// not enabled during onStart(), so we were paused to enable it...
		// onResume() will be called when ACTION_REQUEST_ENABLE activity
		// returns.
		if (bluetoothService != null) {
			// Only if the state is STATE_NONE, do we know that we haven't
			// started already
			if (bluetoothService.getState() == BluetoothService.BTState.none) {
				bluetoothService.startAcceptThread();
			}
		}
	}

    @Override
	public void onClick(View view) {
		int id = view.getId();
		if (id == R.id.connectButton) {
            // Launch the DeviceListDialog to see devices and do scan
			this.deviceListDialog.show(getActivity().getSupportFragmentManager(), DLD);
		} else if (id == R.id.discoverableButton) {
            // Ensure this device is discoverable by others
            ensureDiscoverable();
		} else {
			throw new IllegalStateException("unrecognised button clicked: " + view);
		}
	}
    @Override
    public void onDestroy() {
    	super.onDestroy();
    	close();
    }
	private void ensureDiscoverable() {
		if (BuildConfig.DEBUG) {
			Log.d(TAG, "BluetoothFragment.ensureDiscoverable()");
		}
		if (this.bluetoothAdapter.getScanMode() !=
				BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
			Intent discoverableIntent = new Intent(
					BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
			discoverableIntent.putExtra(
					BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
			startActivity(discoverableIntent);
		}
	}
	/**
	 * Handle the results from startActivityForResult().
	 */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		String methodName ="BluetoothFragment.onActivityResult(";
		if(BuildConfig.DEBUG) {
			Log.d(TAG, methodName +
					requestCode + ", " + resultCode + ")");
		}
		if (requestCode == REQUEST_ENABLE_BT) {
			if (resultCode == Activity.RESULT_OK) {
				bluetoothListener.setStatusMessage("Bluetooth now enabled on this device");
				this.bluetoothEnabledByUs = true;
				setupBluetooth();
			}
			else {
				String message = methodName + "): BT not enabled by user";
				if(BuildConfig.DEBUG) {
					Log.d(TAG, message);
				}
				bluetoothListener.setStatusMessage("Bluetooth not enabled by user");
				bluetoothListener.setBluetoothService(null);
			}
		} else {
			String message = methodName + ") unexpected requestCode: " +
					requestCode;
			Log.e(TAG, message);
			bluetoothListener.setStatusMessage(message);
		}
	}
    public void close() {
        if(BuildConfig.DEBUG) {
        	Log.d(TAG, "BluetoothFragment.close()");
        }
        if (bluetoothService != null) bluetoothService.stop();
		if (bluetoothAdapter != null && this.bluetoothEnabledByUs) {
			bluetoothAdapter.disable();
			bluetoothAdapter = null;
		}
    }
    private void setupBluetooth() {
        if(BuildConfig.DEBUG) {
        	Log.d(TAG, "BluetoothFragment.setupBluetooth()");
        }
        // Initialise the BluetoothService to perform bluetooth connections
		this.bluetoothService = new BluetoothService(
				this, // so can be notified when connected
				this.bluetoothAdapter,
				this.bluetoothListener);
		bluetoothListener.setBluetoothService(bluetoothService);
    }

	@Override
	public void setMACAddress(String address) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        bluetoothService.connect(device);
	}

	public void setConnected(boolean serverMode) {
        if (deviceListDialog != null) {
        	Dialog dialog = deviceListDialog.getDialog();
        	if (dialog != null) {
        		deviceListDialog.dismiss();
        	}
        }
	}

}
