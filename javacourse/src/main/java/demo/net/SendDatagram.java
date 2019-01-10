package demo.net;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SendDatagram implements ActionListener {
	public static final int port = 5001;
	private String host = "localhost";
	private byte[] data = new byte[200];
	private DatagramPacket packet;
	private DatagramSocket socket;
	private JTextArea textArea;
	private JTextField hostField;

	public SendDatagram() throws IOException {
		InetAddress inetAddress = InetAddress.getByName(host);
		packet = new DatagramPacket(data, data.length,
			inetAddress, port);
		socket = new DatagramSocket();
		// create GUI components:
		JFrame frame = new JFrame("Send Datagram");
		textArea = new JTextArea();
		hostField = new JTextField(host, 12);
		JButton sendButton = new JButton("Send");
		sendButton.addActionListener(this);
		// set layout:
		Container container = frame.getContentPane();
		container.add(textArea, "Center");
		JPanel buttonPanel = new JPanel();
		buttonPanel.add(hostField);
		buttonPanel.add(sendButton);
		container.add(buttonPanel, "South");
		frame.setSize(400, 200);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public static void sendDatagram(String host, int port, String message)
			throws IOException {
		InetAddress inetAddress = InetAddress.getByName(host);
		byte[] data = message.getBytes();
		DatagramPacket packet = new DatagramPacket(data, data.length,
			inetAddress, port);
		DatagramSocket socket = new DatagramSocket();
		socket.send(packet);
		socket.close();
	}

	public void actionPerformed(ActionEvent event) {
		try {
			String message = textArea.getText();
			String host = hostField.getText();
			if (!host.equals(this.host)) {
				this.host = host;
				InetAddress inetAddress = InetAddress.getByName(host);
				packet.setAddress(inetAddress);
			}
			packet.setData(message.getBytes());
			packet.setLength(message.length());
			socket.send(packet);
			System.out.println("packet sent; length=" + packet.getLength());
		}
		catch(IOException ioe) {
			ioe.printStackTrace();
		}
	}
	public static void main(String[] args) throws IOException {
		new SendDatagram();
	}
}