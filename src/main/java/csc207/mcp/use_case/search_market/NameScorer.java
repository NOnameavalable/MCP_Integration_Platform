package csc207.mcp.use_case.search_market;

/** Scores server names without coupling the use case to a scoring library. */
public interface NameScorer {
    /** Returns a score from 0 to 1; higher scores indicate stronger character matches. */
    double score(String serverName, String keyword);
}
