package hu.bme.tmit.dbtest;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.google.common.base.Joiner;

public class SQLite {

	private SQLiteConnection conn;

	public SQLite(String path) throws SQLiteException {
		conn = new SQLiteConnection(new File(path));
		conn.open(true);
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
		String statement = MessageFormat.format("INSERT OR REPLACE INTO {0}({1}) VALUES(''{2}'')", table, Joiner
				.on(", ")
				.join(values.keySet()), Joiner.on(", ").join(values.values()));
		conn.exec(statement);
	}

	public void createTable(String name, String... columns) throws SQLiteException {
		String statement = MessageFormat.format(
				"CREATE TABLE IF NOT EXISTS {0} ({1})",
				name,
				Joiner.on(", ").join(columns));
		conn.exec(statement);
	}

	public void createIndex(String table, String column, String indexName) throws SQLiteException {
		conn.exec(MessageFormat.format("CREATE INDEX IF NOT EXISTS {0} ON {1}({2})", indexName, table, column));
	}

	public void dispose() {
		if (conn != null) {
			conn.dispose();
		}
	}
}
