package hu.bme.tmit.wikilinker.callback;

import hu.bme.tmit.wikilinker.BruteSanitezer;
import hu.bme.tmit.wikilinker.Sanitezer;
import hu.bme.tmit.wikilinker.db.SQLite;
import hu.bme.tmit.wikilinker.logger.Logger;
import hu.bme.tmit.wikilinker.model.Anchor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import com.almworks.sqlite4java.SQLiteException;
import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.io.Files;

import edu.jhu.nlp.wikipedia.WikiPage;

public class LinkerCallback extends AbstractPageCallback {

	private Tokenizer tokenizer;
	private HashSet<String> stopList = new HashSet<>();
	private HashSet<String> filterList = new HashSet<>();
	private static final Logger LOGGER = new Logger(LinkerCallback.class);
	private SQLite db;
	private Predicate<String> predicate;
	private Sanitezer sanitezer;

	public LinkerCallback() throws FileNotFoundException, IOException, SQLiteException {
		TokenizerModel tokenModel = null;
		try (FileInputStream modelStream = new FileInputStream("resources\\en-token.bin")) {
			tokenModel = new TokenizerModel(modelStream);
		}
		tokenizer = new TokenizerME(tokenModel);
		filterList.addAll(Files.readLines(new File("resources\\custom-filter.txt"), Charsets.UTF_8));
		stopList.addAll(Files.readLines(new File("resources\\english.stop.txt"), Charsets.UTF_8));
		File file = new File("db/wikidb");
		if (!file.exists()) {
			LOGGER.e("No database found. Exiting...");
			System.exit(-1);
			return;
		}
		file = null;
		db = new SQLite("db/wikidb");
		predicate = new LinkerPredicate();
		sanitezer = new BruteSanitezer();
	}

	@Override
	public void process(WikiPage page) {
		super.process(page);
		if (page.isRedirect() || page.isDisambiguationPage() || page.isSpecialPage()) {
			LOGGER.d(MessageFormat.format(
					"Page ({0}) is a disambbiguation page, a special page or a redirect. No processing.",
					page.getTitle()));
			return;
		}
		LOGGER.d(MessageFormat.format("Processing page: {0}", page.getTitle()));

		/* Tokenize */
		String[] toks;
		toks = tokenizer.tokenize(page.getText());
		List<String> toksTemp = new ArrayList<>();

		/* Stoplist és Predicate */
		for (String token : toks) {
			if (!stopList.contains(token) && predicate.apply(token)) {
				toksTemp.add(sanitezer.sanitize(token));
			}
		}
		toks = toksTemp.toArray(new String[toksTemp.size()]);
		/* Process the page */
		for (int i = 0; i < toks.length; i++) {
			Anchor anchor = null;
			try {
				anchor = db.getAnchor(toks[i].toLowerCase());
			} catch (SQLiteException e) {
				LOGGER.w("SQL Error occured. Reason: " + e.getMessage());
			}
			// LOGGER.i(MessageFormat.format("Anchor tested: {0}. Result: {1}",
			// toks[i], anchor == null ? "NO" : "FOUND"));
			if (anchor == null) {
				continue;
			}
			LOGGER.i(MessageFormat.format("Anchor found: {0}", anchor));

		}

	}

	private class LinkerPredicate implements Predicate<String> {

		@Override
		public boolean apply(String arg0) {
			return !Strings.isNullOrEmpty(arg0) && arg0.length() > 1 && !inFilterList(arg0);
		}

		private boolean inFilterList(String arg0) {
			for (String stopWord : filterList) {
				if (arg0.contains(stopWord)) {
					return true;
				}
			}
			return false;
		}
	}
}
