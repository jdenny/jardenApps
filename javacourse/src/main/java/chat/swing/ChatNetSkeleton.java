package chat.swing;

import java.io.IOException;

public class ChatNetSkeleton implements ChatNetIF {

	private ChatSwingIF chatSwing;

	public ChatNetSkeleton(ChatSwingIF chatSwing, String userName, int unicastPort) {
		this.chatSwing = chatSwing;
	}

	@Override
	public void sendMessageToAll(String message) throws IOException {
		System.out.println("SkeletonChat.sendMessageToAll(" + message + ")");
		chatSwing.showMessage("message sent to all: " + message);
		// now pretend we've received a message back:
		onMessageReceived("received: " + message);
	}

	@Override
	public void sendMessage(User user, String message) throws IOException {
		System.out.println("SkeletonChat.sendMessage(" + user.getName() + ", " + message + ")");
	}
	private void onMessageReceived(String message) {
		chatSwing.showMessage(message);
	}
	
}

