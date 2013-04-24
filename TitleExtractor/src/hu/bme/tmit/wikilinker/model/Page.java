package hu.bme.tmit.wikilinker.model;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class Page {

	private final String name;
	private String url;
	private List<Category> categories;

	public Page(String name, String url) {
		Preconditions.checkNotNull(name);
		this.name = name;
		this.url = url;
	}

	public Page(String pageTitle) {
		this(pageTitle, null);
	}

	public void addCategory(Category category) {
		categories.add(category);
	}

	public void addCategories(Collection<Category> categories) {
		this.categories.addAll(categories);
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(Page.class).add("name", name).toString();
	}
}
