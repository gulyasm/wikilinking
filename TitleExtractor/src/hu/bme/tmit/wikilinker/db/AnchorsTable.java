package hu.bme.tmit.wikilinker.db;

public final class AnchorsTable {

	public static final String TABLE_NAME = "Anchors";

	public static final String FIELD_ANCHOR = "Anchor";
	public static final String FIELD_TITLE = "Title";

	public static final String TYPE_ANCHOR = "TEXT";
	public static final String TYPE_TITLE = "TEXT";

	public static final String INDEX_ANCHOR = "IndexAnchorAnchor";

	private AnchorsTable() {
	}

}
