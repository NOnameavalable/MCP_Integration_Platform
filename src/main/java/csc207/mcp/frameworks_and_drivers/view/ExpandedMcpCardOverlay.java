package csc207.mcp.frameworks_and_drivers.view;

import java.util.List;
import java.util.Objects;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Expanded MCP detail card shown as an overlay preview (max 540 x 500). */
public final class ExpandedMcpCardOverlay extends VBox {
    private static final double MAX_CARD_WIDTH = 540;
    private static final double MAX_CARD_HEIGHT = 500;
    private static final PseudoClass ADDED_PSEUDO_CLASS = PseudoClass.getPseudoClass("added");

    // TODO: Store the finalized MCP server data model object (with full Tool details: name, description, parameters)
    //       and the active source McpCard reference for origin-centered expand/collapse animations.
    private String mcpId = "";

    private final Label titleLabel;
    private final Label descriptionLabel;
    private final Label toolsHeadingLabel;
    private final VBox toolsContainer;
    private final Label countLabel;
    private final Button actionButton;
    private boolean added;

    public ExpandedMcpCardOverlay() {
        getStylesheets().addAll(
                Objects.requireNonNull(
                        ExpandedMcpCardOverlay.class.getResource("/styles/mcp-card.css"),
                        "Missing /styles/mcp-card.css").toExternalForm(),
                Objects.requireNonNull(
                        ExpandedMcpCardOverlay.class.getResource("/styles/mcp-cards-scroll.css"),
                        "Missing /styles/mcp-cards-scroll.css").toExternalForm());
        getStyleClass().add("expanded-mcp-card");
        setPrefSize(MAX_CARD_WIDTH, MAX_CARD_HEIGHT);
        setMaxSize(MAX_CARD_WIDTH, MAX_CARD_HEIGHT);

        titleLabel = new Label();
        titleLabel.getStyleClass().add("expanded-mcp-title");

        Label releaseHint = new Label("Release Space to close");
        releaseHint.getStyleClass().add("expanded-mcp-release-hint");
        releaseHint.setMinWidth(USE_PREF_SIZE);

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        HBox header = new HBox(12, titleLabel, headerSpacer, releaseHint);
        header.getStyleClass().add("expanded-mcp-header");
        header.setAlignment(Pos.CENTER_LEFT);

        Label descHeading = new Label("DESCRIPTION");
        descHeading.getStyleClass().add("expanded-mcp-section-label");

        descriptionLabel = new Label();
        descriptionLabel.getStyleClass().add("expanded-mcp-description");
        descriptionLabel.setWrapText(true);

        toolsHeadingLabel = new Label("TOOLS (0)");
        toolsHeadingLabel.getStyleClass().add("expanded-mcp-section-label");

        toolsContainer = new VBox(10);

        VBox bodyContent = new VBox(10, descHeading, descriptionLabel, toolsHeadingLabel, toolsContainer);
        bodyContent.getStyleClass().add("expanded-mcp-body");

        ScrollPane scrollPane = new ScrollPane(bodyContent);
        scrollPane.getStyleClass().add("mcp-cards-scroll-pane");
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        countLabel = new Label("0 tools available");
        countLabel.getStyleClass().add("mcp-card-more-label");

        Region footerSpacer = new Region();
        HBox.setHgrow(footerSpacer, Priority.ALWAYS);

        actionButton = new Button();
        actionButton.getStyleClass().add("mcp-add-button");
        // TODO: Wire actionButton click to toggle toolkit membership and sync with the source McpCard.
        setAdded(false);

        HBox footer = new HBox(12, countLabel, footerSpacer, actionButton);
        footer.getStyleClass().add("expanded-mcp-footer");
        footer.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(header, scrollPane, footer);
    }

    /** Populates basic card data into the overlay without triggering any animations. */
    public void setCardData(String mcpId, String name, String description, List<String> toolNames, boolean added) {
        this.mcpId = Objects.requireNonNull(mcpId);
        titleLabel.setText(Objects.requireNonNull(name));
        descriptionLabel.setText(description == null ? "" : description);

        List<String> tools = toolNames == null ? List.of() : toolNames;
        toolsHeadingLabel.setText("TOOLS (" + tools.size() + ")");
        countLabel.setText(tools.size() + " tools available");

        toolsContainer.getChildren().clear();
        for (String toolName : tools) {
            VBox row = new VBox(5);
            row.getStyleClass().add("expanded-mcp-tool-row");

            Label chip = new Label(toolName);
            chip.getStyleClass().add("mcp-tool-chip");
            row.getChildren().add(chip);

            // TODO: Render tool parameters and description once the finalized tool data model is available.
            toolsContainer.getChildren().add(row);
        }
        setAdded(added);
    }

    /** Updates the button visual between "+ Add" and "✓ Added". */
    public void setAdded(boolean added) {
        this.added = added;
        actionButton.pseudoClassStateChanged(ADDED_PSEUDO_CLASS, added);
        actionButton.setText(added ? "✓ Added" : "+ Add");
    }

    public boolean isAdded() {
        return added;
    }

    public String getMcpId() {
        return mcpId;
    }

    // TODO: Implement attach(Scene, Stage) and registerCard(McpCard) for hover + hold-Space preview trigger.
    // TODO: Implement show(McpCard sourceCard) and hide() with smooth expand/collapse transitions and backdrop.
}
