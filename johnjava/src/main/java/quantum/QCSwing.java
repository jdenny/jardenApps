package quantum;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Simple java application to create a graphical user interface (GUI).
 * The user types in a string, which is added to a display area
 * in the middle of the window.
 */
public class QCSwing implements ActionListener {
	private JTextField statusTextField;
    private JTextField massTextField;
    private JTextField distanceXTextField;
    private JTextField timeTextField;
    private JTextField clockCtTextField;
    private JTextField deltaXTextField;

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new QCSwing();
            }
        });
	}
	public QCSwing() {
		JFrame frame = new JFrame("quantum.HelloSwing");
		//!! JLabel textLabel = new JLabel("Type And Go:");
		statusTextField = new JTextField(20);
        massTextField = new JTextField(20);
        distanceXTextField = new JTextField(20);
        timeTextField = new JTextField(20);
        clockCtTextField = new JTextField(20);
        deltaXTextField = new JTextField(20);
		JButton goButton = new JButton("Go");
		JButton resetButton = new JButton("Reset");
		Container container = frame.getContentPane();
		JPanel controlPanel = new JPanel();
        //!! controlPanel.add(textLabel);
        controlPanel.add(goButton);
        controlPanel.add(resetButton);
        JPanel fieldsPanel = new JPanel();
        fieldsPanel.setLayout(new GridLayout(5, 2));
        fieldsPanel.add(new JLabel("mass (m)"));
        fieldsPanel.add(massTextField);
        fieldsPanel.add(new JLabel("distance (X)"));
        fieldsPanel.add(distanceXTextField);
        fieldsPanel.add(new JLabel("time (t)"));
        fieldsPanel.add(timeTextField);
        fieldsPanel.add(new JLabel("no. of clocks"));
        fieldsPanel.add(clockCtTextField);
        fieldsPanel.add(new JLabel("deltaX (uncertainty)"));
        fieldsPanel.add(deltaXTextField);
		container.add(controlPanel, BorderLayout.NORTH);
        container.add(fieldsPanel, BorderLayout.CENTER);
        container.add(statusTextField, BorderLayout.SOUTH);
		goButton.addActionListener(this); // action is click on button...
        resetButton.addActionListener(this);
        reset();
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread
	}
    private void reset() {
        massTextField.setText("1.0e9"); // 1
        distanceXTextField.setText("1.0e-6"); // 10
        timeTextField.setText("1");
        clockCtTextField.setText("1"); // 10
        deltaXTextField.setText("0"); // 0.2
    }
	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		if (action.equals("Reset")) {
			reset();
		} else if (action.equals("Go")) {
            quantumGo();
        } else {
			statusTextField.setText("unrecognised action: " + action);
		}
	}

    private void quantumGo() {
        String massStr = massTextField.getText();
        String distanceXStr = distanceXTextField.getText();
        String timeStr = timeTextField.getText();
        String clockCtStr = clockCtTextField.getText();
        String deltaXStr = deltaXTextField.getText();
        double mass, distanceX, time, deltaX;
        int clockCt;
        try {
            massTextField.requestFocusInWindow();
            mass = Double.parseDouble(massStr);
            distanceXTextField.requestFocusInWindow();
            distanceX = Double.parseDouble(distanceXStr);
            timeTextField.requestFocusInWindow();
            time = Double.parseDouble(timeStr);
            clockCtTextField.requestFocusInWindow();
            clockCt = Integer.parseInt(clockCtStr);
            deltaXTextField.requestFocusInWindow();
            deltaX = Double.parseDouble(deltaXStr);
            String result = QClock.moveClocks(
                    mass, distanceX, time, clockCt, deltaX);
            statusTextField.setText(result);
        } catch (NumberFormatException e) {
            statusTextField.setText("Number format exception");
        }
    }
}

