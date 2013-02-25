package hu.bme.tmit.wikilinker.lemmatizer;

import org.tartarus.snowball.SnowballStemmer;

public class SnowballStemmerWrapper implements Lemmatizer {
	
	private SnowballStemmer stemmer;

	public SnowballStemmerWrapper(SnowballStemmer stemmer) {
		this.stemmer = stemmer;
	}

	@Override
	public void setCurrent(String word) {
		stemmer.setCurrent(word);

	}

	@Override
	public boolean lemmatize() {
		return stemmer.stem();
	}

	@Override
	public String getCurrent() {
		return stemmer.getCurrent();
	}

}
