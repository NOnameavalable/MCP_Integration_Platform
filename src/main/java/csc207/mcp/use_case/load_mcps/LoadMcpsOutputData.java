package csc207.mcp.use_case.load_mcps;

import csc207.mcp.entity.Mcp;
import java.util.List;

public final class LoadMcpsOutputData {
    private final List<Mcp> mcps;

    public LoadMcpsOutputData(List<Mcp> mcps) {
        this.mcps = List.copyOf(mcps);
    }

    public List<Mcp> getMcps() {
        return mcps;
    }
}
