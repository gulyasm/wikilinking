package hu.bme.tmit.dbtest;

import java.io.File;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import com.almworks.sqlite4java.SQLiteException;
import com.almworks.sqlite4java.SQLiteStatement;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Maps;

public class DatabaseTest {

	private static final String DB_PATH = "db/test";
	private static final int RUNS = 20_000_000;
	private static final String TABLE_NAME = "Persons";
	private static final String COLUMN_NAME = "Name";
	private static Random random = new Random();
	private static Set<Integer> randomIDs = new HashSet<>();
	private static Set<String> randomStrings = new HashSet<>();

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
		int index = 0;
		while (index++ < 100_000) {
			randomIDs.add(random.nextInt(RUNS));
		}
		index = 0;

		stopwatch.start();
		sqlite.beginTransaction();
		for (int i = 0; i < RUNS; i++) {
			if (randomIDs.contains(i)) {
				randomStrings.add(string);
				System.out.println(string);
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

		for (String s : randomStrings) {
			SQLiteStatement stmt = sqlite.createStatement("SELECT * FROM Persons WHERE Name ='" + s + "'");
			while (stmt.step()) {
				System.out.println("Selected row: " + stmt.columnString(0));
			}
			stmt.dispose();
		}
		logtime("SELECT RANDOM 100_000 ROW FROM " + RUNS + " ROW ", stopwatch);
		stopwatch.reset();

		sqlite.dispose();

		System.out.println("----------------------------------------");
	}

	private static void logtime(String message, Stopwatch stopwatch) {
		System.out.println(message + ": " + stopwatch);

	}
}
