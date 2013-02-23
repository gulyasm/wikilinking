package hu.bme.tmit.wikilinker.linking;

import hu.bme.tmit.wikilinker.sanitizer.DummySanitizer;
import hu.bme.tmit.wikilinker.sanitizer.Sanitezer;
import hu.bme.tmit.wikilinker.stemmer.DummyStemmer;
import hu.bme.tmit.wikilinker.stoplist.DummyStoplistSupplier;
import hu.bme.tmit.wikilinker.wikialg.DummyWikiAlgorithm;
import hu.bme.tmit.wikilinker.wikialg.WikiLinkingAlgorithm;

import java.io.File;
import java.util.List;

import org.tartarus.snowball.SnowballStemmer;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.base.Supplier;

public class DummyLinking extends SimpleWikiLinking {

	public DummyLinking(File file) {
		super(file);
	}

	@Override
	protected Sanitezer getSanitezer() {
		return new DummySanitizer();
	}

	@Override
	protected Predicate<String> getPredicate() {
		return Predicates.alwaysTrue();
	}

	@Override
	protected SnowballStemmer getSnowballStemmer() {
		return new DummyStemmer();
	}

	@Override
	protected Supplier<List<String>> getStoplistSupplier() {
		return new DummyStoplistSupplier();
	}

	@Override
	protected WikiLinkingAlgorithm getWikiLinkingAlgorithm() {
		return new DummyWikiAlgorithm();
	}

}
