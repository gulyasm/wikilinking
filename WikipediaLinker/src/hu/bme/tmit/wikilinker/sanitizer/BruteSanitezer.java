package hu.bme.tmit.wikilinker.sanitizer;


public class BruteSanitezer implements Sanitezer {

	@Override
	public String sanitize(String word) {
		word = word.replaceAll("\"", "");
		if (word.startsWith("-")) {
			word = word.substring(1, word.length());
		}
		return word;
	}

}
