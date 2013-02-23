package hu.bme.tmit.wikilinker.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private final String tag;
	private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");

	public Logger(String tag) {
		this.tag = tag;
	}

	public Logger(Class<?> klazz) {
		this.tag = klazz.getSimpleName();
	}

	private static void log(String tag, String lvl, String msg) {
		System.out.println(String.format("%-13s %-6s %-20s %-30s", formatter.format(new Date()), lvl, tag, msg));
	}

	public void l(String message) {
		log(tag, "INFO", message);
	}

	public void d(String message) {
		log(tag, "DEBUG", message);
	}

	public void e(String message) {
		log(tag, "ERROR", message);
	}

}
