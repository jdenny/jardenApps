package demo.log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Filter;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

/**
 * Simple demo of using Logging.
 * Logging configuration is defined in a file; to use this file add
 *     -Djava.util.logging.config.file=myLogging.properties
 * to VM arguments. 
 * See SecondClass, below, which programmatically adds a new StreamHandler
 * @author john.denny@gmail.com
 *
 */
public class HelloWorldLogger {
	private Logger logger;
	private String className = getClass().getName();
	
	public HelloWorldLogger() {
		logger = Logger.getLogger(className);
	}
	public void sayHello(String name) throws IllegalArgumentException {
		logger.entering(className, "sayHello", name);
		if (name == null || name.length() == 0) {
			throw new IllegalArgumentException("no name supplied");
		}
		logger.info("Hello " + name);
		logger.exiting(className, "sayHello");
	}
 
	public static void main(String[] args) {
		new SecondClass().runProgram();
	}

}

class SecondClass implements Filter {
	private String className = getClass().getName();
	private String loggerChildName = className + ".child";
	private Logger logger;
	private HelloWorldLogger hwl;

	public void runProgram() {
		logger = Logger.getLogger(className);
		hwl = new HelloWorldLogger();
		String name = "Joseph";
		logger.info("about to say hello to " + name);
		try {
			hwl.sayHello(name);
			hwl.sayHello("");
		} catch (Exception e) {
			logger.severe("Exception: " + e.getMessage());
		}
		try {
			createNewHandler();
		} catch (IOException e) {
			logger.severe("Exception: " + e.getMessage());
		}
	}
	private void createNewHandler() throws IOException {
		FileOutputStream fos = new FileOutputStream("/temp/tempLog.txt");
		Handler[] handlers = logger.getHandlers();
		Formatter formatter = null;
		if (handlers.length > 0) {
			formatter = handlers[0].getFormatter();
		}
		if (formatter == null) {
			logger.info("no formatter found; creating new one");
			formatter = new SimpleFormatter();
		}
		StreamHandler streamHandler =
				new StreamHandler(fos, formatter);
		logger.addHandler(streamHandler);
		logger.info("1. this should go to the file & console");
		Logger loggerChild = Logger.getLogger(loggerChildName);
		loggerChild.info("2. this should also go to the file & console");
		logger.setFilter(this);
		logger.info("3. *** should go to neither file nor console");
		loggerChild.info("4. should also go to the file & console");
	}
	@Override
	public boolean isLoggable(LogRecord logRecord) {
		return logRecord.getLoggerName().equals(loggerChildName);
	}
}
