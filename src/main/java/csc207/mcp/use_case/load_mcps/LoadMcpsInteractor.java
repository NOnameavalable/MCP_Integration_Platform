package csc207.mcp.use_case.load_mcps;

import java.util.Objects;

public final class LoadMcpsInteractor implements LoadMcpsInputBoundary {
    private final LoadMcpsDataAccessInterface dataAccess;
    private final LoadMcpsOutputBoundary presenter;

    public LoadMcpsInteractor(LoadMcpsDataAccessInterface dataAccess, LoadMcpsOutputBoundary presenter) {
        this.dataAccess = Objects.requireNonNull(dataAccess);
        this.presenter = Objects.requireNonNull(presenter);
    }

    @Override
    public void execute() {
        LoadMcpsOutputData outputData;
        try {
            outputData = new LoadMcpsOutputData(dataAccess.loadAllMcps());
        } catch (RuntimeException failure) {
            presenter.prepareFailView("Unable to load marketplace servers. Please try again.");
            return;
        }
        presenter.prepareSuccessView(outputData);
    }
}
