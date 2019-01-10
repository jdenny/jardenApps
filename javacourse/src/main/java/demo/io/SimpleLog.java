package demo.io;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

public class SimpleLog {

	public static void main(String[] args) throws IOException {
		File file = new File("/temp/log.txt");
		String line = "message produced " + new Date();
		writeTextToFile(line, file);
	}
	public static void writeTextToFile(String message, File file) throws IOException {
		PrintWriter writer = null;
		boolean append = true;
		try {
			FileWriter fWriter = new FileWriter(file, append);
			writer = new PrintWriter(fWriter);
			writer.println(message);
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}

}
