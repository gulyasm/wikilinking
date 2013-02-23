package hu.bme.tmit.wikilinker.stemmer;

import org.tartarus.snowball.SnowballStemmer;

public class DummyStemmer extends SnowballStemmer {

	@Override
	public boolean stem() {
		return false;
	}

}
