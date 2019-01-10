package jarden.nio.socket;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

/**
 * Communicate via TCP Socket, using a SocketChannel. Can be run as
 * the server, by passing an existing SocketChannel to the constructor,
 * or as a client, using the default constructor, which obtains a
 * socketChannel by connecting to a serverSocketChannel.
 * Note: "Socket channels are safe for use by multiple concurrent threads"
 * from the java API for SocketChannel.
 * 
 * @author john.denny@gmail.com
 */
public class MySocketChannel implements Runnable {
	private MySocketChannelHandler socketChannelHandler;
	private ExecutorService executorService = Executors.newCachedThreadPool();
	private SocketChannel socketChannel;
	private ByteBuffer buffer;
	private boolean echo = false;
	private boolean stop = false;
	private String name;
	private Logger logger;
	private String className = getClass().getName();
	private String host;
	private int port;

	/*
	 * Called from MySocketChannelSwing: client connects to server.
	 */
	public MySocketChannel(MySocketChannelListener socketChannelListener) {
		this(socketChannelListener, "localhost", MyServerSocketChannel.PORT);
	}
	public MySocketChannel(MySocketChannelListener socketChannelListener, String ahost, int aport) {
		this.host = ahost;
		this.port = aport;
		setSocketChannelListener(socketChannelListener);
		this.executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					InetSocketAddress inetSockAdd =
							new InetSocketAddress(host, port); 
					SocketChannel socketChannel = SocketChannel.open(inetSockAdd);
					setSocketChannel(socketChannel, false);
					socketChannelHandler.onConnected();
				} catch (IOException ioe) {
					socketChannelHandler.onConnectError(ioe);
				}
			}
		});
	}
	/*
	 * Called from MyServerSocketChannel, when a client has connected.
	 */
	public MySocketChannel(SocketChannel socketChannel) {
		setSocketChannel(socketChannel, true);
	}
	private void setSocketChannel(SocketChannel socketChannel, boolean server) {
		this.socketChannel = socketChannel;
		this.name = server?"server":"client";
		this.logger = Logger.getLogger(className);
		this.buffer = ByteBuffer.allocate(2048);
		// start thread to connect to server and await incoming messages:
		this.executorService.execute(this);
	}
	public void setSocketChannelListener(MySocketChannelListener socketChannelListener) {
		this.socketChannelHandler = new MySocketChannelHandler(socketChannelListener);
	}
	public void setEcho(boolean echo) {
		this.echo = echo;
	}
	public void sendMessage(String message) {
		this.executorService.execute(new SendMessageRunnable(message));
	}
	private class SendMessageRunnable implements Runnable {
		private String message;

		public SendMessageRunnable(String message) {
			this.message = message;
		}
		@Override
		public void run() {
			message = name + ": " + message; 
			buffer.put(message.getBytes());
			buffer.flip();
			try {
				socketChannel.write(buffer);
			} catch (IOException ioe) {
				logger.severe("exception writing to socket: " + ioe);
				socketChannelHandler.onSendError(ioe);
			}
			buffer.clear();
		}
	}
	public void close() {
		this.stop = true;
		try {
			this.socketChannel.close();
		} catch (IOException ioe) {
			logger.severe("exception closing socketChannel: " + ioe);
		}
		this.executorService.shutdown();
	}

	@Override
	public void run() {
		try {
			logger.info("socket created for host "
					+ socketChannel.socket().getLocalAddress());
			int bytesRead;
			while (!stop && (bytesRead = socketChannel.read(buffer)) > 0) {
				logger.info("bytesRead=" + bytesRead);
				if (bytesRead > 0) {
					byte[] bytes = new byte[bytesRead];
					buffer.flip();
					buffer.get(bytes);
					buffer.clear();
					String inMessage = new String(bytes);
					logger.info("MySocketChannel.run() received: " +
							inMessage);
					if (this.socketChannelHandler != null) {
						this.socketChannelHandler.onMessageReceived(inMessage);
					}
					if (this.echo) {
						sendMessage("echo: " + inMessage);
					}
				}
			}
		} catch (ClosedByInterruptException e) {
			logger.info("ClosedByInterruptException; closing down");
		} catch (IOException e) {
			logger.warning("exception: " + e);
		} finally {
			try {
				socketChannel.close();
				logger.info("socketChannel now closed");
				this.socketChannelHandler.onDisconnected();
			} catch (IOException e) {
				logger.warning("exception: " + e);
			}
		}
	}
}
