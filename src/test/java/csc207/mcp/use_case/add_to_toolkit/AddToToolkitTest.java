package csc207.mcp.use_case.add_to_toolkit;

import csc207.mcp.entity.McpServer;
import csc207.mcp.entity.User;
import csc207.mcp.interface_adapter.MarketViewModel;
import csc207.mcp.interface_adapter.Session;
import csc207.mcp.interface_adapter.add_to_toolkit.AddToToolkitController;
import csc207.mcp.interface_adapter.add_to_toolkit.AddToToolkitPresenter;
import csc207.mcp.use_case.login.LoginOutputData;
import java.util.ArrayList;
import java.util.List;

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
        Session session = new Session();
        session.login(new LoginOutputData("u1", "user", "user@example.com"));
        MarketViewModel model = new MarketViewModel();
        List<Runnable> queue = new ArrayList<>();
        Object frontendSelection = new Object();
        AddToToolkitPresenter presenter = new AddToToolkitPresenter(model, queue::add);
        AddToToolkitInteractor interactor = new AddToToolkitInteractor(dao, presenter);
        AddToToolkitController controller = new AddToToolkitController(interactor, session,
                input -> input == frontendSelection ? "m1" : null);
        controller.execute(frontendSelection);
        check(dao.saves == 1 && queue.size() == 1 && !model.getState().isSuccessful(),
                "Object mapping, session, persistence, and UI dispatch are connected");
        queue.remove(0).run();
        MarketViewModel.Result success = model.getState();
        check(success.isSuccessful() && success.getServerId().equals("m1")
                && success.getUserId().equals("u1"), "presenter publishes successful addition");

        controller.execute(frontendSelection);
        queue.remove(0).run();
        check(!model.getState().isSuccessful() && model.getState().getServerId().isEmpty()
                && !model.getState().getMessage().isBlank(), "failure clears stale success data");
        check(success.isSuccessful(), "previous result is immutable");
        controller.execute(null);
        queue.remove(0).run();
        check(!model.getState().isSuccessful() && dao.saves == 1, "empty Object input rejected");

        session.logout();
        int previousLookups = dao.lookups;
        controller.execute(frontendSelection);
        queue.remove(0).run();
        check(!model.getState().isSuccessful() && dao.lookups == previousLookups,
                "controller uses current session on each call");
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
