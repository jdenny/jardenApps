package jarden.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;

public class ChatNet implements Runnable, ChatNetIF {
	private final static String MULTICAST_ADDRESS = "230.0.0.1";
	private final static int MULTICAST_PORT = 9002;

	private ChatListener chatListener;
	private String userName;
	private int unicastPort = 8002;
	private DatagramSocket unicastSocket = null;
	private MulticastSocket multicastSocket = null;
	private InetAddress group = null;

	private Thread unicastListenThread = null;
	private Thread multicastListenThread = null;
	private String myHostAddress = "239.255.0.1";
	private User selfUser;

	public ChatNet(ChatListener chatSwing, String userName, int unicastPort) throws IOException {
		this.chatListener = chatSwing;
		this.userName = userName;
		this.unicastPort = unicastPort;
		startNetwork();
	}
	private void startNetwork() throws IOException {
		InetAddress addr = InetAddress.getLocalHost();
		myHostAddress = addr.getHostAddress();
		selfUser = new User(this.userName, this.myHostAddress, this.unicastPort);
		group = InetAddress.getByName(MULTICAST_ADDRESS);
		multicastSocket = new MulticastSocket(MULTICAST_PORT);
		//? multicastSocket.setLoopbackMode(true);
		
		// listen for messages sent on this multicast socket:
		multicastListenThread = new Thread(new Runnable() {
			@Override
			public void run() {
				// this thread is started within the unicast listen thread, so the latter must
				// be running before we sent our multicast connect message
				System.out.println("multicast listen thread running");
				try {
					multicastSocket.joinGroup(group);
					String message = "connect::name=" + userName + "::host=" +
							myHostAddress + "::port=" + unicastPort;
					DatagramPacket packet = new DatagramPacket(
							message.getBytes(), message.length(), group, MULTICAST_PORT);
					multicastSocket.send(packet);
				} catch (IOException e) {
					e.printStackTrace();
				}
				byte[] inBuffer = new byte[1000];
				DatagramPacket recv = new DatagramPacket(inBuffer, inBuffer.length);
				while (true) {
					try {
						multicastSocket.receive(recv);
						processPacket(recv, true);
					} catch (IOException e) {
						if (e instanceof SocketException &&
								e.getMessage().equals("socket closed")) {
							System.out.println("inner thread terminating as socket now closed");
							break;
						}
						e.printStackTrace();
						break;
					}
				}
			}
		});
		// listen for messages sent on unicast socket
		unicastListenThread = new Thread(this);
		unicastListenThread.setPriority(unicastListenThread.getPriority() + 1);
		unicastListenThread.start();
		System.out.println("unicastListenThread started at priority " +
				unicastListenThread.getPriority());
	}
	/**
	 * Process a packet received on multicast or unicast ports.
	 * Both ports may be used to send connect messages or ordinary messages.
	 * Multicast:
	 * 		if from self, ignore.
	 * 		if connect, add or update user, and send a connect reply
	 * 		otherwise display message
	 * Unicast:
	 * 		(shouldn't get from self)
	 * 		if connect, add user
	 * 		otherwise display message
	 * 
	 * Notes: we could loopback mode inhibit to true, to stop a socket receiving
	 * it's own messages, but that would stop two clients
	 * on the same IP address receiving messages from each other
	 * @param packet
	 * @param multicast
	 * @throws IOException
	 */
	private void processPacket(DatagramPacket packet, boolean multicast) throws IOException {
		String message = new String(packet.getData(), 0, packet.getLength());
		String type = multicast?"multicast":"unicast";
		System.out.println(type + " packet received from " +
				packet.getAddress().getHostAddress() + ":" + packet.getPort() +
				"='" + message + "'");
		// e.g. connect::name=John::host=192.168.2.1::port=8002
		String[] tokens = message.split("::");
		if (tokens.length == 4 && tokens[0].equals("connect") &&
				tokens[1].startsWith("name=") &&
				tokens[2].startsWith("host=") &&
				tokens[3].startsWith("port=")) {
			String name = tokens[1].substring(5);
			String host = tokens[2].substring(5);
			int port = Integer.parseInt(tokens[3].substring(5));
			User user = new User(name, host, port);
			// check to see if trying to add itself:
			if (user.equals(this.selfUser)) {
				assert (multicast): "shouldn't receive unicast from self";
				System.out.println("ignoring message from self");
				return;
			}
			chatListener.addUser(user);
			// if multicast then must be new user or reconnecting user
			// if unicast, then user must already know about me
			if (multicast) {
				// new user, so send back a connect message
				System.out.println("sending back connect message to user " + name);
				String message2 = "connect::name=" + userName + "::host=" +
						myHostAddress + "::port=" + unicastPort;
				SendDatagram.sendDatagram(host, port, message2);
			}
		} else {
			chatListener.showMessage(message);
		}
	}
	@Override
	public void run() {
		System.out.println("unicast listen thread running");
		byte[] data = new byte[100];
		DatagramPacket packet = new DatagramPacket(data, data.length);
		try {
			multicastListenThread.start();
			System.out.println("multicastListenThread started at priority " +
					multicastListenThread.getPriority());
			unicastSocket = new DatagramSocket(unicastPort);
				while (true) {
					unicastSocket.receive(packet);
					processPacket(packet, false);
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public void sendMessageToAll(String message) throws IOException {
		message = "from " + userName + " to All: " + message;
		DatagramPacket packet = new DatagramPacket(
				message.getBytes(), message.length(), group, MULTICAST_PORT);
		multicastSocket.send(packet);
	}
	public void sendMessage(User user, String message) throws IOException {
		message = "from " + userName + " to " + user.getName() +
				": " + message;
		SendDatagram.sendDatagram(user.getHost(), user.getPort(), message);
	}
}
