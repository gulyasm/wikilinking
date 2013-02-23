package hu.bme.tmit.wikilinker.stoplist;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.google.common.base.Charsets;
import com.google.common.base.Supplier;
import com.google.common.io.Files;

public class StoplistFactory implements Supplier<List<String>> {

	@Override
	public List<String> get() {
		try {
			return Files.readLines(new File("resources\\english.stop.txt"), Charsets.UTF_8);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
