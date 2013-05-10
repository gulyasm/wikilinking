package hu.bme.tmit.wikilinker.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class Page {

	private final String name;
	private String url;
	private List<Category> categories;

	public Page(String name, String url) {
		Preconditions.checkNotNull(name);
		categories = new ArrayList<>();
		this.name = name;
		this.url = url;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Page) {
			return Objects.equal(name, ((Page) obj).name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
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

	public List<Category> getCategories() {
		return categories;
	}

	public List<String> getCategoryNames() {
		List<String> catname = new ArrayList<String>();
		for (int i = 0; i < categories.size(); i++)
			catname.add(categories.get(i).getName().toLowerCase().trim());
		return catname;
	}

	@Override
	public String toString() {
		return Objects
				.toStringHelper(Page.class)
				.add("name", name)
				.add("categories", Joiner.on(",").join(categories))
				.toString();
	}
}
