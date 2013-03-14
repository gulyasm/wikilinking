package edu.jhu.nlp.wikipedia;

public class Wikilink {
	
	public final String anchor;
	public final String link;
	
	public Wikilink(String anchor, String link) {
		super();
		this.anchor = anchor;
		this.link = link;
	}
	
	public Wikilink(String link) {
		super();
		this.anchor = null;
		this.link = link;
	}
	
	public boolean hasAnchor() {
		return anchor != null;
	}
	
	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder("Wikilink[");
		bld.append("link=").append(link);
		if(anchor != null) {
			bld.append(", anchor=").append(anchor);
		}
		bld.append("]");
		return bld.toString();
	}
}
