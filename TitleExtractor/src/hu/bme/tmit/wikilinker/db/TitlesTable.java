package hu.bme.tmit.wikilinker.db;

public final class TitlesTable {

	public static final String TABLE_NAME = "Titles";

	public static final String FIELD_CATEGORY = "Category";
	public static final String FIELD_TITLE = "Title";

	public static final String TYPE_CATEGORY = "TEXT";
	public static final String TYPE_TITLE = "TEXT";

	public static final String INDEX_TITLE = "IndexTitlesTitle";

	private TitlesTable() {
	}

}
