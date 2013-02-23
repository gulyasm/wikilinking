package hu.bme.tmit.wikilinker.sanitizer;

public class DummySanitizer implements Sanitezer {

	@Override
	public String sanitize(String word) {
		return word;
	}

}
