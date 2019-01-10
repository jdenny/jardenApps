package jarden.nio.socket;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Swing interface to MySocketChannel, i.e. simple chat program using sockets.
 */
public class MySocketChannelSwing  implements ActionListener, MySocketChannelListener {
	private JTextField hostTextField;
	private JTextField portTextField;
	private JTextField messageTextField;
	private JButton goButton;
	private JButton connectButton;
	private JTextArea messageArea;
	private JTextField statusTextField;
	private MySocketChannel mySocketChannel;
	private JFrame frame;
	private boolean server;
	
	public static void main(String[] args) throws IOException {
		new MySocketChannelSwing(false);
	}
	public MySocketChannelSwing(boolean server) {
		this.server = server;
		this.frame = new JFrame("MySocketChannelSwing: " +
				(this.server?"server":"client"));
		this.hostTextField = new JTextField("localhost", 14);
		this.portTextField = new JTextField(Integer.toString(
				MyServerSocketChannel.PORT), 6);
		this.messageTextField = new JTextField(20);
		this.statusTextField = new JTextField(20);
		this.messageArea = new JTextArea(10, 20);
		this.goButton = new JButton("Go");
		this.goButton.setEnabled(false);
		this.connectButton = new JButton("Connect");
		// set layout of components:
		Container container = frame.getContentPane();
		JPanel controlPanel = new JPanel(new GridLayout(2, 1));
		JPanel connectPanel = new JPanel();
		connectPanel.add(new JLabel("Host"));
		connectPanel.add(hostTextField);
		connectPanel.add(new JLabel("Port"));
		connectPanel.add(portTextField);
		connectPanel.add(connectButton);
		JPanel sendPanel = new JPanel();
		sendPanel.add(new JLabel("Message to send:"));
		sendPanel.add(messageTextField);
		sendPanel.add(goButton);
		if (server) {
			container.add(sendPanel, BorderLayout.NORTH);
		} else {
			container.add(controlPanel, BorderLayout.NORTH);
			controlPanel.add(connectPanel);
			controlPanel.add(sendPanel);
		}
		container.add(messageArea, BorderLayout.CENTER);
		container.add(statusTextField, BorderLayout.SOUTH);
		// handle component events:
		messageTextField.addActionListener(this);
		this.goButton.addActionListener(this);
		this.connectButton.addActionListener(this);
		frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt){
            	if (mySocketChannel != null) {
            		mySocketChannel.close();
            	}
            }
        });
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true); // start event handling thread
	}
	public void setSocketChannel(MySocketChannel mySocketChannel) {
		this.mySocketChannel = mySocketChannel;
		goButton.setEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		this.statusTextField.setText("");
		if (event.getSource() == this.connectButton) {
			String host = this.hostTextField.getText();
			int port = Integer.parseInt(this.portTextField.getText());
			this.mySocketChannel = new MySocketChannel(this, host, port);
		} else { // must be 'sendMessage'
			if (this.mySocketChannel == null) {
				this.statusTextField.setText("not connected!");
			} else {
				mySocketChannel.sendMessage(this.messageTextField.getText());
				this.messageTextField.setText("");
			}
		}
	}
	@Override
	public void onConnected() {
		this.server = false;
		this.connectButton.setEnabled(false);
		this.goButton.setEnabled(true);
	}
	@Override
	public void onDisconnected() {
		mySocketChannel.close();
		if (this.server) {
			JOptionPane.showMessageDialog(frame,
					"Lost connection!");
			this.frame.dispose();
		} else {
			this.goButton.setEnabled(false);
			this.connectButton.setEnabled(true);
			this.mySocketChannel = null;
		}
	}
	@Override
	public void onSendError(IOException ioe) {
		if (this.server) {
			this.statusTextField.setText(
					"Lost connection with client. Closing listener. Exception sending message: " + ioe);
		} else {
			this.statusTextField.setText("exception sending message: " + ioe);
		}
		onDisconnected();
	}
	@Override
	public void onConnectError(IOException ioe) {
		this.statusTextField.setText("exception connecting: " + ioe);
	}
	@Override
	public void onMessageReceived(String message) {
		messageArea.insert(message + "\n", 0);
	}
}
