# MCP Integration Platform

CSC207 project scaffold for an MCP integration platform using Clean Architecture.

## Structure

```text
src/
├── main/
│   ├── java/csc207/mcp/
│   │   ├── app/
│   │   ├── entity/
│   │   ├── use_case/
│   │   ├── interface_adapter/
│   │   └── frameworks_and_drivers/
│   └── resources/
│       └── static/
└── test/
    └── java/csc207/mcp/
        ├── entity/
        ├── use_case/
        ├── interface_adapter/
        └── frameworks_and_drivers/
```

- `app/`: application startup and assembly of concrete implementations.
- `entity/`: core domain objects and business rules.
- `use_case/`: application operations and their input, output, and external-access interfaces.
- `interface_adapter/`: controllers, presenters, and response models.
- `frameworks_and_drivers/`: HTTP server, database access, and MCP client implementations.
- `resources/static/`: HTML, CSS, JavaScript, and other web assets.
- `docs/`: architecture documentation and user stories.

Source dependencies point inward. Entities are independent of outer layers;
use cases depend on entities and inner-layer interfaces. Outer implementations
implement those interfaces, and `app/` wires the application together.

## Development

Use JDK 11 or newer and Maven. Java compilation targets Java 11 to match the
current course examples.

```sh
mvn compile
```

This scaffold has no Java classes or runnable application yet. Empty directories
contain `.gitkeep` placeholders so they can be included in Git.
