package com.jardenconsulting.bluetooth;

import com.jardenconsulting.bluetooth.BluetoothService.BTState;
import com.jardenconsulting.bluetoothapplib.BuildConfig;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class BluetoothHandler extends Handler {
	private BluetoothListener btListener;
	private BTState[] stateValues;
	
	public BluetoothHandler(BluetoothListener btListener) {
		super();
		this.btListener = btListener;
		this.stateValues = BluetoothService.BTState.values();
	}
	
    @Override
    public void handleMessage(Message message) {
    	int what = message.what;
        if(BuildConfig.DEBUG) Log.i(BluetoothFragment.TAG, "handleMessage; what=" + what);
    	if (what == BluetoothService.MESSAGE_STATE_CHANGE) {
            if (BuildConfig.DEBUG) {
            	Log.i(BluetoothFragment.TAG, "MESSAGE_STATE_CHANGE: " + message.arg1);
            }
            this.btListener.onStateChange(this.stateValues[message.arg1]);
    	} else if (what == BluetoothService.MESSAGE_SERVER_DEVICE_NAME) {
    		Bundle bundle = message.getData();
    		btListener.onConnectedAsClient(bundle.getString(BluetoothService.DEVICE_NAME));
    	} else if (what == BluetoothService.MESSAGE_CLIENT_DEVICE_NAME) {
    		Bundle bundle = message.getData();
    		btListener.onConnectedAsServer(bundle.getString(BluetoothService.DEVICE_NAME));
    	} else if (what == BluetoothService.MESSAGE_READ) {
    		byte[] messageBytes = (byte[]) message.obj;
    		btListener.onMessageRead(messageBytes);
    	} else if (what == BluetoothService.MESSAGE_TOAST) {
    		btListener.onMessageToast(message.getData().getString(BluetoothService.TOAST));
    	} else if (what == BluetoothService.MESSAGE_DEVICE_CONNECTION_LOST) {
    		btListener.onConnectionLost();
    	} else {
        	btListener.onError("handler unrecognised 'what': " + what);
    	}
    }
}

