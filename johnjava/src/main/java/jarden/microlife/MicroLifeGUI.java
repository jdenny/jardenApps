package jarden.microlife;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class MicroLifeGUI implements ActionListener {
	private final static String[] columnNames = {
		"Protein (nucleotides)",
		"address",
        "ML source"
	};
	private JLabel statusLabel;
	private MicroLife microLife;
	private Object[][] data;

	public static void main(String[] args) {
		new MicroLifeGUI();
	}
	public MicroLifeGUI() {
		JFrame frame = new JFrame("MicroLife Dashboard");
		JButton goButton = new JButton("Go");
		JButton stepButton = new JButton("Step");
		String fileName = "docs/sort.ml";
		try {
			this.data = getData(fileName);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		JTable table = new JTable(data, columnNames);
		JScrollPane scrollPane = new JScrollPane(table);
		table.setFillsViewportHeight(true);
		statusLabel = new JLabel();
		Container container = frame.getContentPane();
		JPanel controlPanel = new JPanel();
		controlPanel.add(goButton);
		controlPanel.add(stepButton);
		container.add(controlPanel, BorderLayout.NORTH);
		container.add(scrollPane, BorderLayout.CENTER);
		container.add(statusLabel, BorderLayout.SOUTH);
		goButton.addActionListener(this);
		stepButton.addActionListener(this);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread
	}

	private Object[][] getData(String mlFileName) throws IOException {
		String outFileName = "/temp/output.txt";
		Tools.compileToFile(mlFileName, outFileName);
		InputStream is = new FileInputStream(outFileName);
		char[] protein = Tools.inputStreamToCharArray(is);
		this.microLife = new MicroLife(protein);
		return Tools.reverseCompile(protein);
	}
	@Override
	public void actionPerformed(ActionEvent event) {
		if (event.getActionCommand().equals("Go")) {
			this.statusLabel.setText("Go not yet implemented!");
		} else if (event.getActionCommand().equals("Step")) {
			int pc = this.microLife.stepProtein();
			int[] registers = microLife.getRegisters();
			System.out.println("pc=" + pc + "; r0=" + registers[0] +
					"; r1=" + registers[1]);
			//System.out.println("result=" + microLife.getResult());
			//System.out.println("finished=" + microLife.isFinished());
		} 
	}

}
