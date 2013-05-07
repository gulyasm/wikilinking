package hu.bme.tmit.wikilinker.db;

import hu.bme.tmit.wikilinker.model.Anchor;
import hu.bme.tmit.wikilinker.model.Category;
import hu.bme.tmit.wikilinker.model.Page;

import java.io.File;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

public class SQLite {

	private SQLiteConnection conn;
	private static final String QUERY_ANCHOR_SELECT = "SELECT * FROM " + AnchorsTable.TABLE_NAME + " WHERE "
			+ AnchorsTable.FIELD_ANCHOR + " = ?";
	private static final String QUERY_TITLE_SELECT = "SELECT * FROM " + TitlesTable.TABLE_NAME + " WHERE "
			+ TitlesTable.FIELD_TITLE + " = ?";
	private SQLiteStatement anchorStatement = null;
	private SQLiteStatement titleStatement = null;
	private HashMap<String, String> tempMap = new HashMap<>();
	private static final ApostrhopyFunction aposFunc = new ApostrhopyFunction();

	public SQLite(String path) throws SQLiteException {
		conn = new SQLiteConnection(new File(path));
		conn.open(true);
	}

	private void ensureAnchorStatement() throws SQLiteException {
		anchorStatement = conn.prepare(QUERY_ANCHOR_SELECT);
	}

	private void ensureTitleStatement() throws SQLiteException {
		titleStatement = conn.prepare(QUERY_TITLE_SELECT);
	}

	public void beginTransaction() throws SQLiteException {
		conn.exec("BEGIN TRANSACTION");
	}

	public SQLiteStatement createStatement(String query) throws SQLiteException {
		return conn.prepare(query);
	}

	public void exec(String query) throws SQLiteException {
		conn.exec(query);
	}

	public void commitTransaction() throws SQLiteException {
		conn.exec("COMMIT TRANSACTION");
	}

	public void delete(String table) throws SQLiteException {
		conn.exec("DELETE FROM " + table);
	}

	public void insert(String table, Map<String, String> values) throws SQLiteException {
		String statement = MessageFormat.format("INSERT OR REPLACE INTO {0}({1}) VALUES({2})", table, Joiner
				.on(", ")
				.join(values.keySet()), Joiner.on(", ").join(Collections2.transform(values.values(), aposFunc)));
		try {
			conn.exec(statement);
		} catch (Exception e) {
			System.out.println(statement);
		}
	}

	public void createTables() throws SQLiteException {
		createTable(
				AnchorsTable.TABLE_NAME,
				AnchorsTable.FIELD_ANCHOR + ", " + AnchorsTable.FIELD_TITLE,
				AnchorsTable.FIELD_ANCHOR + " " + AnchorsTable.TYPE_ANCHOR,
				AnchorsTable.FIELD_TITLE + " " + AnchorsTable.TYPE_TITLE);
		createTable(
				TitlesTable.TABLE_NAME,
				TitlesTable.FIELD_TITLE + ", " + TitlesTable.FIELD_CATEGORY,
				TitlesTable.FIELD_TITLE + " " + TitlesTable.TYPE_TITLE,
				TitlesTable.FIELD_CATEGORY + " " + TitlesTable.TYPE_CATEGORY);
	}

	public void createTable(String name, String primarykeys, String... columns) throws SQLiteException {
		String statement = MessageFormat.format(
				"CREATE TABLE IF NOT EXISTS {0} ({1}, PRIMARY KEY ({2}) ON CONFLICT IGNORE)",
				name,
				Joiner.on(", ").join(columns),
				primarykeys);
		conn.exec(statement);
	}

	public void createIndex(String table, String column, String indexName) throws SQLiteException {
		conn.exec(MessageFormat.format("CREATE INDEX IF NOT EXISTS {0} ON {1}({2})", indexName, table, column));
	}

	public void createIndexes() throws SQLiteException {
		createIndex(AnchorsTable.TABLE_NAME, AnchorsTable.FIELD_ANCHOR, AnchorsTable.INDEX_ANCHOR);
		createIndex(TitlesTable.TABLE_NAME, TitlesTable.FIELD_TITLE, TitlesTable.INDEX_TITLE);
	}

	public void dispose() {
		if (conn != null) {
			conn.dispose();
		}
	}

	public void putTitle(String title, String category) throws SQLiteException {
		tempMap.put(TitlesTable.FIELD_TITLE, title.toLowerCase().trim());
		tempMap.put(TitlesTable.FIELD_CATEGORY, category.toLowerCase().trim());
		insert(TitlesTable.TABLE_NAME, tempMap);
		tempMap.clear();
	}

	public void putAnchor(String anchor, String target) throws SQLiteException {
		tempMap.put(AnchorsTable.FIELD_ANCHOR, anchor);
		tempMap.put(AnchorsTable.FIELD_TITLE, target);
		insert(AnchorsTable.TABLE_NAME, tempMap);
		tempMap.clear();
	}

	public Anchor getAnchor(String anchor) throws SQLiteException {
		ensureAnchorStatement();
		try {
			Anchor result = null;
			anchorStatement.bind(1, anchor);
			while (anchorStatement.step()) {
				if (result == null) {
					result = new Anchor(anchor);
				}
				result.addPage(new Page(anchorStatement.columnString(1)));
			}
			return result;
		} finally {
			anchorStatement.reset();
		}
	}

	public Page getPage(String title) throws SQLiteException {
		ensureTitleStatement();
		try {
			Page result = null;
			titleStatement.bind(1, title);
			while (titleStatement.step()) {
				if (result == null) {
					result = new Page(title);
				}
				result.addCategory(new Category(titleStatement.columnString(1)));
			}
			return result;
		} finally {
			titleStatement.reset();
		}
	}

	private static final class ApostrhopyFunction implements Function<String, String> {

		private final StringBuilder b = new StringBuilder();

		@Override
		public String apply(String arg0) {
			try {
				return b.append("'").append(arg0.replace("'", "''")).append("'").toString();
			} finally {
				b.setLength(0);
			}
		}

	}
}
