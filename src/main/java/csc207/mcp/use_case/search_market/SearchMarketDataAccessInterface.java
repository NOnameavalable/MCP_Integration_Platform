package csc207.mcp.use_case.search_market;

import java.util.List;

/** Provides server names and search data; matching belongs to the interactor. */
public interface SearchMarketDataAccessInterface {
    /** Returns all available MCP server names as a non-null list, or an empty list. */
    List<String> findAllServerNames();

    /**
     * Retrieves existing server IDs, names, descriptions, and tool-name lists for the supplied names.
     * Returns a non-null list, or an empty list. Matching belongs to the interactor.
     */
    List<McpServerData> findSearchDataByNames(List<String> serverNames);
}
