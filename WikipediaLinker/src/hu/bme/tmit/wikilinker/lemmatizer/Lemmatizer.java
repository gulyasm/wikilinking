package hu.bme.tmit.wikilinker.lemmatizer;

public interface Lemmatizer {
	
	void setCurrent(String word);
	
	boolean lemmatize();
	
	String getCurrent();

}
