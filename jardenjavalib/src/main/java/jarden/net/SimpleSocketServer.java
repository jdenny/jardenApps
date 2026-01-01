package jarden.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class SimpleSocketServer {

	public static void main(String[] args) throws Exception {
		int port = 1234;
		System.out.println("about to listen for connections to port " +
				port);
		try (ServerSocket serverSocket = new ServerSocket(port)) {
			while (true) {
				Socket socket = serverSocket.accept();
				talkToSocket(socket);
			}
		}
	}
	public static void talkToSocket(Socket socket) {
		try {
			System.out.println("socket created for host " +
				socket.getInetAddress().getHostName());
			InputStream is = socket.getInputStream();
			OutputStream os = socket.getOutputStream();
			BufferedReader reader = new BufferedReader(
				new InputStreamReader(is));
			PrintWriter writer = new PrintWriter(os);
			String inMessage;
			while ((inMessage = reader.readLine()) != null) {
				System.out.println("message received from host " +
					socket.getInetAddress().getHostAddress() +
					"; " + inMessage);
				writer.println("received by John: " + inMessage);
				writer.flush();
			}
			System.out.println("null line; Enter to continue");
			System.in.read();
			System.out.println("closing socket for host " +
				socket.getInetAddress().getHostName());
			writer.close();
			// reader.close();
			// socket.close();
		}
		catch(Exception e) {
			System.out.println("exception: " + e);
			e.printStackTrace();
		}
	}
}