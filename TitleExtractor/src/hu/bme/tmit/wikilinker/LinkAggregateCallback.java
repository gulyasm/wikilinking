package hu.bme.tmit.wikilinker;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Joiner;

import edu.jhu.nlp.wikipedia.WikiPage;

public class LinkAggregateCallback extends AbstractPageCallback {

	private List<String> titles = new ArrayList<>();
	
	@Override
	public void process(WikiPage page) {
		super.process(page);
		titles.add(page.getTitle());
		System.out.println(Joiner.on("; ").join(page.getLinks()));
	}
	
	public List<String> getTitles() {
		return titles;
	}

}
