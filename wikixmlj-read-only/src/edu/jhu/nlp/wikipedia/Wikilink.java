package edu.jhu.nlp.wikipedia;

public class Wikilink {

	public final String anchor;
	public final String link;

	public Wikilink(String anchor, String link) {
		super();
		if (anchor != null) {
			this.anchor = new String(anchor);
		} else {
			this.anchor = null;
		}
		this.link = new String(link);
	}

	public Wikilink(String link) {
		this(null, link);
	}

	public boolean hasAnchor() {
		return anchor != null;
	}

	public String getAnchorOrLink() {
		return anchor != null ? anchor : link;
	}

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder("Wikilink[");
		bld.append("link=").append(link);
		if (anchor != null) {
			bld.append(", anchor=").append(anchor);
		}
		bld.append("]");
		return bld.toString();
	}
}
