package csc207.mcp.use_case.search_market;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/** Matches server names before retrieving search data for the matching servers. */
public final class SearchMarketInteractor implements SearchMarketInputBoundary {
    private final SearchMarketDataAccessInterface dataAccess;
    private final SearchMarketOutputBoundary presenter;
    private final NameScorer nameScorer;

    public SearchMarketInteractor(SearchMarketDataAccessInterface dataAccess,
                                  SearchMarketOutputBoundary presenter, NameScorer nameScorer) {
        this.dataAccess = Objects.requireNonNull(dataAccess);
        this.presenter = Objects.requireNonNull(presenter);
        this.nameScorer = Objects.requireNonNull(nameScorer);
    }

    @Override
    public void excute(SearchMarketInputData data) {
        if (data == null || data.getKeyword() == null) {
            presenter.prepareFailView("Please provide a search keyword.");
            return;
        }

        String keyword = data.getKeyword();
        SearchMarketOutputData outputData;
        try {
            List<String> serverNames = dataAccess.findAllServerNames();
            List<String> matchedNames = searchServerNames(keyword, serverNames);
            List<McpServerData> results = findSearchDataInRankOrder(matchedNames);
            outputData = new SearchMarketOutputData(keyword, results);
        } catch (RuntimeException failure) {
            presenter.prepareFailView("Unable to search marketplace servers. Please try again.");
            return;
        }
        presenter.prepareSuccessView(outputData);
    }

    /** Scores each name once; ties keep the source order and repeated names are requested once. */
    private List<String> searchServerNames(String keyword, List<String> serverNames) {
        Map<String, Double> scores = new LinkedHashMap<>();
        for (String serverName : serverNames) {
            double score = nameScorer.score(serverName, keyword);
            if (score > 0.0) {
                scores.putIfAbsent(serverName, score);
            }
        }
        return scores.entrySet().stream()
                .sorted(Map.Entry.<String, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .toList();
    }

    /** Keeps name-scoring order even if the data-access adapter returns records out of order. */
    private List<McpServerData> findSearchDataInRankOrder(List<String> matchedNames) {
        if (matchedNames.isEmpty()) {
            return List.of();
        }
        Map<String, Integer> ranks = new LinkedHashMap<>();
        for (int index = 0; index < matchedNames.size(); index++) {
            ranks.put(matchedNames.get(index), index);
        }
        return dataAccess.findSearchDataByNames(matchedNames).stream()
                .filter(server -> ranks.containsKey(server.getName()))
                .sorted(Comparator.comparingInt(server -> ranks.get(server.getName())))
                .toList();
    }
}
