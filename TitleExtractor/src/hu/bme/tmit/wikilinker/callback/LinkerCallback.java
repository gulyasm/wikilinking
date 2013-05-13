package hu.bme.tmit.wikilinker.callback;

import hu.bme.tmit.wikilinker.BruteSanitezer;
import hu.bme.tmit.wikilinker.Sanitezer;
import hu.bme.tmit.wikilinker.db.SQLite;
import hu.bme.tmit.wikilinker.logger.Logger;
import hu.bme.tmit.wikilinker.model.Anchor;
import hu.bme.tmit.wikilinker.model.Category;
import hu.bme.tmit.wikilinker.model.Hit;
import hu.bme.tmit.wikilinker.model.LevenshteinDistance;
import hu.bme.tmit.wikilinker.model.Page;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import com.almworks.sqlite4java.SQLiteException;
import com.google.common.base.Charsets;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
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
	private PrintWriter outputStream;

	public LinkerCallback(OutputStream stream) throws FileNotFoundException, IOException, SQLiteException {
		this.outputStream = new PrintWriter(stream);
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

		/* Stoplist and Predicate */
		for (String token : toks) {
			if (!stopList.contains(token) && predicate.apply(token)) {
				toksTemp.add(sanitezer.sanitize(token));
			}
		}
		toks = toksTemp.toArray(new String[toksTemp.size()]);
		/* Eliminate duplicate occurrences */
		Set<String> tokenSet = Sets.newHashSet(toks);

		/* Process the page along tokenSet set */
		for (Iterator<String> istr = tokenSet.iterator(); istr.hasNext();) {
			Anchor anchor = null;
			String token = istr.next();
			try {
				anchor = db.getAnchor(token);
			} catch (SQLiteException e) {
				LOGGER.w("SQL Error occured. Reason: " + e.getMessage());
			}
			if (anchor == null) {
				continue;
			}
			System.out.println("Anchor found: " + anchor.getName());
			double maxsim = -1.0; //
			Set<Page> titles = anchor.getTitles();
			List<Hit> hits = new ArrayList<>();
			Page querypage = null;
			for (Iterator<Page> it = titles.iterator(); it.hasNext();) {
				Page title = it.next();
				double sim = 0.0;
				double rld = 0.0;
				Page hitPage = null;
				int ld = LevenshteinDistance.compute(token, title.getName());
				
				// Reverse Levenshtein Distance
				if (ld != 0)
					rld = (double) 1.0/ld;
				else
					rld = 1.0;
				
				try {
					querypage = db.getPage(title.getName().toLowerCase().trim());
				} catch (SQLiteException e) {
					LOGGER.w("SQL Error occured. Reason: " + e.getMessage());
				}
				if (querypage != null) {
					sim = similarity(page.getCategories(), querypage.getCategoryNames());
					hitPage = querypage;
				}
				else hitPage = new Page(title.getName(), title.getUrl());
				
				hits.add(new Hit(hitPage, sim + rld));
			}
			Collections.sort(hits);
			Collections.reverse(hits);
			for(int i = 0; i < hits.size(); i++)
				System.out.println((i+1) + ". hit: " + hits.get(i).getPage().getName());
			if (maxsim > -1) {
				outputStream.println(MessageFormat.format(
						"{0}\t{1}",
						hits.get(0).getOutputFormat(),
						anchor.getOutputFormat()));
			}
		}
		System.out.println("--------------------------- Hits ---------------------------");
	}

	@Override
	public void onFinished() {
		super.onFinished();
		outputStream.close();
	}

	private double similarity(Vector<String> test, List<String> retrieved) {
		int s = test.size();
		if (s == 0) return 0.0;
		double count = 0.0;
		for (int i = 0; i < s; i++)
			if (retrieved.contains(test.get(i))) count += 1.0;
		return (double) count / s;
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
