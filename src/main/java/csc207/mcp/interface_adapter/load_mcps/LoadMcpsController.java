package csc207.mcp.interface_adapter.load_mcps;

import csc207.mcp.use_case.load_mcps.LoadMcpsInputBoundary;
import java.util.Objects;

/** Triggers loading all marketplace MCP servers. */
public final class LoadMcpsController {
    private final LoadMcpsInputBoundary loadMcpsInteractor;

    public LoadMcpsController(LoadMcpsInputBoundary loadMcpsInteractor) {
        this.loadMcpsInteractor = Objects.requireNonNull(loadMcpsInteractor);
    }

    public void execute() {
        loadMcpsInteractor.execute();
    }
}
