package chat.swing;

import java.io.IOException;
import java.util.Scanner;

/*
 * Your mission, should you choose to accept it, is to write a
 * full implementation of ChatIF, to replace ChatNetSkeleton.
 */
public class ChatMain {
	public static void main(String[] args) throws IOException {
		Scanner scanner = new Scanner(System.in);
		System.out.println("user name: ");
		String userName = scanner.nextLine();
		/*
		 * If each version of Chat runs on its own PC, they can all
		 * use the same Unicast port. But to make it easier to test,
		 * if 2 versions are running on the same PC they each need
		 * to use a different port, hence the option to change it,
		 * below.
		 */
		System.out.println("unicast port (default " +
				ChatNetIF.DEFAULT_UNICAST_PORT + "): ");
		String portStr = scanner.nextLine();
		scanner.close();
		int  unicastPort = ChatNetIF.DEFAULT_UNICAST_PORT;
		if (portStr.length() > 0) {
			unicastPort = Integer.parseInt(portStr);
		}
		ChatSwingIF chatSwing = new ChatSwing(userName, unicastPort);
		ChatNetIF chat = new ChatNetSkeleton(chatSwing, userName, unicastPort);
		chatSwing.setChat(chat);
	}
}
