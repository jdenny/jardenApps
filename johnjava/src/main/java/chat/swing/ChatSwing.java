package chat.swing;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.AbstractTableModel;

import jarden.net.ChatListener;
import jarden.net.ChatNet;
import jarden.net.ChatNetIF;
import jarden.net.User;

/**
 * Swing front-end for the simple network chat system.
 * So this front-end can be plugged into different implementations
 * of chat, the implementation must implement ChatIF.
 */
public class ChatSwing implements ActionListener, ChatListener {
    private JTextArea messageArea;
    private JTable userTable;
    private JTextField messageInputField;
    private JCheckBox toAllCheckbox;
    private UserTableModel userTableModel;
    private ChatNetIF chat;

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
        ChatListener chatListener = new ChatSwing(userName, unicastPort);
        ChatNetIF chat = // new SkeletonChat(chatSwing, userName, unicastPort);
                new ChatNet(chatListener, userName, unicastPort);
        chatListener.setChat(chat);
    }

	public ChatSwing(String userName, int unicastPort) {
		userTableModel = new UserTableModel();
		JFrame frame = new JFrame("ChatGUI user=" + userName + "; port=" + unicastPort);
		JLabel messageLabel = new JLabel("Message:");
		messageArea = new JTextArea(10, 20);
		toAllCheckbox = new JCheckBox("To All");
		userTable = new JTable(userTableModel);
		userTable.setPreferredScrollableViewportSize(new Dimension(500, 100));
		messageInputField = new JTextField(20);
		messageInputField.setActionCommand("SendMessage");
		// layouts:
		Container container = frame.getContentPane();
		JPanel controlPanel = new JPanel();
		JScrollPane scrollPane = new JScrollPane(userTable);
		//? table.setFillsViewportHeight(true);
		container.add(controlPanel, BorderLayout.SOUTH);
		container.add(messageArea, BorderLayout.CENTER);
		container.add(scrollPane, BorderLayout.NORTH);
		controlPanel.add(toAllCheckbox);
		controlPanel.add(messageLabel);
		controlPanel.add(messageInputField);
		// event listeners:
		messageInputField.addActionListener(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		if (action.equals("SendMessage")) {
			String message = messageInputField.getText();
			try {
				if (this.toAllCheckbox.isSelected()) {
					chat.sendMessageToAll(message);
				} else {
					int userIndex = userTable.getSelectedRow();
					if (userIndex < 0) {
						messageArea.append("no user selected\n");
					} else {
						User user = userTableModel.getUser(userIndex);
						chat.sendMessage(user, message);
						messageArea.append(message + "\n");
						messageInputField.setText("");
					}
				}
			} catch (IOException e) {
				messageArea.append("exception: " + e + "\n");
				e.printStackTrace();
			}
		}
	}
	@Override
	public void setChat(ChatNetIF chat) {
		this.chat = chat;
	}
	private class ShowMessage implements Runnable {
		private String message;

		public ShowMessage(String message) {
			this.message = message;
		}
		@Override
		public void run() {
			// Note: event dispatch thread runs at priority 6
			// In our solution (solution.net.ChatNet),
			// the multicast thread runs at 5 (normal priority)
			// and the multicast thread runs at 6.
			System.out.println("thread: " + Thread.currentThread());
			messageArea.append(this.message + "\n");
			messageInputField.setText("");
		}
	}
	private class AddUser implements Runnable {
		private User user;

		public AddUser(User user) {
			this.user = user;
		}
		@Override
		public void run() {
			userTableModel.addUser(this.user);
		}
	}
	@Override
	public void addUser(User user) {
		// run in AWT event thread:
		EventQueue.invokeLater(new AddUser(user));
	}
	@Override
	public void showMessage(String message) {
		// run in AWT event thread:
		EventQueue.invokeLater(new ShowMessage(message));
	}
	private static class UserTableModel extends AbstractTableModel {
		private static final long serialVersionUID = 1L;
		private final static String[] columnNames = {
			"Name", "Host", "Port", "Status"
		};
		private ArrayList<User> userList = new ArrayList<User>();
		/**
		 * If user already in list, return 0, else add to list and return 1.
		 */
		public int addUser(User newUser) {
			// check to see if user already in list of other users:
			for (int i = 0; i < userList.size(); i++) {
				User user = userList.get(i);
				if (user.equals(newUser)) {
					if (user.getName().equals(newUser.getName())) {
						System.out.println("addUser failed as trying to add existing user: " + newUser.getName());
					}
					else {
						user.setName(newUser.getName());
						System.out.println("addUser updated username of existing user");
						this.fireTableRowsInserted(i, i);
					}
					return 0;
				}
			}
			userList.add(newUser);
			int lastRow = userList.size() - 1;
			this.fireTableRowsInserted(lastRow, lastRow);
			// this.fireTableDataChanged();
			return 1;
		}
		public User getUser(int index) {
			return userList.get(index);
		}
		@Override
		public int getColumnCount() {
	        return columnNames.length;
	    }
		@Override
		public int getRowCount() {
	        return userList.size();
	    }
		@Override
		public String getColumnName(int col) {
	        return columnNames[col];
	    }
		@Override
		public Object getValueAt(int row, int col) {
			User user = userList.get(row);
			switch(col) {
			case 0:
				return user.getName();
			case 1:
				return user.getHost();
			case 2:
				return Integer.toString(user.getPort());
			case 3:
				return user.getStatus();
			default:
				throw new IllegalArgumentException("column " + col +
						" invalid for user table");
			}
	    }
		@Override
	    public Class<?> getColumnClass(int c) {
			if (c == 0) return JCheckBox.class;
			else return String.class;
	    }
	}
}

