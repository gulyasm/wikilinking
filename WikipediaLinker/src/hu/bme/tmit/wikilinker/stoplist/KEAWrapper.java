package hu.bme.tmit.wikilinker.stoplist;

import java.util.HashSet;
import java.util.Set;

import kea.stopwords.Stopwords;

public class KEAWrapper extends Stopwords {

	private static final long serialVersionUID = 1L;
	private Set<String> stopwords;

	@Override
	public boolean isStopword(String arg0) {
		return stopwords.contains(arg0);
	}

	public KEAWrapper() {
		stopwords = new HashSet<>(new StoplistFactory().get());
	}

}
