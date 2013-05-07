package hu.bme.tmit.wikilinker;

import hu.bme.tmit.wikilinker.callback.AbstractPageCallback;
import hu.bme.tmit.wikilinker.callback.DBAggregateCallback;
import hu.bme.tmit.wikilinker.callback.LinkerCallback;
import hu.bme.tmit.wikilinker.db.AnchorsTable;
import hu.bme.tmit.wikilinker.db.SQLite;
import hu.bme.tmit.wikilinker.db.TitlesTable;
import hu.bme.tmit.wikilinker.logger.Logger;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.regex.Pattern;

import com.google.common.base.Splitter;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;

import edu.jhu.nlp.wikipedia.WikiXMLParser;
import edu.jhu.nlp.wikipedia.WikiXMLParserFactory;

public class WikiLinker {

	private static final Logger LOG = new Logger(WikiLinker.class);
	public static int logInterval = -1;

	public static void main(String[] args) {
		if (args.length < 2 || !isValidCommand(args[0])) {
			exitWithError();
		}
		ArrayList<String> paths = null;
		final String command = args[0];
		for (int i = 1; i < args.length; i++) {
			String actualParam = args[i];
			if (actualParam.startsWith("dump")) {
				paths = extractPaths(actualParam);
			}
			if (actualParam.startsWith("-l")) {
				try {
					logInterval = Integer.parseInt(args[i + 1]);
				} catch (NumberFormatException e) {
					// Do nothing. logInterval stays as it was.
				}
			}
		}
		if (paths == null) {
			exitWithError();
		}
		WikiLinker extractor = new WikiLinker();
		switch (command) {
		case "extract":
			for (String path : paths) {
				extractor.extract(path);
			}
			break;
		case "link":
			extractor.link(paths.get(0));
			break;
		case "index":
			extractor.createIndex();
			break;
		default:
			throw new IllegalArgumentException("Unknown command");
		}
		exit();
	}

	private static void exitWithError() {
		printUsage();
		exit();
	}

	private static void exit() {
		System.out.println("Exiting.");
		System.exit(-1);
	}

	private static ArrayList<String> extractPaths(String actualParam) {
		actualParam = actualParam.replaceFirst(Pattern.quote("dump="), "");
		Iterable<String> split = Splitter.on(",").omitEmptyStrings().trimResults().split(actualParam);
		return Lists.newArrayList(split);

	}

	private static boolean isValidCommand(String command) {
		if (command == null) {
			return false;
		}
		return "extract".equalsIgnoreCase(command) || "link".equalsIgnoreCase(command)
				|| "index".equalsIgnoreCase(command);
	}

	public static void printUsage() {
		StringBuilder bld = new StringBuilder("Usage:\n");
		bld.append("hu.bme.tmit.wikilinker.WikiLinker <command> <params> dump=path1,[path2,path3...]").append("\n\n");
		bld.append("Command").append("\n");
		bld.append("\t").append("extract").append("\t").append("Extract anchors from dump.").append("\n");
		bld.append("\t").append("index").append("\t").append("Creates index on DB tables").append("\n");
		bld.append("\t").append("link").append("\t").append("Link a Wikipedia page.").append("\n");
		bld.append("Parameters").append("\n");
		bld
				.append("\t")
				.append("dump")
				.append("\t")
				.append("The path(s) of the dump(s) to process. No space is allowed in or between the path(s)")
				.append("\n");
		bld.append("Parameters").append("\n");
		bld
				.append("\t")
				.append("-l <logInterval>")
				.append("\t")
				.append("Callback logs progress every <logInterval> page")
				.append("\n");
		System.out.println(bld.toString());
	}

	private void extract(String path) {
		LOG.i("Extraction started");
		WikiXMLParser parser = WikiXMLParserFactory.getSAXParser(path);
		try {
			Logger.setLevel(Logger.INFO);

			AbstractPageCallback callback = new DBAggregateCallback();
			parser.setPageCallback(callback);
			callback.setLogPagesInterval(logInterval);

			// Start your engine...
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			// Start!
			parser.parse();
			callback.onFinished();
			// Finish Line
			stopwatch.stop();

			// Excel miatt
			Locale.setDefault(Locale.US);
			System.out.println("-------------------------- STATISTICS --------------------------");
			final int parsedPages = callback.getParsedPages();
			LOG.i("Pages:\t" + parsedPages);
			final int redirects = callback.getRedirects();
			LOG.i(MessageFormat.format("Redirects:\t{0} - {1}% of total", redirects, redirects * 100 / parsedPages));
			LOG.i("Time:\t\t" + stopwatch.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void link(String path) {
		LOG.i("Linking started");
		WikiXMLParser parser = WikiXMLParserFactory.getSAXParser(path);
		try {
			Logger.setLevel(Logger.INFO);
			AbstractPageCallback callback = new LinkerCallback();

			parser.setPageCallback(callback);
			callback.setLogPagesInterval(logInterval);

			// Start your engine...
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			// Start!
			parser.parse();
			callback.onFinished();
			// Finish Line
			stopwatch.stop();

			// Excel miatt
			Locale.setDefault(Locale.US);
			System.out.println("-------------------------- STATISTICS --------------------------");
			final int parsedPages = callback.getParsedPages();
			LOG.i("Pages:\t" + parsedPages);
			final int redirects = callback.getRedirects();
			LOG.i(MessageFormat.format("Redirects:\t{0} - {1}% of total", redirects, redirects * 100 / parsedPages));
			LOG.i("Time:\t\t" + stopwatch.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void createIndex() {
		LOG.i("Creating index...");
		SQLite db = null;
		try {
			File file = new File("db/wikidb");
			if (!file.exists()) {
				LOG.e("db file doesn't exist");
				return;
			}
			db = new SQLite("db/wikidb");
			Logger.setLevel(Logger.INFO);
			// Start your engine...
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			// Start!
			db.createIndex(AnchorsTable.TABLE_NAME, AnchorsTable.FIELD_ANCHOR, AnchorsTable.INDEX_ANCHOR);
			db.createIndex(TitlesTable.TABLE_NAME, TitlesTable.FIELD_TITLE, TitlesTable.INDEX_TITLE);
			// Finish Line
			stopwatch.stop();

			// Excel miatt
			Locale.setDefault(Locale.US);
			System.out.println("-------------------------- STATISTICS --------------------------");
			LOG.i("Time:\t\t" + stopwatch.toString());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) {
				db.dispose();
			}
		}

	}

}
