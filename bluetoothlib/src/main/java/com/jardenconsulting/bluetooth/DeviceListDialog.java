package com.jardenconsulting.bluetooth;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jardenconsulting.bluetoothapplib.BuildConfig;
import com.jardenconsulting.bluetoothapplib.R;

import java.util.Set;

public class DeviceListDialog extends DialogFragment
		implements OnClickListener, OnItemClickListener {
	// callback listener:
	private DeviceListListener deviceListListener;
	
    private BluetoothAdapter bluetoothAdapter;
    private ArrayAdapter<String> pairedDevicesAdapter;
    private ArrayAdapter<String> newDevicesAdapter;
	private AlertDialog alertDialog;
	private Button scanButton;
	private ProgressBar progressBar;
	private TextView titleNewDevices;

	public void setListener(DeviceListListener deviceListListener) {
		this.deviceListListener = deviceListListener;
	}

	@SuppressLint("InflateParams")
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) {
        	String stateStr = (savedInstanceState==null?"":"not ") + "null";
        	Log.d(BluetoothFragment.TAG, "DeviceListDialog.onCreateDialog(" +
        			stateStr + ")");
        }
		Activity activity = getActivity();
		pairedDevicesAdapter = new ArrayAdapter<>(activity, R.layout.device_name);
		newDevicesAdapter = new ArrayAdapter<>(activity, R.layout.device_name);

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle(R.string.select_device);
		LayoutInflater inflater = activity.getLayoutInflater();
		View view = inflater.inflate(R.layout.device_list, null);
        ListView pairedListView = view.findViewById(R.id.paired_devices);
        pairedListView.setAdapter(pairedDevicesAdapter);
        pairedListView.setOnItemClickListener(this);
        
        ListView newDevicesListView = view.findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(newDevicesAdapter);
        newDevicesListView.setOnItemClickListener(this);
        
        this.titleNewDevices = view.findViewById(R.id.title_new_devices);

        this.scanButton = view.findViewById(R.id.button_scan);
		this.scanButton.setOnClickListener(this);
        builder.setView(view);
        this.progressBar = view.findViewById(R.id.progressBar);
        this.progressBar.setVisibility(View.GONE);

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        activity.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        activity.registerReceiver(mReceiver, filter);

        // Get the local Bluetooth adapter
		this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = this.bluetoothAdapter.getBondedDevices();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            view.findViewById(R.id.title_paired_devices).setVisibility(View.VISIBLE);
            for (BluetoothDevice device : pairedDevices) {
            	pairedDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            pairedDevicesAdapter.add(noDevices);
        }
		this.alertDialog = builder.create();
		return this.alertDialog;
	}

	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int arg2, long arg3) {
        // Cancel discovery because it's costly and we're about to connect
        this.bluetoothAdapter.cancelDiscovery();

        // Get the device MAC address, which is the last 17 chars in the View
		String info = ((TextView) view).getText().toString();
        String address = info.substring(info.length() - 17);
        deviceListListener.setMACAddress(address);
        this.alertDialog.cancel();
	}

	@Override
	public void onClick(View view) {
		doDiscovery();
		view.setVisibility(View.GONE);
	}

    /**
     * Start device discover with the BluetoothAdapter
     */
	private void doDiscovery() {
        if (BuildConfig.DEBUG) {
        	Log.d(BluetoothFragment.TAG, "DeviceListDialog.doDiscovery()");
        }

        // Indicate scanning in the title
        this.alertDialog.setTitle(R.string.scanning);

		this.scanButton.setVisibility(View.GONE);
        this.progressBar.setVisibility(View.VISIBLE);
        // Turn on sub-title for new devices
        this.titleNewDevices.setVisibility(View.VISIBLE);

        // If we're already discovering, stop it
        if (this.bluetoothAdapter.isDiscovering()) {
        	this.bluetoothAdapter.cancelDiscovery();
        }

        // Request discover from BluetoothAdapter
        this.bluetoothAdapter.startDiscovery();
	}
	
    @Override
	public void onDestroy() {
        if (BuildConfig.DEBUG) {
        	Log.d(BluetoothFragment.TAG, "DeviceListDialog.onDestroy()");
        }
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (this.bluetoothAdapter != null) {
        	this.bluetoothAdapter.cancelDiscovery();
        }

        // Unregister broadcast listeners
        getActivity().unregisterReceiver(mReceiver);
    }

    // The BroadcastReceiver that listens for discovered devices and
    // changes the title when discovery is finished
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    newDevicesAdapter.add(device.getName() + "\n" + device.getAddress());
                }
            // When discovery is finished, change the Activity title
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
            	DeviceListDialog.this.progressBar.setVisibility(View.GONE);
            	DeviceListDialog.this.alertDialog.setTitle(R.string.select_device);
                if (newDevicesAdapter.getCount() == 0) {
                    String noDevices = getResources().getText(R.string.none_found).toString();
                    newDevicesAdapter.add(noDevices);
                }
            }
        }
    };

}

