package jarden.life.gui;

import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

/**
 * Created by john.denny@gmail.com on 14/02/2017.
 */

public class LifeSwing {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Life is complicated");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JLabel label = new JLabel("Hola Juan");
        Container contentPane = frame.getContentPane();
        contentPane.add(label);
        frame.pack();
        frame.setVisible(true);
    }
}
