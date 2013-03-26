package hu.bme.tmit.wikilinker.model;

import com.google.common.base.Function;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class Category {

	private final String name;

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Category)) return false;
		return ((Category) obj).name.equalsIgnoreCase(name);
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	public Category(String name) {
		Preconditions.checkNotNull(name);
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return Objects.toStringHelper(Category.class).add("name", name).toString();
	}

	public static final class CreateFunction implements Function<String, Category> {

		@Override
		public Category apply(String name) {
			return new Category(name);
		}

	}

}
