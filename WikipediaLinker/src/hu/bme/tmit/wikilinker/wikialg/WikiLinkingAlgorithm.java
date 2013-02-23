package hu.bme.tmit.wikilinker.wikialg;

import hu.bme.tmit.wikilinker.WikiLink;

import java.util.Collection;
import java.util.List;

public interface WikiLinkingAlgorithm {
	
	public Collection<WikiLink> link(List<String> nouns);

}
