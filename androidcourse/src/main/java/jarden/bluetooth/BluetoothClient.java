package jarden.bluetooth;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class BluetoothClient implements Runnable {
	private BluetoothActivity bluetoothActivity;
    private final BluetoothSocket socket;
    private BluetoothDevice mmDevice;
    private OutputStream socketOutputStream;
    private String action;
    private String message;
 
    public BluetoothClient(BluetoothDevice device, BluetoothActivity bluetoothActivity)
    		throws IOException {
    	this.bluetoothActivity = bluetoothActivity;
        mmDevice = device;
 
        // Get a BluetoothSocket to connect with the given BluetoothDevice
        // MY_UUID is the app's UUID string, also used by the server code
    	UUID amazeQuizUUID = UUID.fromString(BluetoothServer.AMAZE_QUIZ_UUID_STR);
        socket = mmDevice.createRfcommSocketToServiceRecord(amazeQuizUUID);
        connect();
    }
    public void connect() {
    	this.action = "connect";
    	Thread thread = new Thread(this);
    	thread.start();
    }
    public void sendMessage(String message) {
    	this.action = "write";
    	this.message = message;
    	Thread thread = new Thread(this);
    	thread.start();
    }
    public void close() {
    	this.action = "close";
    	Thread thread = new Thread(this);
    	thread.start();
    }

    public void run() {
 
        try {
        	if (action.equals("connect")) {
                socket.connect();
                bluetoothActivity.logMessageOnUI("client now connected to server");
    			// InputStream is = socket.getInputStream();
    			socketOutputStream = socket.getOutputStream();
        	} else if (action.equals("write")) {
    			socketOutputStream.write(message.getBytes(BluetoothServer.CHARSET));
    			socketOutputStream.flush();
        	} else if (action.equals("close")) {
        		stop();
        	} else {
        		throw new IllegalStateException("unrecognised action: " + action);
        	}
        } catch (IOException ioe) {
        	bluetoothActivity.logMessageOnUI("ioe: " + ioe);
        	stop();
        }
    }
 
    /** Will cancel an in-progress connection, and close the socket */
    public void stop() {
        try {
            socket.close();
        } catch (IOException ioe) {
        	bluetoothActivity.logMessageOnUI("ioe: " + ioe);
        }
    }
}