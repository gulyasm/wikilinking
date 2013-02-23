package hu.bme.tmit.wikilinker;

import com.google.common.base.Objects;

public class WikiLink {

	private String anchor;
	private String targetTitle;
	private String targetPath;

	public WikiLink(String link, String targetTitle, String targetPath) {
		super();
		this.anchor = link;
		this.targetTitle = targetTitle;
		this.targetPath = targetPath;
	}

	public WikiLink() {
		this(null, null, null);
	}

	public String getAnchor() {
		return anchor;
	}

	public void setAnchor(String anchor) {
		this.anchor = anchor;
	}

	public String getTargetTitle() {
		return targetTitle;
	}

	public void setTargetTitle(String targetTitle) {
		this.targetTitle = targetTitle;
	}

	public String getTargetPath() {
		return targetPath;
	}

	public void setTargetPath(String targetPath) {
		this.targetPath = targetPath;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(getClass()).add("anchor", anchor).add("targetTitle", targetTitle).toString();
	}

}
