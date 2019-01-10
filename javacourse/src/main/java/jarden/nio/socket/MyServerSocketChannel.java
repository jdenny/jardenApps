package jarden.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.logging.Logger;

/**
 * MyServerSocketChannel
 * 		serverThread: wait for remote connections
 * 			for each connection: create MySocketChannel
 * 			serverThread will continue to run until we hit the red terminate button
 * MySocketChannel
 * 		mainThread: wait for user input to send message or stop netThread
 * 		netThread: wait to receive socket messages and display them
 * MySocketChannelListener
 * 		interact with user; send user's messages across network via MySocketChannel;
 * 			process messages received on network via MySocketChannel
 * 2 implementations of this interface provided: MySocketChannelConsole
 * 		& MySocketChannelSwing
 * See diagram in docs/exercises/classesObjects.doc
 * @author john.denny@gmail.com
 *
 */
public class MyServerSocketChannel implements Runnable {
	public static final int PORT = 1234;
	private Logger logger;
	private String className = getClass().getName();

	public static void main(String[] args) throws IOException {
		new MyServerSocketChannel();
	}
	public MyServerSocketChannel() {
		new Thread(this).start();
	}
	@Override
	public void run() {
		this.logger = Logger.getLogger(className);
		logger.info("about to listen for connections to port " + PORT);
		try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
			SocketAddress socketAddress = new InetSocketAddress(PORT);
			serverSocket.socket().bind(socketAddress);
			while (true) {
				SocketChannel socketChannel = serverSocket.accept();
				MySocketChannel mySocketChannel = new MySocketChannel(socketChannel);
				MySocketChannelListener listener = new MySocketChannelSwing(true); 
				mySocketChannel.setSocketChannelListener(listener);
				listener.setSocketChannel(mySocketChannel);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
