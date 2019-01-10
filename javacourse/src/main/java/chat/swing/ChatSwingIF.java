package chat.swing;


public interface ChatSwingIF {

	void addUser(User user);

	void showMessage(String message);

	void setChat(ChatNetIF chat);

}
