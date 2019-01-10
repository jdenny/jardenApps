package jarden.nio.socket;

import java.awt.EventQueue;
import java.io.IOException;

/**
 * Trying to imitate android.os.Handler, to pass messages across threads,
 * in this case from the network threads to the UI thread. 
 * @author john.denny@gmail.com
 *
 */
public class MySocketChannelHandler implements MySocketChannelListener {
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_DEVICE_CONNECTION_LOST = 7;
    public static final int MESSAGE_CONNECTED = 8;
	public static final int MESSAGE_SEND_ERROR = 9;
	public static final int MESSAGE_CONNECT_ERROR = 10;
	
	private MySocketChannelListener socketChannelListener;
    
    public MySocketChannelHandler(MySocketChannelListener socketChannelListener) {
    	this.socketChannelListener = socketChannelListener;
    }

	@Override
	public void onMessageReceived(String message) {
		EventQueue.invokeLater(new WorkerThread(MESSAGE_READ, message));
	}
	
	private class WorkerThread implements Runnable {
		private String message;
		private int action;
		private IOException exception;
		
		public WorkerThread(int action) {
			this.action = action;
		}
		public WorkerThread(int action, String message) {
			this.action = action;
			this.message = message;
		}
		public WorkerThread(int action, IOException exception) {
			this.action = action;
			this.exception = exception;
		}

		@Override
		public void run() {
			if (action == MESSAGE_READ) {
				socketChannelListener.onMessageReceived(message);
			} else if (action == MESSAGE_CONNECTED) {
				socketChannelListener.onConnected();
			} else if (action == MESSAGE_DEVICE_CONNECTION_LOST) {
				socketChannelListener.onDisconnected();
			} else if (action == MESSAGE_SEND_ERROR) {
				socketChannelListener.onSendError(exception);
			}
		}
		
	}

	@Override
	public void setSocketChannel(MySocketChannel mySocketChannel) {
		//? EventQueue.invokeLater(new WorkerThread(MESSAGE_READ, message));
	}

	@Override
	public void onDisconnected() {
		EventQueue.invokeLater(new WorkerThread(MESSAGE_DEVICE_CONNECTION_LOST));
	}

	@Override
	public void onConnected() {
		EventQueue.invokeLater(new WorkerThread(MESSAGE_CONNECTED));
	}

	@Override
	public void onSendError(IOException ioe) {
		EventQueue.invokeLater(new WorkerThread(MESSAGE_SEND_ERROR, ioe));
	}

	@Override
	public void onConnectError(IOException ioe) {
		EventQueue.invokeLater(new WorkerThread(MESSAGE_CONNECT_ERROR, ioe));
	}

}
