# View class inheritance

Each branch connects a superclass to a direct subclass. Types marked JavaFX come from the framework. All other classes are in this package.

```text
Application (JavaFX)
└── Main

Region (JavaFX)
├── HeaderBar (JavaFX)
│   └── Header (abstract)
│       └── PlatformHeader
└── Pane (JavaFX)
    ├── HBox (JavaFX)
    │   └── SearchBar
    ├── StackPane (JavaFX)
    │   └── ContentView (abstract)
    │       ├── AccountContent
    │       ├── HomeContent
    │       └── ToolkitContent
    ├── VBox (JavaFX)
    │   ├── McpCardsPane
    │   └── Sidebar (abstract)
    │       ├── FixedSidebar
    │       └── ToggleableSidebar (abstract)
    │           └── ExpandableSidebar
    └── OverlayLayer
```
