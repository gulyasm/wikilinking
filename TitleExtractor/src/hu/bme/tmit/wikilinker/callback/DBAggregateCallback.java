package hu.bme.tmit.wikilinker.callback;

import hu.bme.tmit.wikilinker.db.SQLite;
import hu.bme.tmit.wikilinker.logger.Logger;

import java.io.File;
import java.text.MessageFormat;
import java.util.List;
import java.util.Vector;

import com.almworks.sqlite4java.SQLiteException;
import com.google.common.base.Joiner;

import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.Wikilink;

public class DBAggregateCallback extends AbstractPageCallback {

	private int filteredByThreshold = 0;
	private static final Logger LOGGER = new Logger(DBAggregateCallback.class);
	private static final int THRESHOLD = 50;
	private SQLite db;

	public DBAggregateCallback() throws SQLiteException {
		File file = new File("db/wikidb");
		if (file.exists()) {
			file.delete();
		}
		file = null;
		db = new SQLite("db/wikidb");
		db.createTables();
		db.beginTransaction();
	}

	@Override
	public void onFinished() {
		super.onFinished();
		try {
			db.commitTransaction();
		} catch (SQLiteException e) {
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public void process(WikiPage wikiPage) {
		super.process(wikiPage);
		try {
			if (wikiPage.isRedirect()) {
				return;
			}
			String pageTitle = new String(wikiPage.getTitle().toLowerCase().trim());
			List<String> categories = wikiPage.getCategories();
//			LOGGER.i(MessageFormat.format("Put title: {0}, categories: {1}", pageTitle, Joiner.on(",").join(categories)));
			for (String string : categories) {
				db.putTitle(pageTitle, string.toLowerCase().trim());
			}

			Vector<Wikilink> links = wikiPage.getLinks();
			for (Wikilink wikilink : links) {
				String anchorText = wikilink.getAnchorOrLink();
				if (anchorText.length() > THRESHOLD) {
					LOGGER.d(MessageFormat.format("Filtered anchor: {0}", anchorText));
					filteredByThreshold++;
					continue;
				}
				db.putAnchor(anchorText, wikilink.link);
			}
		} catch (SQLiteException e) {
			e.printStackTrace();
			System.exit(-1);
		}

	}

	public int getFilteredByThreshold() {
		return filteredByThreshold;
	}

}
