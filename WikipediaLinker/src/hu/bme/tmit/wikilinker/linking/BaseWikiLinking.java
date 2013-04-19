package hu.bme.tmit.wikilinker.linking;

import hu.bme.tmit.wikilinker.WikiLink;
import hu.bme.tmit.wikilinker.lemmatizer.Lemmatizer;
import hu.bme.tmit.wikilinker.logger.Logger;
import hu.bme.tmit.wikilinker.sanitizer.Sanitezer;
import hu.bme.tmit.wikilinker.wikialg.WikiLinkingAlgorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import opennlp.tools.postag.POSTagger;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.InvalidFormatException;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.io.CharStreams;

public abstract class BaseWikiLinking implements WikiLinking {

	public static final Logger Log = new Logger(BaseWikiLinking.class);
	protected boolean debug;
	private File wikiFile;

	public BaseWikiLinking(File file) {
		this.wikiFile = file;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	protected abstract Lemmatizer getLemmatizer();

	protected abstract Tokenizer getTokenizer() throws InvalidFormatException, IOException;

	protected abstract Sanitezer getSanitezer();

	protected abstract Predicate<String> getPredicate();

	protected abstract WikiLinkingAlgorithm getWikiLinkingAlgorithm();

	protected abstract Supplier<List<String>> getStoplistSupplier();

	protected abstract POSTagger getPosTagger() throws FileNotFoundException, IOException;

	@Override
	public Collection<WikiLink> link(File file) {
		/* Configuration */
		try {
			Tokenizer tokenizer = getTokenizer();
			WikiLinkingAlgorithm linkingAlgorithm = getWikiLinkingAlgorithm();
			Sanitezer sanitezer = getSanitezer();
			Supplier<List<String>> stoplistSupplier = getStoplistSupplier();
			POSTagger posTagger = getPosTagger();
			Lemmatizer lemmatizer = getLemmatizer();
			Predicate<String> predicate = getPredicate();
			Set<String> stopList = new HashSet<>(stoplistSupplier.get());

			/* Tokenize */
			String[] toks;
			toks = tokenizer.tokenize(CharStreams.toString(new FileReader(wikiFile)));
			List<String> toksTemp = new ArrayList<>();

			/* Stoplist és Predicate */
			for (String token : toks) {
				if (!stopList.contains(token) && predicate.apply(token)) {
					toksTemp.add(sanitezer.sanitize(token));
				}
			}
			toks = toksTemp.toArray(new String[toksTemp.size()]);

			/* POS Tagging */
			String[] tags = posTagger.tag(toks);

			/* Collecting NOUNS (and stemming..) */
			List<String> nouns = new ArrayList<>();
			for (int i = 0; i < tags.length; i++) {
				if (tags[i].startsWith("NN")) {
					String word = toks[i];
					/* Plural, stemming */
					if (tags[i].endsWith("S")) {
						lemmatizer.setCurrent(toks[i]);
						if (lemmatizer.lemmatize()) {
							word = lemmatizer.getCurrent();
							if (debug) {
								Log.d(MessageFormat.format("Stemming: {0} -> {1}", toks[i], word));
							}
						}
					}
					nouns.add(word);
				}
			}

			/* Linking */
			Collection<WikiLink> links = new ArrayList<>();
			links = linkingAlgorithm.link(nouns);

			/* Wohohoho! We're done! */
			return links;
		} catch (IOException e) {
			Log.e(e.getMessage());
			return null;
		}
	}

}
