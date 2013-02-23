package hu.bme.tmit.wikilinker.linking;

import hu.bme.tmit.wikilinker.WikiLink;

import java.io.File;
import java.util.Collection;

public interface WikiLinking {

	public Collection<WikiLink> link(File file);

	public void setDebug(boolean debug);

}
