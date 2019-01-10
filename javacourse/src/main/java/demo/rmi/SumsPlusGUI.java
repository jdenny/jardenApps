package demo.rmi;

import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 * GUI Client of SumsPlus. One of the methods, addSumsListener, requires
 * a SumsListener, which is a Remote interface. This class (SumsPlusGUI)
 * is an implementation of that interface.
 * 
 * To run: see SumsPlusServer
 * @author john.denny@gmail.com
 */
public class SumsPlusGUI extends UnicastRemoteObject
		implements SumsListener, ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String[] OPERATORS = {
		"+", "-"
	};
	private SumsPlus sumsPlus;
	private JTextField fieldA;
	private JTextField fieldB;
	private JTextField fieldTimeout;
	private JTextField fieldStatus;
	private JComboBox<String> operatorChoice;

	public static void main(String[] args) throws RemoteException {
		String host = "localhost";
		if (args.length > 0) host = args[0];
		new SumsPlusGUI(host);
	}
	public SumsPlusGUI() throws RemoteException {
	}
	public SumsPlusGUI(String host) throws RemoteException {
		JFrame frame = new JFrame("SumsPlusGUI");
		Container container = frame.getContentPane();
		container.setLayout(new FlowLayout());
		fieldA = new JTextField(10);
		fieldB = new JTextField(10);
		fieldTimeout = new JTextField(10);
		fieldStatus = new JTextField(30);
		operatorChoice = new JComboBox<String>(OPERATORS);
		container.add(fieldA);
		container.add(operatorChoice);
		container.add(fieldB);
		container.add(new JLabel("timeout"));
		container.add(fieldTimeout);
		container.add(fieldStatus);
		fieldA.addActionListener(this);
		fieldB.addActionListener(this);
		fieldTimeout.addActionListener(this);
		try {
			sumsPlus = (SumsPlus)Naming.lookup("rmi://" + host + "/Sums");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		frame.setSize(300, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public void actionPerformed(ActionEvent event) {
	    try {
		if (event.getSource() == fieldTimeout) {
			int timeout = Integer.parseInt(fieldTimeout.getText());
			sumsPlus.addSumsListener(this, timeout);
			fieldStatus.setText("asked sums to notify after " +
				timeout + " seconds");
			return;
		}
		String operator = (String) operatorChoice.getSelectedItem();
		int a = Integer.parseInt(fieldA.getText());
		int b = Integer.parseInt(fieldB.getText());
		if (operator.equals("+")) {
			fieldStatus.setText("result is: " + sumsPlus.add(a, b));
		}
		else if (operator.equals("-")) {
			fieldStatus.setText("result is: " + sumsPlus.subtract(a, b));
		}
		else {
			fieldStatus.setText("unknown operator: " + operator);
		}
	    }
	    catch(Exception e) {
	    	e.printStackTrace();
	    }
	}
	public void finished(String message) {
		fieldStatus.setText("finished: " + message);
	}
}
