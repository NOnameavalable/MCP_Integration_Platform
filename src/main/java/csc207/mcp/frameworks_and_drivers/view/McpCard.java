package csc207.mcp.frameworks_and_drivers.view;

import java.util.List;
import java.util.Objects;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/** Compact 3:2 MCP card displayed inside {@link McpCardsPane}. */
public final class McpCard extends VBox {
    private static final double CARD_WIDTH = 300;
    private static final double CARD_HEIGHT = 200;
    private static final int MAX_VISIBLE_TOOLS = 3;
    private static final PseudoClass ADDED_PSEUDO_CLASS = PseudoClass.getPseudoClass("added");

    // TODO: Store the finalized MCP server data model object (with mcpId, description, and full tool details)
    //       so ExpandedMcpCardOverlay can read complete server metadata from this card.
    private final String mcpId;
    private final String name;
    private final String description;
    private final List<String> toolNames;

    private final Button actionButton;
    private boolean added;

    public McpCard(String mcpId, String name, String description, List<String> toolNames) {
        this.mcpId = Objects.requireNonNull(mcpId);
        this.name = Objects.requireNonNull(name);
        this.description = description == null ? "" : description;
        this.toolNames = toolNames == null ? List.of() : List.copyOf(toolNames);

        getStylesheets().add(Objects.requireNonNull(
                McpCard.class.getResource("/styles/mcp-card.css"),
                "Missing /styles/mcp-card.css").toExternalForm());
        getStyleClass().add("mcp-card");
        setPrefSize(CARD_WIDTH, CARD_HEIGHT);
        setMinSize(CARD_WIDTH, CARD_HEIGHT);
        setMaxSize(CARD_WIDTH, CARD_HEIGHT);

        HBox header = buildHeader();
        VBox toolList = buildToolList();
        actionButton = new Button();
        actionButton.getStyleClass().add("mcp-add-button");
        // TODO: Wire actionButton click to AddToToolkit / RemoveFromMcpkit controller callback.
        setAdded(false);

        HBox footer = buildFooter();
        getChildren().addAll(header, toolList, footer);
    }

    private HBox buildHeader() {
        Label titleLabel = new Label(name);
        titleLabel.getStyleClass().add("mcp-card-title");
        titleLabel.setMaxWidth(Double.MAX_VALUE);
        HBox.setHgrow(titleLabel, Priority.ALWAYS);

        Label spaceHint = new Label("Space");
        spaceHint.getStyleClass().add("mcp-card-space-hint");
        spaceHint.setMinWidth(USE_PREF_SIZE);
        // TODO: Show spaceHint smoothly on card hover when preview interaction is wired.

        HBox header = new HBox(8, titleLabel, spaceHint);
        header.setAlignment(Pos.CENTER_LEFT);
        return header;
    }

    private VBox buildToolList() {
        VBox list = new VBox(6);
        list.setAlignment(Pos.TOP_LEFT);
        VBox.setVgrow(list, Priority.ALWAYS);

        int visibleCount = Math.min(toolNames.size(), MAX_VISIBLE_TOOLS);
        for (int i = 0; i < visibleCount; i++) {
            Label chip = new Label(toolNames.get(i));
            chip.getStyleClass().add("mcp-tool-chip");
            chip.setMaxWidth(Double.MAX_VALUE);
            list.getChildren().add(chip);
        }
        return list;
    }

    private HBox buildFooter() {
        int remaining = Math.max(0, toolNames.size() - MAX_VISIBLE_TOOLS);
        String countText = remaining > 0 ? "+" + remaining + " more" : toolNames.size() + " tools";
        Label moreLabel = new Label(countText);
        moreLabel.getStyleClass().add("mcp-card-more-label");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox footer = new HBox(8, moreLabel, spacer, actionButton);
        footer.setAlignment(Pos.CENTER_LEFT);
        return footer;
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getToolNames() {
        return toolNames;
    }
}
