package hu.bme.tmit.dbtest;

import java.io.File;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Random;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;

public class DatabaseTest {

	private static final String DB_PATH = "db/test";
	private static final int RUNS = 1_000_000;
	private static final String TABLE_NAME = "Persons";
	private static final String COLUMN_NAME = "Name";
	private static Random random = new Random();

	public static void main(String[] args) throws SQLiteException {
		System.out.println("Test: Inserting " + RUNS + " string");
		File file = new File(DB_PATH);
		if (file.exists()) {
			file.delete();
		}
		file = null;
		SQLite sqlite = new SQLite(DB_PATH);
		Stopwatch stopwatch = new Stopwatch();
		stopwatch.start();
		sqlite.createTable(TABLE_NAME, COLUMN_NAME + " TEXT");
		logtime("CREATE TABLE", stopwatch);
		stopwatch.reset();
		int selected = random.nextInt(RUNS);
		String selectedString = null;
		stopwatch.start();
		sqlite.beginTransaction();
		for (int i = 0; i < RUNS; i++) {
			String string = new BigInteger(130, random).toString(32);
			if (i == selected) {
				selectedString = string;
			}
			HashMap<String, String> values = Maps.newHashMap();
			values.put(COLUMN_NAME, string);
			sqlite.insert(TABLE_NAME, values);
		}
		sqlite.commitTransaction();

		logtime("INSERT " + RUNS + " ROW ", stopwatch);
		stopwatch.reset();
		stopwatch.start();

		sqlite.createIndex(TABLE_NAME, COLUMN_NAME, "sd");

		logtime("INDEXING " + RUNS + " ROW ", stopwatch);
		stopwatch.reset();
		stopwatch.start();

		SQLiteStatement stmt = sqlite.createStatement("SELECT * FROM Persons WHERE Name ='" + selectedString + "'");
		while (stmt.step()) {
			System.out.println("Selected row: " + stmt.columnString(0));
		}
		logtime("SELECT RANDOM ROW FROM " + RUNS + " ROW ", stopwatch);
		stopwatch.reset();

		stmt.dispose();
		sqlite.dispose();

		System.out.println("----------------------------------------");
	}

	private static void logtime(String message, Stopwatch stopwatch) {
		System.out.println(message + ": " + stopwatch);

	}
}
