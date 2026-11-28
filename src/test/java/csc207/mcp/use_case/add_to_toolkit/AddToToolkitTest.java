package csc207.mcp.use_case.add_to_toolkit;

import csc207.mcp.entity.McpServer;
import csc207.mcp.entity.User;
import csc207.mcp.interface_adapter.Session;
import csc207.mcp.interface_adapter.add_to_toolkit.AddToToolkitController;
import csc207.mcp.interface_adapter.add_to_toolkit.AddToToolkitPresenter;
import csc207.mcp.interface_adapter.mcpkit.McpkitState;
import csc207.mcp.interface_adapter.mcpkit.McpkitViewModel;
import csc207.mcp.use_case.login.LoginOutputData;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/** Standalone business-flow checks; no database or JavaFX application startup needed. */
public final class AddToToolkitTest {
    private static int checks;

    public static void main(String[] args) {
        addition();
        failures();
        controllerAndPresentation();
        System.out.println("Add-to-toolkit checks passed: " + checks);
    }

    private static void addition() {
        Dao dao = new Dao();
        McpServer existing = new McpServer("Existing", "", "existing");
        dao.user.getMcpkit().addTool(existing);
        User original = dao.user;
        Result result = new Result();
        new AddToToolkitInteractor(dao, result).excute(new AddToToolkitInputData("u1", "m1"));
        check(dao.saves == 1 && result.successes == 1 && result.failures == 0, "saved before success");
        check(dao.user.getMcpkit().contains(existing)
                && dao.user.getMcpkit().contains(dao.server), "preserves existing toolkit entries");
        check(original.getMcpkit().mcpkitGetter().size() == 1, "loaded user remains unchanged");
        check(result.output.getUserId().equals("u1") && result.output.getServerId().equals("m1")
                && result.output.getServerName().equals("Selected"), "output identifies saved addition");
        check(result.output.getMcpkitList().equals(List.of(existing, dao.server)),
                "output carries both existing entries and the saved addition");

        // A database lookup may return a different object for the same MCP ID.
        dao.server = new McpServer("Selected", "", "m1");
        Result duplicate = new Result();
        new AddToToolkitInteractor(dao, duplicate).excute(new AddToToolkitInputData("u1", "m1"));
        check(duplicate.failures == 1 && duplicate.successes == 0 && dao.saves == 1,
                "duplicate ID rejected without saving again");
    }

    private static void failures() {
        for (AddToToolkitInputData input : new AddToToolkitInputData[]{null,
                new AddToToolkitInputData(null, "m1"), new AddToToolkitInputData(" ", "m1"),
                new AddToToolkitInputData("u1", null), new AddToToolkitInputData("u1", " ")}) {
            Dao dao = new Dao();
            Result result = new Result();
            new AddToToolkitInteractor(dao, result).excute(input);
            check(result.failures == 1 && dao.lookups == 0 && dao.saves == 0,
                    "invalid input avoids database access");
        }
        for (int scenario = 0; scenario < 5; scenario++) {
            Dao dao = new Dao();
            User original = dao.user;
            if (scenario == 0) { dao.server = null; }
            if (scenario == 1) { dao.user = null; }
            if (scenario == 2) { dao.failAt = "server"; }
            if (scenario == 3) { dao.failAt = "user"; }
            if (scenario == 4) { dao.failAt = "save"; }
            Result result = new Result();
            new AddToToolkitInteractor(dao, result).excute(new AddToToolkitInputData("u1", "m1"));
            check(result.failures == 1 && result.successes == 0 && result.output == null,
                    "missing data and database errors report only failure");
            check(original.getMcpkit().mcpkitGetter().isEmpty(), "failed request leaves original toolkit intact");
            check(!result.error.contains("internal database details"), "failure uses a frontend message");
            if (scenario != 4) {
                check(dao.saves == 0, "lookup failure does not save");
            }
        }
    }

    private static void controllerAndPresentation() {
        Dao dao = new Dao();
        McpServer existing = new McpServer("Existing", "", "existing");
        dao.user.getMcpkit().addTool(existing);
        Session session = new Session();
        session.login(new LoginOutputData("u1", "user", "user@example.com"));
        McpkitViewModel model = new McpkitViewModel();
        model.setState(new McpkitState("", false, List.of(existing)));
        AtomicInteger notifications = new AtomicInteger();
        model.stateProperty().addListener((observable, previous, current) -> notifications.incrementAndGet());
        List<Runnable> queue = new ArrayList<>();
        Object frontendSelection = new Object();
        AddToToolkitPresenter presenter = new AddToToolkitPresenter(model, queue::add);
        AddToToolkitInteractor interactor = new AddToToolkitInteractor(dao, presenter);
        AddToToolkitController controller = new AddToToolkitController(interactor, session,
                input -> input == frontendSelection ? "m1" : null);
        controller.execute(frontendSelection);
        check(dao.saves == 1 && queue.size() == 1 && !model.getState().getStatus()
                && notifications.get() == 0 && model.getState().getMcpkitList().equals(List.of(existing)),
                "Object mapping, session, persistence, and UI dispatch are connected");
        queue.remove(0).run();
        McpkitState success = model.getState();
        check(success.getStatus() && success.getMcpkitList().equals(List.of(existing, dao.server))
                && !success.getMessage().isBlank(), "presenter publishes complete updated toolkit");
        check(notifications.get() == 1, "successful addition notifies toolkit observers once");

        controller.execute(frontendSelection);
        queue.remove(0).run();
        check(!model.getState().getStatus() && model.getState().getMcpkitList().equals(success.getMcpkitList())
                && !model.getState().getMessage().isBlank(), "failure preserves current toolkit and updates message");
        check(notifications.get() == 2, "failed addition notifies toolkit observers once");
        check(success.getStatus(), "previous result remains unchanged");
        controller.execute(null);
        queue.remove(0).run();
        check(!model.getState().getStatus() && dao.saves == 1, "empty Object input rejected");

        session.logout();
        int previousLookups = dao.lookups;
        controller.execute(frontendSelection);
        queue.remove(0).run();
        check(!model.getState().getStatus() && dao.lookups == previousLookups,
                "controller uses current session on each call");
        check(model.getState().getMcpkitList().equals(success.getMcpkitList()),
                "invalid requests preserve the displayed toolkit");

        // A failure dispatched later should preserve the state present when it is displayed.
        presenter.prepareFailView("Unable to add.");
        McpServer refreshed = new McpServer("Refreshed", "", "refreshed");
        model.setState(new McpkitState("Loaded", true, List.of(refreshed)));
        queue.remove(0).run();
        check(!model.getState().getStatus() && model.getState().getMcpkitList().equals(List.of(refreshed)),
                "queued failure preserves a newer toolkit refresh");
    }

    private static final class Dao implements AddToToolkitDataAccessInterface {
        private User user = new User("u1", "user", "user@example.com", "hash");
        private McpServer server = new McpServer("Selected", "", "m1");
        private int saves;
        private int lookups;
        private String failAt = "";

        public McpServer getMcpById(String id) {
            lookups++;
            failIf("server");
            return server != null && server.getMcpId().equals(id) ? server : null;
        }

        public User getUserById(String id) {
            lookups++;
            failIf("user");
            return user != null && user.getUserId().equals(id) ? user : null;
        }

        public void saveUser(User updatedUser) {
            saves++;
            failIf("save");
            user = updatedUser;
        }

        private void failIf(String operation) {
            if (failAt.equals(operation)) {
                throw new IllegalStateException("internal database details");
            }
        }
    }

    private static final class Result implements AddToToolkitOutputBoundary {
        private int successes;
        private int failures;
        private String error;
        private AddToToolkitOutputData output;

        public void prepareSuccessView(AddToToolkitOutputData data) {
            successes++;
            output = data;
        }

        public void prepareFailView(String errorMessage) {
            failures++;
            error = errorMessage;
        }
    }

    private static void check(boolean condition, String message) {
        if (!condition) { throw new AssertionError(message); }
        checks++;
    }
}
