package jarden.net;

import java.io.IOException;

public interface ChatNetIF {
	static int DEFAULT_UNICAST_PORT = 8002;

	void sendMessageToAll(String message) throws IOException;

	void sendMessage(User user, String message) throws IOException;

}
