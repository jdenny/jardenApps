package solution;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.util.Date;

public class LogMessages {

	public static void main(String[] args) throws IOException {
		Path path = Paths.get("c:/temp/logfile.txt");
		logMessage("hello John", path);
		logMessage("Nos vemos", path);
		System.out.println("bye John");
	}

	public static void logMessage(String message, Path path) throws IOException {
		try (BufferedWriter osw = Files.newBufferedWriter(path,
				Charset.defaultCharset(), StandardOpenOption.APPEND,
				StandardOpenOption.CREATE);
				PrintWriter writer = new PrintWriter(osw)) {
			String messagePlus = MessageFormat.format(
					"<{0,date} {0,time}> {1}", new Date(), message);
			writer.println(messagePlus);
		}
	}

}
