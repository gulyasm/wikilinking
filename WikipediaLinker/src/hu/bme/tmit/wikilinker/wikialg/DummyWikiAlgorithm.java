package hu.bme.tmit.wikilinker.wikialg;

import hu.bme.tmit.wikilinker.WikiLink;

import java.util.Collection;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;

public class DummyWikiAlgorithm implements WikiLinkingAlgorithm {

	@Override
	public Collection<WikiLink> link(List<String> nouns) {
		return Collections2.transform(nouns, new Function<String, WikiLink>() {

			@Override
			public WikiLink apply(String arg0) {
				return new WikiLink(arg0, "http:\\\\" + arg0, arg0);
			}
		});
	}

}
