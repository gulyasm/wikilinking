package hu.bme.tmit.wikilinker.linking;

import hu.bme.tmit.wikilinker.sanitizer.BruteSanitezer;
import hu.bme.tmit.wikilinker.sanitizer.Sanitezer;
import hu.bme.tmit.wikilinker.stoplist.StoplistFactory;
import hu.bme.tmit.wikilinker.wikialg.DummyWikiAlgorithm;
import hu.bme.tmit.wikilinker.wikialg.WikiLinkingAlgorithm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTagger;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

import org.tartarus.snowball.SnowballStemmer;
import org.tartarus.snowball.ext.englishStemmer;

import com.google.common.base.Predicate;
import com.google.common.base.Supplier;

public class SimpleWikiLinking extends BaseWikiLinking {

	public SimpleWikiLinking(File file) {
		super(file);
	}

	protected Tokenizer getTokenizer() throws InvalidFormatException, IOException {
		TokenizerModel tokenModel = null;
		try (FileInputStream modelStream = new FileInputStream(
				"e:\\Users\\gulyasm\\workspace-wikipedia\\WikipediaLinker\\resources\\en-token.bin")) {
			tokenModel = new TokenizerModel(modelStream);
		}
		Tokenizer tokenizer = new TokenizerME(tokenModel);
		return tokenizer;
	}

	protected SnowballStemmer getSnowballStemmer() {
		return new englishStemmer();
	}

	protected Sanitezer getSanitezer() {
		return new BruteSanitezer();
	}

	protected WikiLinkingAlgorithm getWikiLinkingAlgorithm() {
		return new DummyWikiAlgorithm();
	}

	protected Supplier<List<String>> getStoplistSupplier() {
		return new StoplistFactory();
	}

	protected Predicate<String> getPredicate() {
		return new Predicate<String>() {

			@Override
			public boolean apply(String arg0) {
				return arg0.length() > 2;
			}
		};
	}

	protected POSTagger getPosTagger() throws FileNotFoundException, IOException {
		POSModel posmodel = null;
		try (FileInputStream modelStream = new FileInputStream(
				"e:\\Users\\gulyasm\\workspace-wikipedia\\WikipediaLinker\\resources\\en-pos-maxent.bin")) {
			posmodel = new POSModel(modelStream);
		}
		POSTaggerME tagger = new POSTaggerME(posmodel);
		return tagger;
	}

}
