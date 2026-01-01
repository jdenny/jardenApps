package jarden.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.Scanner;

public class MulticastSendReceive {
	private final static String host = "228.5.6.7";
	private final static int port = 6789;
	private MulticastSocket socket;

	public static void main(String[] args) throws IOException {
		new MulticastSendReceive();
	}
	public MulticastSendReceive() throws IOException {
		Scanner scanner = new Scanner(System.in);
		String message;
		DatagramPacket packet;
		// join a Multicast group
		InetAddress group = InetAddress.getByName(host);
		socket = new MulticastSocket(port);
		socket.joinGroup(group);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// get their responses!
				byte[] inBuffer = new byte[1000];
				DatagramPacket recv = new DatagramPacket(inBuffer, inBuffer.length);
				while (true) {
					try {
						socket.receive(recv);
						System.out.println("length=" + recv.getLength());
						String message = new String(recv.getData(), 0, recv.getLength());
						System.out.println("packet received from " + recv.getAddress() +
							": " + message);
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
		thread.start();
		System.out.println("supply messages to multicast to " + host + ":" + port + " or empty line to quit)");
		while (true) {
			System.out.println("message (empty to quit):");
			message = scanner.nextLine();
			if (message.length() == 0) {
				break;
			}
			packet = new DatagramPacket(message.getBytes(), message.length(), group, port);
			socket.send(packet);

		}
		scanner.close();
		socket.leaveGroup(group);
		socket.close();
		System.out.println("multicast socket closed");
	}


}
