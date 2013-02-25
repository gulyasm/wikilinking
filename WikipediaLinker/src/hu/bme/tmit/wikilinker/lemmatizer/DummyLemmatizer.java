package hu.bme.tmit.wikilinker.lemmatizer;


public class DummyLemmatizer implements Lemmatizer {

	@Override
	public void setCurrent(String word) {
	}

	@Override
	public boolean lemmatize() {
		return false;
	}

	@Override
	public String getCurrent() {
		return null;
	}


}
