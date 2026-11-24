package csc207.mcp.interface_adapter.add_to_toolkit;

import csc207.mcp.interface_adapter.Session;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitInputBoundary;
import csc207.mcp.use_case.add_to_toolkit.AddToToolkitInputData;
import java.util.Objects;
import java.util.function.Function;

/** Converts a frontend selection into use-case input using the current session. */
public final class AddToToolkitController {
    private final AddToToolkitInputBoundary interactor;
    private final Session session;
    private final Function<Object, String> serverIdExtractor;

    /**
     * The frontend supplies its Object-to-MCP-ID mapping once its input type is defined.
     * The extractor should return null for an invalid selection.
     */
    public AddToToolkitController(AddToToolkitInputBoundary interactor, Session session,
                                  Function<Object, String> serverIdExtractor) {
        this.interactor = Objects.requireNonNull(interactor);
        this.session = Objects.requireNonNull(session);
        this.serverIdExtractor = Objects.requireNonNull(serverIdExtractor);
    }

    public void execute(Object input) {
        String serverId = input == null ? null : serverIdExtractor.apply(input);
        interactor.excute(new AddToToolkitInputData(session.getUserId(), serverId));
    }
}
