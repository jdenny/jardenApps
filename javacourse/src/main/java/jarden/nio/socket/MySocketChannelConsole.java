package jarden.nio.socket;

import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Logger;

public class MySocketChannelConsole implements MySocketChannelListener {
	private MySocketChannel mySocketChannel;
	private Logger logger;
	private String className = getClass().getName();

	public static void main(String[] args) throws IOException {
		MySocketChannelConsole mscc = new MySocketChannelConsole();
		MySocketChannel mySocketChannel = new MySocketChannel(mscc);
		//! mySocketChannel.setSocketChannelListener(mscc);
		mscc.setSocketChannel(mySocketChannel);
	}
	public MySocketChannelConsole() {
	}
	public void setSocketChannel(MySocketChannel socketChannel) {
		this.mySocketChannel = socketChannel;
		this.logger = Logger.getLogger(className);
		// now start networking!
		try (Scanner scanner = new Scanner(System.in)) {
			while (true) {
				System.out.println("message to send to receiver, <s> swing or <q> to quit:");
				String line = scanner.nextLine();
				if (line.equals("<q>")) {
					this.mySocketChannel.close();
					break;
				} else if (line.equals("<s>")) {
					MySocketChannelSwing mscs = new MySocketChannelSwing(false);
					mscs.setSocketChannel(mySocketChannel);
					this.mySocketChannel.setSocketChannelListener(mscs);
					break;
				} else {
					this.mySocketChannel.sendMessage(line);
				}
			}
		}
	}
	@Override
	public void onMessageReceived(String message) {
		this.logger.entering(className, "onMessageReceived", message);
	}
	@Override
	public void onDisconnected() {
		this.logger.entering(className, "onDisconnected");
	}
	@Override
	public void onConnected() {
		this.logger.entering(className, "onConnected");
	}
	@Override
	public void onSendError(IOException ioe) {
		this.logger.entering(className, "onSendError", ioe);
	}
	@Override
	public void onConnectError(IOException ioe) {
		this.logger.entering(className, "onConnectError", ioe);
	}

}
