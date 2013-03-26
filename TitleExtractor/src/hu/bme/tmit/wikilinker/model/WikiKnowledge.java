package hu.bme.tmit.wikilinker.model;

import java.util.HashMap;
import java.util.Map;

public class WikiKnowledge {

	private Map<String, Anchor> anchors;
	private Map<String, Page> pages;

	public WikiKnowledge() {
		anchors = new HashMap<>();
		pages = new HashMap<>();
	}

	public void addAnchor(Anchor anchor) {
		anchors.put(anchor.getName(), anchor);
	}

	public void addPage(Page page) {
		pages.put(page.getName(), page);
	}

	public Page getPage(String pageTitle) {
		return pages.get(pageTitle);
	}

	public Anchor getAnchor(String anchorString) {
		return anchors.get(anchorString);
	}
}
