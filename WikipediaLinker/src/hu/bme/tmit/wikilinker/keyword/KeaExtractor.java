package hu.bme.tmit.wikilinker.keyword;

import hu.bme.tmit.wikilinker.logger.Logger;
import hu.bme.tmit.wikilinker.stoplist.KEAWrapper;

import java.util.Collections;
import java.util.List;

import kea.main.KEAKeyphraseExtractor;
import kea.main.KEAModelBuilder;
import kea.stemmers.PorterStemmer;

public class KeaExtractor implements KeywordExtractor {

	private static final String KEA_MODEL_PATH = "resources/kea/model";
	private static final String KEA_TRAIN_PATH = "resources/kea/train";
	private static final String KEA_TEST_PATH = "resources/kea/test";
	private static final Logger Log = new Logger(KeaExtractor.class);
	private KEAModelBuilder km;
	private KEAKeyphraseExtractor ke;

	@Override
	public List<Keyword> extract() {
		// Train
		setOptionsTraining();
		createModel();
		
		// Test
		setOptionsTesting(KEA_TEST_PATH);
		extractKeyphrases();
		return Collections.emptyList();
	}

	private void extractKeyphrases() {
		try {
			ke.loadModel();
			ke.extractKeyphrases(ke.collectStems());
		} catch (Exception e) {
			Log.e(e.getMessage());
		}
	}

	private void createModel() {
		try {
			km.buildModel(km.collectStems());
			km.saveModel();
		} catch (Exception e) {
			Log.e(e.getMessage());
		}
	}

	private void setOptionsTraining() {

		km = new KEAModelBuilder();

		// A. required arguments (no defaults):

		// 1. Name of the directory -- give the path to your directory with
		// documents and keyphrases
		// documents should be in txt format with an extention "txt"
		// keyphrases with the same name as documents, but extension "key"
		// one keyphrase per line!
		km.setDirName(KEA_TRAIN_PATH);

		// 2. Name of the model -- give the path to where the model is to be
		// stored and its name
		km.setModelName(KEA_MODEL_PATH);

		// 3. Name of the vocabulary -- name of the file (without extension)
		// that is stored in VOCABULARIES
		// or "none" if no Vocabulary is used (free keyphrase extraction).
		km.setVocabulary("none");

		// 4. Format of the vocabulary in 3. Leave empty if vocabulary = "none",
		// use "skos" or "txt" otherwise.
		km.setVocabularyFormat("");

		// B. optional arguments if you want to change the defaults
		// 5. Encoding of the document
		km.setEncoding("UTF-8");

		// 6. Language of the document -- use "es" for Spanish, "fr" for French
		// or other languages as specified in your "skos" vocabulary
		km.setDocumentLanguage("en"); // es for Spanish, fr for French

		// 7. Stemmer -- adjust if you use a different language than English or
		// if you want to alterate results
		// (We have obtained better results for Spanish and French with
		// NoStemmer)
		km.setStemmer(new PorterStemmer());

		// 8. Stopwords -- adjust if you use a different language than English!
		km.setStopwords(new KEAWrapper());

		// 9. Maximum length of a keyphrase
		km.setMaxPhraseLength(5);

		// 10. Minimum length of a keyphrase
		km.setMinPhraseLength(1);

		// 11. Minumum occurrence of a phrase in the document -- use 2 for long
		// documents!
		km.setMinNumOccur(2);

		// Optional: turn off the keyphrase frequency feature
		// km.setUseKFrequency(false);

	}

	private void setOptionsTesting(String testDirectory) {

		ke = new KEAKeyphraseExtractor();

		// A. required arguments (no defaults):

		// 1. Name of the directory -- give the path to your directory with
		// documents
		// documents should be in txt format with an extention "txt".
		// Note: keyphrases with the same name as documents, but extension "key"
		// one keyphrase per line!

		ke.setDirName(testDirectory);

		// 2. Name of the model -- give the path to the model
		ke.setModelName(KEA_MODEL_PATH);

		// 3. Name of the vocabulary -- name of the file (without extension)
		// that is stored in VOCABULARIES
		// or "none" if no Vocabulary is used (free keyphrase extraction).
		ke.setVocabulary("none");

		// 4. Format of the vocabulary in 3. Leave empty if vocabulary = "none",
		// use "skos" or "txt" otherwise.
		ke.setVocabularyFormat("");

		// B. optional arguments if you want to change the defaults
		// 5. Encoding of the document
		ke.setEncoding("UTF-8");

		// 6. Language of the document -- use "es" for Spanish, "fr" for French
		// or other languages as specified in your "skos" vocabulary
		ke.setDocumentLanguage("en"); // es for Spanish, fr for French

		// 7. Stemmer -- adjust if you use a different language than English or
		// want to alterate results
		// (We have obtained better results for Spanish and French with
		// NoStemmer)
		ke.setStemmer(new PorterStemmer());

		// 8. Stopwords
		ke.setStopwords(new KEAWrapper());

		// 9. Number of Keyphrases to extract
		ke.setNumPhrases(10);

		// 10. Set to true, if you want to compute global dictionaries from the
		// test collection
		ke.setBuildGlobal(false);
	}

}
