package hu.bme.tmit.wikilinker.logger;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {

	private final String tag;
	private static final SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss:SSS");
	public static final int VERBOSE = 1;
	public static final int DEBUG = 2;
	public static final int INFO = 3;
	public static final int ERROR = 4;
	public static final int NONE = Integer.MAX_VALUE;
	private static int LEVEL = DEBUG;

	public Logger(String tag) {
		this.tag = tag;
	}

	public Logger(Class<?> klazz) {
		this.tag = klazz.getSimpleName();
	}

	private static void log(String tag, int lvl, String msg) {
		if (lvl < LEVEL) return;
		System.out.println(String.format(
				"%-13s %-6s %-20s %-30s",
				formatter.format(new Date()),
				getLevelLabel(lvl),
				tag,
				msg));
	}

	public static void setLevel(int level) {
		LEVEL = level;
	}

	private static String getLevelLabel(int lvl) {
		switch (lvl) {
		case VERBOSE:
			return "VERBOSE";
		case DEBUG:
			return "DEBUG";
		case INFO:
			return "INFO";
		case ERROR:
			return "ERROR";
		default:
			throw new IllegalArgumentException();
		}
	}

	public void i(String message) {
		log(tag, INFO, message);
	}

	public void d(String message) {
		log(tag, DEBUG, message);
	}

	public void e(String message) {
		log(tag, ERROR, message);
	}

	public void v(String message) {
		log(tag, VERBOSE, message);
	}
}
