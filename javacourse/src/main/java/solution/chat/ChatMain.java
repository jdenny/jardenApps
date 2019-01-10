package solution.chat;

import java.io.IOException;
import java.util.Scanner;

import chat.swing.ChatNetIF;
import chat.swing.ChatSwing;
import chat.swing.ChatSwingIF;
import chat.swing.User;

/*
 * Your mission, should you choose to accept it, is to write a
 * full implementation of ChatIF, to replace SkeletonChat below.
 * See the javadoc for ChatSwing for some suggestions. Also see
 * classes in demo.net for examples of sending and receiving
 * datagrams, using point-to-point or multicast.
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
		ChatNetIF chat = // new SkeletonChat(chatSwing, userName, unicastPort);
				new ChatNet(chatSwing, userName, unicastPort);
		chatSwing.setChat(chat);
	}
}

class SkeletonChat implements ChatNetIF {

	public SkeletonChat(ChatSwingIF chatSwing, String userName, int unicastPort) {
	}

	@Override
	public void sendMessageToAll(String message) throws IOException {
		System.out.println("SkeletonChat.sendMessageToAll(" + message + ")");
	}

	@Override
	public void sendMessage(User user, String message) throws IOException {
		System.out.println("SkeletonChat.sendMessage(" + user.getName() + ", " + message + ")");
	}
	
}
