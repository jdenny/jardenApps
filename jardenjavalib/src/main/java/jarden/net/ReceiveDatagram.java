package jarden.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

class ReceiveDatagram {
	public static void main(String[] args) {
		byte[] data = new byte[100];
		DatagramSocket socket = null;
		try {
			DatagramPacket packet = new DatagramPacket(data, data.length);
			socket = new DatagramSocket(SendDatagram.port);
			System.out.println("waiting to receive packets on port " +
				SendDatagram.port);
			while (true) {
				socket.receive(packet);
				System.out.println("length=" + packet.getLength());
				String message = new String(packet.getData(), 0, packet.getLength());
				System.out.println("packet received from " + packet.getAddress() +
					": " + message);
			}
		} catch(IOException ioe) {
			ioe.printStackTrace();
		} finally {
			if (socket != null) socket.close();
		}
		
	}
}