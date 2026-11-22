package csc207.mcp.frameworks_and_drivers.search;

import csc207.mcp.use_case.search_market.NameScorer;
import java.text.Normalizer;
import java.util.Locale;
import org.apache.commons.text.similarity.FuzzyScore;

/** Normalizes names and exposes Commons Text character matching as a score from 0 to 1. */
public final class CommonsTextNameScorer implements NameScorer {
    private final FuzzyScore fuzzyScore = new FuzzyScore(Locale.ROOT);

    @Override
    public double score(String serverName, String keyword) {
        if (serverName == null || keyword == null) {
            return 0.0;
        }

        String name = normalize(serverName);
        String query = normalize(keyword);
        if (name.isEmpty() || query.isEmpty()) {
            return 0.0;
        }

        int actualScore = fuzzyScore.fuzzyScore(name, query);
        int maximumScore = fuzzyScore.fuzzyScore(query, query);
        if (maximumScore <= 0) {
            return 0.0;
        }
        return (double) actualScore / maximumScore;
    }

    private static String normalize(String text) {
        return Normalizer.normalize(text, Normalizer.Form.NFKC)
                .strip()
                .toLowerCase(Locale.ROOT);
    }
}
