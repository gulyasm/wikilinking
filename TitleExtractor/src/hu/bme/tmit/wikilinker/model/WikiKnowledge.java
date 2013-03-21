package hu.bme.tmit.wikilinker.model;

import java.util.HashMap;
import java.util.Map;

public class WikiKnowledge {

	private Map<String, Anchor> anchors;

	public WikiKnowledge() {
		anchors = new HashMap<>();
	}

	public void addAnchor(Anchor anchor) {
		anchors.put(anchor.getName(), anchor);
	}
}
