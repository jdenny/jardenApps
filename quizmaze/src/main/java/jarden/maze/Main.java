package jarden.maze;

import javax.swing.JFrame;
import javax.swing.JPanel;

/*
 * to deploy on johnsT500:
 * 		build jar file: quizmazeBuild.xml
 * 		refresh python project: F5
 * 		copy statics to apache: manage.py collectstatic
 * 		restart apache
 */
/* TODO:
Have upload option, so users can create their own files and upload them;
perhaps to a directory associated with the username. Then list all
files for that user.

Add method Player.paintPlayer(int x, int y);
	Inherit spider from player; write new paint shape.
 */

public class Main {

	public static void main(String[] args) {
		System.out.println("jarden.maze.Main.main()");
		JPanel panel = new QuizMazeJPanel();
		JFrame jFrame = new JFrame("Quiz Maze 2.0");
		jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		jFrame.getContentPane().add(panel);
		jFrame.pack();
		jFrame.setVisible(true);
	}
}
