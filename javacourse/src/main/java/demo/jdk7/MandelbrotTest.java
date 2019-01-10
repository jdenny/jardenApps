package demo.jdk7;

import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MandelbrotTest {
	public static void main(String[] args) {
		new MandelbrotTest();
	}
	public MandelbrotTest() {
		JFrame frame = new JFrame("MandelbrotTest");
		Container contentPane = frame.getContentPane();
		contentPane.add(new MandelbrotPanel(), "Center");
		
		frame.setSize(400, 400);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true); // start event handling thread
	}
}

class MandelbrotPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		BufferedImage image = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		generate(image);
		g.drawImage(image, 0, 0, null);
	}

	public void generate(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		WritableRaster raster = image.getRaster();
		ColorModel model = image.getColorModel();

		Color fractalColor = Color.red;
		int argb = fractalColor.getRGB();
		Object colorData = model.getDataElements(argb, null);

		for (int i = 0; i < width; i++)
			for (int j = 0; j < height; j++) {
				double a = XMIN + i * (XMAX - XMIN) / width;
				double b = YMIN + j * (YMAX - YMIN) / height;
				if (!escapesToInfinity(a, b))
					raster.setDataElements(i, j, colorData);
			}
	}

	private boolean escapesToInfinity(double a, double b) {
		double x = 0.0;
		double y = 0.0;
		int iterations = 0;
		do {
			double xnew = x * x - y * y + a;
			double ynew = 2 * x * y + b;
			x = xnew;
			y = ynew;
			iterations++;
			if (iterations == MAX_ITERATIONS)
				return false;
		} while (x <= 2 && y <= 2);
		return true;
	}

	private static final double XMIN = -2;

	private static final double XMAX = 2;

	private static final double YMIN = -2;

	private static final double YMAX = 2;

	private static final int MAX_ITERATIONS = 16;

}