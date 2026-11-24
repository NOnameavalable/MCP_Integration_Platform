package csc207.mcp.use_case.load_mcps;

import csc207.mcp.entity.Mcp;
import java.util.List;

public interface LoadMcpsDataAccessInterface {
    List<Mcp> loadAllMcps();
}
