package hu.bme.tmit.wikilinker.model;

import com.google.common.base.Joiner;
import com.google.common.base.Objects;

public class Hit implements Comparable<Hit>{
	private Page page;
	private double rank;
	
	public Hit(Page page, double similarity){
		this.page = page;
		this.rank = similarity;
	}

	@Override
	public int compareTo(Hit h) {
		if(this.rank < h.rank) return -1;
		if(this.rank > h.rank) return 1;
		return 0;
	}

	@Override
	public String toString() {
		return Objects
				.toStringHelper(Hit.class)
				.add("page", page)
				.add("rank", rank)
				.toString();
	}

	public String getOutputFormat() {
		return toString();
	}
}
