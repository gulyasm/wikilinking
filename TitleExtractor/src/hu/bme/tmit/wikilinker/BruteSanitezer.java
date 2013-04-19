package hu.bme.tmit.wikilinker;

import java.util.regex.Pattern;

public class BruteSanitezer implements Sanitezer {

	@Override
	public String sanitize(String word) {
		word = word.replaceAll("\"", "");
		word = word.replaceAll("]", "");
		word = word.replaceAll(Pattern.quote("["), "");
		word = word.replaceAll("-", "");
		return word;
	}

}
