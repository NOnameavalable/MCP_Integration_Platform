package csc207.mcp.use_case.search_market;

import java.util.List;
import java.util.Objects;

/** Immutable search results containing server names, descriptions, and tool names. */
public final class SearchMarketOutputData {
    private final String keyword;
    private final List<McpServerData> results;

    public SearchMarketOutputData(String keyword, List<McpServerData> results) {
        this.keyword = Objects.requireNonNull(keyword);
        this.results = List.copyOf(results);
    }

    public String getKeyword() { return keyword; }
    public List<McpServerData> getResults() { return results; }
}
