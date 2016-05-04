package jarden.bluetooth;


import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;

public class BluetoothServer implements Runnable {
	public final static String CHARSET = "UTF-8";
	public final static String AMAZE_QUIZ_SERVICE_NAME =
			"AmazeQuizService";
	public final static String AMAZE_QUIZ_UUID_STR =
			"398e3790-7d9e-4ced-87cf-e06321010ae6";
	public static UUID amazeQuizUUID;
	private BluetoothServerSocket mmServerSocket;
	private BluetoothAdapter mBluetoothAdapter;
	private BluetoothActivity bluetoothActivity;

	public BluetoothServer(BluetoothAdapter mBluetoothAdapter,
			BluetoothActivity bluetoothActivity) throws IOException {
		this.mBluetoothAdapter = mBluetoothAdapter;
		this.bluetoothActivity = bluetoothActivity;
		amazeQuizUUID = UUID.fromString(AMAZE_QUIZ_UUID_STR);
		mmServerSocket = this.mBluetoothAdapter.listenUsingRfcommWithServiceRecord(
				AMAZE_QUIZ_SERVICE_NAME,
				amazeQuizUUID);
	}

	public void run() {
		BluetoothSocket socket = null;
		bluetoothActivity.logMessageOnUI("bluetooth server ready for clients!");
		try {
			socket = mmServerSocket.accept();
			bluetoothActivity.logMessageOnUI("client now connected");
			mmServerSocket.close();
			InputStream is = socket.getInputStream();
			byte[] buffer = new byte[1024];
			int dataLength;
			while ((dataLength = is.read(buffer, 0, buffer.length)) > -1) {
				String line = new String(buffer, 0, dataLength, CHARSET);
				bluetoothActivity.logMessageOnUI(line);
			}
			socket.close();
			stop();

		} catch (IOException e) {
			bluetoothActivity.logMessage("exception accepting connection: " + e);
		}
	}

	/** Will cancel the listening socket, and cause the thread to finish */
	public void stop() {
		try {
			mmServerSocket.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}