package hu.bme.tmit.wikilinker.model;

import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class Anchor {
	private String name;
	private Set<Page> titles;

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Anchor)) return false;
		return ((Anchor) obj).name.equals(name);
	}

	public Anchor(final String name) {
		Preconditions.checkNotNull(name);
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public Set<Page> getTitles(){
		return titles;
	}

	public void addPage(Page page) {
		if (titles == null) {
			titles = new HashSet<>();
		}
		titles.add(page);
	}

	@Override
	public String toString() {
		return Objects
				.toStringHelper(Anchor.class)
				.add("name", name)
				.add("titles", Joiner.on(", ").join(titles))
				.toString();
	}

}
