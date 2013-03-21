package hu.bme.tmit.wikilinker.model;

import java.util.ArrayList;
import java.util.List;

public class Title {

	private final String name;
	private String url;
	private List<Category> categories;

	public Title(String name, String url) {
		this.name = name;
		this.url = url;
		categories = new ArrayList<>();
	}

	public void addCategory(Category category) {
		categories.add(category);
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}
}
