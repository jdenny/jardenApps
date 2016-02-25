package com.jardenconsulting.bluetooth;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import com.jardenconsulting.bluetoothapplib.BuildConfig;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * This class does all the work for setting up and managing Bluetooth
 * connections with other devices. It has a thread that listens for
 * incoming connections, a thread for connecting with a device, and a
 * thread for performing data transmissions when connected.
 */
public class BluetoothService {

	public static enum BTState {
		none,       // doing nothing
		listening,  // listening for incoming connections, using AcceptThread
		connecting, // initiating an outgoing connection, using ConnectThread
		connected   // connected to a remote device, using ConnectedThread
	}
    // Message types ('what') sent from the BluetoothService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    // i.e. we are the client, and we are returning the name of the server device:
    public static final int MESSAGE_SERVER_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // i.e. we are the server, and we are returning the name of the client device:
    public static final int MESSAGE_CLIENT_DEVICE_NAME = 6;
    public static final int MESSAGE_DEVICE_CONNECTION_LOST = 7;
    public static final int MESSAGE_CONNECTED = 8; // used by SocketChannel, not Bluetooth

    // Key names received from the BluetoothService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Name for the SDP record when creating server socket
    private static final String BLUETOOTH_SERVICE_NAME = "BluetoothService";

    // Unique UUID for this application
    private static final UUID BLUETOOTH_UUID =
        UUID.fromString("398e3790-7d9e-4ced-87cf-e06321010ae6");

    // Member fields
	private BluetoothFragment bluetoothFragment;
    private final BluetoothAdapter mAdapter;
    private Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private BTState state;
	private boolean stopping;

	public BluetoothService(BluetoothFragment bluetoothFragment,
			BluetoothAdapter btAdapter, BluetoothListener btListener) {
        this.bluetoothFragment = bluetoothFragment;
		this.mAdapter = btAdapter;
		this.state = BTState.none;
        this.mHandler = new BluetoothHandler(btListener);
    }

    /**
     * Set the current state of the bluetooth connection
     * @param state  An integer defining the current connection state
     */
    private synchronized void setState(BTState state) {
        if (BuildConfig.DEBUG) {
        	Log.d(BluetoothFragment.TAG, "setState() " + this.state + " -> " + state);
        }
        this.state = state;

        // Give the new state to the Handler so the UI Activity can update
        mHandler.obtainMessage(MESSAGE_STATE_CHANGE, state.ordinal(), -1).sendToTarget();
    }

    /**
     * Return the current connection state.
     */
    public synchronized BTState getState() {
        return this.state;
    }

    /**
     * Start the bluetooth service. Specifically start AcceptThread to begin a
     * session in listening (server) mode. Called by the Activity onResume() */
    public synchronized void startAcceptThread() {
        if (BuildConfig.DEBUG) {
        	Log.d(BluetoothFragment.TAG, "BluetoothService.startAcceptThread()");
        }

        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to listen on a BluetoothServerSocket
        if (mAcceptThread == null) {
            mAcceptThread = new AcceptThread();
            mAcceptThread.start();
        }
        setState(BTState.listening);
    }

    /**
     * Start the ConnectThread to initiate a connection to a remote device.
     * @param device  The BluetoothDevice to connect
     */
    public synchronized void connect(BluetoothDevice device) {
        if (BuildConfig.DEBUG) {
        	Log.d(BluetoothFragment.TAG, "BluetoothService.connect(" + device + ")");
        }

        // Cancel any thread attempting to make a connection
        if (this.state == BTState.connecting) {
            if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Start the thread to connect with the given device
        mConnectThread = new ConnectThread(device);
        mConnectThread.start();
        setState(BTState.connecting);
    }

    /**
     * Start the ConnectedThread to begin managing a Bluetooth connection
     * @param socket  The BluetoothSocket on which the connection was made
     * @param device  The BluetoothDevice that has been connected
     * @param serverMode added by John; bluetooth client -> knowMe client;
     * 		bluetooth server -> knowMe server, i.e. runs KnowMeService
     */
    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device,
            boolean serverMode) {
        if (BuildConfig.DEBUG) {
        	Log.d(BluetoothFragment.TAG, "BluetoothService.connected(" +
        			socket + ", " + device + ", " + serverMode);
        }

        // Cancel the thread that completed the connection
        if (mConnectThread != null) {mConnectThread.cancel(); mConnectThread = null;}

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {mConnectedThread.cancel(); mConnectedThread = null;}

        // Cancel the accept thread because we only want to connect to one device
        if (mAcceptThread != null) {
        	mAcceptThread.cancel();
        	mAcceptThread = null;
        }

        // Start the thread to manage the connection and perform transmissions
        mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();

        // Tell BluetoothFragment we are connected:
        this.bluetoothFragment.setConnected(serverMode);
        // Send the name of the connected device back to the UI Activity
        int messageNumber = serverMode?MESSAGE_CLIENT_DEVICE_NAME:MESSAGE_SERVER_DEVICE_NAME;
        Message msg = mHandler.obtainMessage(messageNumber);
        Bundle bundle = new Bundle();
        bundle.putString(DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);

        setState(BTState.connected);
    }

    /**
     * Stop all threads
     */
    public synchronized void stop() {
    	Exception e = new Exception("stop() called");
    	Log.e(BluetoothFragment.TAG, Log.getStackTraceString(e));
    	this.stopping = true;
        if (BuildConfig.DEBUG) {
        	Log.d(BluetoothFragment.TAG, "BluetoothService.stop()");
        }

        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        if (mAcceptThread != null) {
        	mAcceptThread.cancel();
        	mAcceptThread = null;
        }

        setState(BTState.none);
    }

    /**
     * Write to the ConnectedThread in an unsynchronized manner
     * @param out The bytes to write
     * @see ConnectedThread#write(byte[])
     */
    public void write(byte[] out) {
        // Create temporary object
        ConnectedThread r;
        // Synchronize a copy of the ConnectedThread
        synchronized (this) {
        	if (this.state != BTState.connected) return;
            r = mConnectedThread;
        }
        // Perform the write unsynchronized
        r.write(out);
    }

    /**
     * Indicate that the connection attempt failed and notify the UI Activity.
     */
    private void connectionFailed() {
        setState(BTState.listening);

        // Send a failure message back to the Activity
        Message msg = mHandler.obtainMessage(MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }


    /**
     * This thread runs while listening for incoming connections. It behaves
     * like a server-side client. It runs until a connection is accepted
     * (or until cancelled).
     */
    private class AcceptThread extends Thread {
        // The local server socket
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            // Create a new listening server socket
            try {
                tmp = mAdapter.listenUsingInsecureRfcommWithServiceRecord(
                		BLUETOOTH_SERVICE_NAME, BLUETOOTH_UUID);
            } catch (IOException e) {
                Log.e(BluetoothFragment.TAG,
                		"BluetoothService listenUsingInsecureRfcomm() failed: " + e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            if (BuildConfig.DEBUG) {
            	Log.d(BluetoothFragment.TAG, "BluetoothService$AcceptThread.run()");
            }
            setName("AcceptThread");
            BluetoothSocket socket = null;

            // Listen to the server socket if we're not connected
            while (BluetoothService.this.state != BTState.connected) {
                try {
                    // This is a blocking call and will only return on a
                    // successful connection or an exception
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    Log.e(BluetoothFragment.TAG,
                    		"BluetoothService$AcceptThread accept() failed: " + e);
                    break;
                }

                // If a connection was accepted
                if (socket != null) {
                    synchronized (BluetoothService.this) {
                    	switch (BluetoothService.this.state) {
                        case listening:
                        case connecting:
                            // Situation normal. Start the connected thread.
                            connected(socket, socket.getRemoteDevice(), true);
                            break;
                        case none:
                        case connected:
                            // Either not ready or already connected. Terminate new socket.
                            try {
                                socket.close();
                            } catch (IOException e) {
                                Log.e(BluetoothFragment.TAG,
                                		"BluetoothService$AcceptThread Could not close unwanted socket: " + e);
                            }
                            break;
                        }
                    }
                }
            }
            if (BuildConfig.DEBUG) {
            	Log.d(BluetoothFragment.TAG, "BluetoothService$AcceptThread.run() ending");
            }
        }

        public void cancel() {
            if (BuildConfig.DEBUG) {
            	Log.d(BluetoothFragment.TAG, "BluetoothService$AcceptThread.cancel()");
            }
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(BluetoothFragment.TAG,
                		"BluetoothService$AcceptThread.cancel(): close of server failed: " + e);
            }
        }
    }


    /**
     * This thread runs while attempting to make an outgoing connection
     * with a device. It runs straight through; the connection either
     * succeeds or fails.
     */
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            mmDevice = device;
            BluetoothSocket tmp = null;

            // Get a BluetoothSocket for a connection with the
            // given BluetoothDevice
            try {
                tmp = device.createInsecureRfcommSocketToServiceRecord(BLUETOOTH_UUID);
            } catch (IOException e) {
                Log.e(BluetoothFragment.TAG,
                		"BluetoothService$ConnectThread: createInsecureRfcommSocket() failed: " + e);
            }
            mmSocket = tmp;
        }

        public void run() {
        	if (BuildConfig.DEBUG) {
        		Log.d(BluetoothFragment.TAG, "BluetoothService$ConnectThread.run()");
        	}
            setName("ConnectThread");

            // Always cancel discovery because it will slow down a connection
            mAdapter.cancelDiscovery();

            // Make a connection to the BluetoothSocket
            try {
                // This is a blocking call and will only return on a
                // successful connection or an exception
                mmSocket.connect();
            } catch (IOException e) {
                connectionFailed();
                // Close the socket
                try {
                    mmSocket.close();
                } catch (IOException e2) {
                    Log.e(BluetoothFragment.TAG,
                    	"BluetoothService$ConnectThread.run(): unable to close() socket during connection failure: " + e2);
                }
                return;
            }

            // Reset the ConnectThread because we're done
            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }

            // Start the connected thread
            connected(mmSocket, mmDevice, false);
        }

        public void cancel() {
            if (BuildConfig.DEBUG) {
            	Log.d(BluetoothFragment.TAG, "BluetoothService$ConnectThread.cancel()");
            }
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(BluetoothFragment.TAG,
                		"BluetoothService$ConnectThread.cancel(): close of connect socket failed: " + e);
            }
        }
    }

    /**
     * This thread runs during a connection with a remote device.
     * It handles all incoming and outgoing transmissions.
     */
    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            if (BuildConfig.DEBUG) {
            	Log.d(BluetoothFragment.TAG, "BluetoothService$ConnectedThread()");
            }
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the BluetoothSocket input and output streams
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e(BluetoothFragment.TAG,
                		"BluetoothService$ConnectedThread(): temp sockets not created: " + e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            if (BuildConfig.DEBUG) {
            	Log.d(BluetoothFragment.TAG, "BluetoothService$ConnectedThread.run()");
            }
            byte[] buffer = new byte[1024];
            int messLen;

            // Keep listening to the InputStream while connected
            while (true) {
                try {
                    // Read from the InputStream
                    messLen = mmInStream.read(buffer);
                    byte[] messageBytes = new byte[messLen];
                    for (int i = 0; i < messLen; i++) {
                    	messageBytes[i] = buffer[i];
                    }
                    if (BuildConfig.DEBUG) {
                    	Log.d(BluetoothFragment.TAG,
                    			"BluetoothService$ConnectedThread: bt message read: " +
                    					new String(buffer, 0, messLen));
                    }
                    // Send the obtained bytes to the UI Activity
                    mHandler.obtainMessage(MESSAGE_READ, -1, -1, messageBytes).sendToTarget();
                } catch (IOException e) {
                	String message = e.getMessage();
                	String logMessage = "BluetoothService$ConnectedThread.run(); stopping=" +
        				stopping + "; " + message;
                	if (message.equals("bt socket closed, read return: -1") ||
                			message.equals("Connection reset by peer")) {
                    	Log.w(BluetoothFragment.TAG, logMessage);
                	} else {
                        Log.e(BluetoothFragment.TAG, logMessage);
                	}
                    // Send a failure message back to the Activity
                    mHandler.obtainMessage(MESSAGE_DEVICE_CONNECTION_LOST).sendToTarget();
                    if (!BluetoothService.this.stopping) {
                    	startAcceptThread();
                    }
                    break;
                }
            }
        }

        /**
         * Write to the connected OutStream.
         * @param buffer  The bytes to write
         */
        public void write(byte[] buffer) {
            if (BuildConfig.DEBUG) {
            	Log.d(BluetoothFragment.TAG,
            			"BluetoothService$ConnectedThread.write(" + new String(buffer));
            }

            try {
                mmOutStream.write(buffer);
                mmOutStream.flush();
            } catch (IOException e) {
                Log.e(BluetoothFragment.TAG,
                		"BluetoothService$ConnectedThread, exception during write: " + e);
            }
        }

        public void cancel() {
        	String methodName = "BluetoothService$ConnectedThread.cancel()";
            if (BuildConfig.DEBUG) {
            	Log.d(BluetoothFragment.TAG, methodName);
            }
            try {
                mmInStream.close();
            } catch (IOException e) {
                Log.e(BluetoothFragment.TAG, methodName +
                		"; close of socket inputStream failed: " + e);
            }
            try {
                mmOutStream.close();
            } catch (IOException e) {
                Log.e(BluetoothFragment.TAG, methodName +
                		"; close of socket outputStream failed: " + e);
            }
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(BluetoothFragment.TAG, methodName +
                		"; close of connect socket failed: " + e);
                Log.e(BluetoothFragment.TAG, "close() of connect socket failed: " + e);
            }
        }
    }
}
