package jarden.nio.socket;

import java.io.IOException;

public interface MySocketChannelListener {
	void onMessageReceived(String message);
	void setSocketChannel(MySocketChannel mySocketChannel);
	void onDisconnected();
	void onConnected();
	void onSendError(IOException ioe);
	void onConnectError(IOException ioe);
}
