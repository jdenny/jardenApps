package jarden.net;


public interface ChatListener {

	void addUser(User user);

	void showMessage(String message);

	void setChat(ChatNetIF chat);

}
