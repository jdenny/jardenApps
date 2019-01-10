package demo.net;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleSocketClient {
	public static void main(String[] args) throws Exception {
		String[] messages = {
				"Hello from Juan",
				"Goodbye from Juan"
		};
		int port = 1234;
		String host = "localhost";
		if (args.length > 0) host = args[0];
		Socket socket = new Socket(host, port);
		InputStream is = socket.getInputStream();
		OutputStream os = socket.getOutputStream();
		BufferedReader reader = new BufferedReader(
			new InputStreamReader(is));
		PrintWriter writer = new PrintWriter(os);
		for (String message: messages) {
			writer.println(message);
		}
		writer.flush();
		String reply;
		while ((reply = reader.readLine()) != null) {
			System.out.println("reply=" + reply);
		}
		System.out.println("null line; Enter to continue");
		System.in.read();
		// reader.close();
		socket.close();
		System.out.println("press Enter to Exit");
		System.in.read();
		System.exit(0);
	}
}