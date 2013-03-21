package hu.bme.tmit.wikilinker.model;

import java.util.HashSet;
import java.util.Set;

public class Anchor {
	private String name;
	private Set<Title> titles;

	public Anchor(final String name) {
		this.name = name;
		titles = new HashSet<>();
	}

	public String getName() {
		return name;
	}

	public void addAnchor(Title title) {
		titles.add(title);
	}

}
