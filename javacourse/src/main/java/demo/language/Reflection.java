package demo.language;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

/**
 * Uses reflection to show information about a class name you supply.
 */
public class Reflection implements ActionListener {
	private JTextField textField;
	private JTextArea messageArea;
	private JLabel statusLabel;

	public static void main(String[] args) {
		//Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
            	new Reflection();
            }
        });
	}
	public Reflection() {
		JFrame frame = new JFrame("Reflection");
		JLabel textLabel = new JLabel("Class name:");
		textField = new JTextField(20);
		messageArea = new JTextArea(10, 20);
		statusLabel = new JLabel();
		JButton goButton = new JButton("Go");
		JButton clearButton = new JButton("Clear");
		Container container = frame.getContentPane();
		JPanel controlPanel = new JPanel();
		container.add(controlPanel, BorderLayout.NORTH);
		container.add(messageArea, BorderLayout.CENTER);
		container.add(statusLabel, BorderLayout.SOUTH);
		controlPanel.add(textLabel);
		controlPanel.add(textField);
		controlPanel.add(goButton);
		controlPanel.add(clearButton);
		goButton.addActionListener(this); // action is click on button...
		textField.addActionListener(this); // ... or press Enter
		clearButton.addActionListener(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		statusLabel.setText("");
		String action = event.getActionCommand();
		if (action.equals("Clear")) {
			textField.setText("");
			messageArea.setText("");
		} else {
			messageArea.setText("");
			try {
				showClass(textField.getText(), messageArea);
			} catch (Exception e1) {
				e1.printStackTrace();
				statusLabel.setText(e1.toString());
			}
		}
	}
	private static void showClass(String className, JTextArea messageArea) throws Exception {
		Class<?> clazz = Class.forName(className);
		messageArea.append(Modifier.toString(clazz.getModifiers()) + " class " +
				clazz.getName() + " {\n");
		Field[] fields = clazz.getDeclaredFields();
		for (Field field: fields) {
			messageArea.append("   " + Modifier.toString(field.getModifiers()) + " ");
			messageArea.append(field.getType().getName() + " " + field.getName() + ";\n");
		}
		Constructor<?>[] constructors = clazz.getConstructors();
		if (constructors.length > 0) {
			messageArea.append("\n");
			for (Constructor<?> constructor: constructors) {
				messageArea.append("   " + Modifier.toString(constructor.getModifiers()) + " ");
				messageArea.append(constructor.getName());
				printParameters(constructor.getParameterTypes(), messageArea);
			}
		}
		Method[] methods = clazz.getDeclaredMethods();
		if (methods.length > 0) {
			messageArea.append("\n");
			for (Method method: methods) {
				messageArea.append("   " + Modifier.toString(method.getModifiers()) + " ");
				messageArea.append(method.getReturnType().getName());
				messageArea.append(" " + method.getName());
				printParameters(method.getParameterTypes(), messageArea);
			}
		}
		messageArea.append("}\n\n\n");
	}
	private static void printParameters(Class<?>[] parameterTypes, JTextArea messageArea) {
		messageArea.append("(");
		StringBuilder builder = new StringBuilder();
		for (Class<?> type: parameterTypes) {
			if (type.isArray()) {
				builder.append(type.getComponentType().getName() + "[]");
			} else {
				builder.append(type.getName());
			}
			builder.append(", ");
		}
		if (builder.length() > 2) {
			// remove last ", "
			builder.setLength(builder.length() - 2);
		}
		messageArea.append(builder.toString() + ");\n");
	}
}

