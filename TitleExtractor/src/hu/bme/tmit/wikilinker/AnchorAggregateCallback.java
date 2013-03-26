package hu.bme.tmit.wikilinker;

import hu.bme.tmit.wikilinker.logger.Logger;
import hu.bme.tmit.wikilinker.model.Anchor;
import hu.bme.tmit.wikilinker.model.Page;
import hu.bme.tmit.wikilinker.model.WikiKnowledge;

import java.text.MessageFormat;

import edu.jhu.nlp.wikipedia.WikiPage;
import edu.jhu.nlp.wikipedia.Wikilink;

public class AnchorAggregateCallback extends AbstractPageCallback {

	private WikiKnowledge knowledge;
	private int filteredByThreshold = 0;
	private static final Logger LOGGER = new Logger(AnchorAggregateCallback.class);
	private static final int THRESHOLD = 50;

	public AnchorAggregateCallback(WikiKnowledge knowledge) {
		this.knowledge = knowledge;
	}

	@Override
	public void process(WikiPage wikiPage) {
		super.process(wikiPage);
		if (wikiPage.isRedirect()) {
			return;
		}
		String pageTitle = new String(wikiPage.getTitle());
		Page page = getOrCreatePage(pageTitle);
		if (page == null) {
			return;
		}
		// List<String> categories = wikiPage.getCategories();
		// for (String string : categories) {

		// }

		for (Wikilink wikilink : wikiPage.getLinks()) {
			String anchorText = wikilink.getAnchorOrLink();
			if (anchorText.length() > THRESHOLD) {
				LOGGER.d(MessageFormat.format("Filtered anchor: {0}", anchorText));
				filteredByThreshold++;
				continue;
			}
			LOGGER.v(MessageFormat.format("Found anchor: {0}", anchorText));
			Anchor anchor = getOrCreateAnchor(anchorText);
			anchor.addPage(getOrCreatePage(wikilink.link));
		}

	}

	private Anchor getOrCreateAnchor(String anchorText) {
		Anchor anchor = knowledge.getAnchor(anchorText);
		if (anchor == null) {
			anchor = new Anchor(anchorText);
			knowledge.addAnchor(anchor);
			LOGGER.d(MessageFormat.format("Anchor created: {0}", anchorText));
		}
		return anchor;
	}

	private Page getOrCreatePage(String pageTitle) {
		if (pageTitle.length() > THRESHOLD) {
			LOGGER.d(MessageFormat.format("Filtered page: {0}", pageTitle));
			return null;
		}
		Page page = knowledge.getPage(pageTitle);
		if (page == null) {
			page = new Page(pageTitle);
			knowledge.addPage(page);
			LOGGER.d(MessageFormat.format("Page created: {0}", pageTitle));
		}
		return page;
	}

	public WikiKnowledge getKnowledge() {
		return knowledge;
	}

	public int getFilteredByThreshold() {
		return filteredByThreshold;
	}

}
