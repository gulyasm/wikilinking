package hu.bme.tmit.wikilinker.callback;

import com.almworks.sqlite4java.SQLiteException;
import com.google.common.base.*;
import com.google.common.collect.Collections2;
import com.google.common.io.Files;
import edu.jhu.nlp.wikipedia.WikiPage;
import hu.bme.tmit.wikilinker.BruteSanitezer;
import hu.bme.tmit.wikilinker.Sanitezer;
import hu.bme.tmit.wikilinker.db.SQLite;
import hu.bme.tmit.wikilinker.logger.Logger;
import hu.bme.tmit.wikilinker.model.Anchor;
import hu.bme.tmit.wikilinker.model.Hit;
import hu.bme.tmit.wikilinker.model.LevenshteinDistance;
import hu.bme.tmit.wikilinker.model.Page;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import java.io.*;
import java.text.MessageFormat;
import java.util.*;

public class LinkerCallback extends AbstractPageCallback {

    private Tokenizer tokenizer;
    private HashSet<String> stopList = new HashSet<>();
    private HashSet<String> filterList = new HashSet<>();
    private static final Logger LOGGER = new Logger(LinkerCallback.class);
    private SQLite db;
    private Predicate<String> predicate;
    private Sanitezer sanitezer;
    private PrintWriter outputStream;

    public LinkerCallback(OutputStream stream) throws IOException, SQLiteException {
        this.outputStream = new PrintWriter(stream);
        TokenizerModel tokenModel;
        try (FileInputStream modelStream = new FileInputStream("resources\\en-token.bin")) {
            tokenModel = new TokenizerModel(modelStream);
        }
        tokenizer = new TokenizerME(tokenModel);
        filterList.addAll(Files.readLines(new File("resources\\custom-filter.txt"), Charsets.UTF_8));
        stopList.addAll(Files.readLines(new File("resources\\english.stop.txt"), Charsets.UTF_8));
        File file = new File("db/wikidb");
        if (!file.exists()) {
            LOGGER.e("No database found. Exiting...");
            System.exit(-1);
            return;
        }
        db = new SQLite("db/wikidb");
        predicate = new LinkerPredicate();
        sanitezer = new BruteSanitezer();
    }

    @Override
    public void process(WikiPage page) {
        super.process(page);
        if (page.isRedirect() || page.isDisambiguationPage() || page.isSpecialPage()) {
            LOGGER.d(MessageFormat.format(
                    "Page ({0}) is a disambiguation page, a special page or a redirect. No processing.",
                    page.getTitle()));
            return;
        }
        LOGGER.d(MessageFormat.format("Processing page: {0}", page.getTitle()));

		/* Tokenize */
        String[] toks;
        toks = tokenizer.tokenize(page.getText());
        List<String> toksTemp = new ArrayList<>();
        List<String> expressions;

		/* Stoplist and Predicate */
        for (String token : toks) {
            if (!stopList.contains(token) && predicate.apply(token)) {
                toksTemp.add(sanitezer.sanitize(token));
            }
        }

        expressions = makeExpressions(toksTemp, 2);
        toks = expressions.toArray(new String[expressions.size()]);

		/* Process the page along tokenSet set */
        for (String token : toks) {
            Anchor anchor = null;
            if (Strings.isNullOrEmpty(token)) {
                continue;
            }
            try {
                anchor = db.getAnchor(token);
            } catch (SQLiteException e) {
                LOGGER.w("SQL Error occured. Reason: " + e.getMessage());
            }
            if (anchor == null) {
                continue;
            }
            Set<Page> titles = anchor.getTitles();
            List<Hit> hits = new ArrayList<>();
            Page querypage = null;
            for (Page title : titles) {
                double sim = 0.0;
                double rld;
                double substr = 0.0;
                Page hitPage;
                if (token.contains(title.getName()) || title.getName().contains(token)) substr += 0.5;
                int ld = LevenshteinDistance.compute(token, title.getName());

                // Reverse Levenshtein Distance
                if (ld != 0) {
                    rld = 1.0 / ld;
                } else {
                    rld = 1.0;
                }

                try {
                    querypage = db.getPage(title.getName().toLowerCase().trim());
                } catch (SQLiteException e) {
                    LOGGER.w("SQL Error occured. Reason: " + e.getMessage());
                }

                if (querypage != null) {
                    sim = similarity(page.getCategories(), querypage.getCategoryNames());
                    hitPage = querypage;
                } else hitPage = new Page(title.getName(), title.getUrl());

                if (hitPage.getName().compareTo(page.getTitle().toLowerCase().trim()) != 0)
                    hits.add(new Hit(hitPage, sim + rld + substr));
            }
            Collections.sort(hits);
            Collections.reverse(hits);

            // Format output: anchor : title1,title2,...
            if (hits.size() > 0) {
                outputStream.print(anchor.getName() + " : ");
                String pages = Joiner.on(", ").join(Collections2.transform(hits, new Function<Hit, String>() {
                    @Override
                    public String apply(Hit hit) {
                        return hit.getPage().getName();
                    }
                }));
                outputStream.print(pages);
            }
        }
    }

    @Override
    public void onFinished() {
        super.onFinished();
        outputStream.close();
    }

    private double similarity(Vector<String> test, List<String> retrieved) {
        int s = test.size();
        if (s == 0) return 0.0;
        double count = 0.0;
        for (String category : test) {
            if (retrieved.contains(category)) count += 1.0;
        }
        return count / (double) s;
    }

    private List<String> makeExpressions(List<String> tokens, int depth) {
        List<String> expressions = new ArrayList<>();
        for (int i = 0; i < tokens.size() + 1 - depth; i++) {
            String temp = tokens.get(i);
            if (!expressions.contains(temp))
                expressions.add(temp);
            for (int j = 1; j < depth; j++) {
                temp = temp.concat(" ").concat(tokens.get(i + j));
                if (!expressions.contains(temp))
                    expressions.add(temp);
            }
        }
        return expressions;
    }

    private class LinkerPredicate implements Predicate<String> {

        @Override
        public boolean apply(String arg0) {
            return !Strings.isNullOrEmpty(arg0) && arg0.length() > 1 && !inFilterList(arg0);
        }

        private boolean inFilterList(String arg0) {
            for (String stopWord : filterList) {
                if (arg0.contains(stopWord)) {
                    return true;
                }
            }
            return false;
        }
    }
}
