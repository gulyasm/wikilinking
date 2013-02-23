package hu.bme.tmit.wikilinker;

import hu.bme.tmit.wikilinker.linking.SimpleWikiLinking;
import hu.bme.tmit.wikilinker.linking.WikiLinking;
import hu.bme.tmit.wikilinker.logger.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

public class WikiApp {

	public static final Logger Log = new Logger(WikiApp.class);

	public static void main(String[] args) throws FileNotFoundException {
		if (args.length < 2) {
			printHelp();
			exit();
		}
		String path = null;
		boolean debug = false;
		for (int i = 0; i < args.length; i++) {
			if ("-f".matches(args[i])) {
				path = args[i + 1];
			}
			if ("-d".matches(args[i])) {
				debug = Boolean.parseBoolean(args[i + 1]);
			}
		}
		File file = new File(path);
		if (!file.exists()) {
			printHelp();
			exit();

		}
		if (!file.canRead()) {
			Log.e("Cannot read file!");
			exit();

		}
		WikiLinking linking = new SimpleWikiLinking(file);
		linking.setDebug(debug);
		Collection<WikiLink> link = linking.link(file);

		Log.l("------------------------------------");
		for (WikiLink wikiLink : link) {
			Log.l(wikiLink.toString());
		}

	}

	private static void exit() {
		Log.e("Exiting...");
		System.exit(-1);
	}

	private static void printHelp() {
		System.out.println("Usage:\tWikiApp -f <file_name> -d [true|false]");
	}

}
