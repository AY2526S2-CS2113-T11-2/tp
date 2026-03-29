# Ranga - Project Portfolio Page

## Summary of Contributions
### [v1.0] - Core Functional Implementation(MVP)

| Category                     | Contribution Details                                                                                                                                                                                  |
|:-----------------------------|:------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Application Architecture** | Designed and implemented parts of the main **`TradeLog`** entry point, including the core input loop, command routing via `Parser`, and persistent storage lifecycle (load on startup, save on exit). |
| **Command Logic**            | Implemented **`ListCommand`** to display all logged trades in a structured, numbered format, and **`Command`** as the abstract base class defining the contract for all commands.                     |
| **UI Layer**                 | Designed and built the **`Ui`** class, centralising all user-facing output including welcome/goodbye messages, trade display, error handling, and performance summary formatting.                     |
| **Parser & Validation**      | Implemented **`Parser`** for command routing and **`ParserUtil`** for reusable data parsing and validation helpers (price parsing, ticker formatting, direction validation, stop loss validation).    |
| **Defensiveness**            | Added assertions, logging, and exception handling across `Ui`, `ListCommand`, and `TradeLog` to improve robustness and traceability.                                                                  |
| **Standards & QA**           | Followed the team's coding standards across all owned files, and wrote JUnit tests for `Ui`, `ListCommand`, and `Command`.                                                                            |
| **Testing Infrastructure**   | Set up the regression test framework including `input.txt` and `EXPECTED.TXT` covering all 6 MVP commands.                                                                                            |
| **Documentation**            | Contributed to reviewing the User Guide and Developer Guide by documenting the architecture, command routing flow, and UI design decisions.                                                           |

### [v2.0] - Strategy Workflow Enhancements

| Category                      | Contribution Details                                                                                                                                                                                                                                      |
|:------------------------------|:----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Strategy Shortcut Parsing** | Implemented strategy shortcut expansion in **`ParserUtil`** so shorthand codes such as `BB`, `PB`, and `MTR` are normalized to canonical strategy names during parsing. Wired the flow into **`AddCommand`**, **`EditCommand`**, and **`FilterCommand`**. |
| **Strategy Analytics**        | Implemented **`CompareCommand`** and the **`StrategyStats`** helper to compute per-strategy trade count, win rate, average win, average loss, and EV in a single pass over the trade list.                                                                |
| **UI Enhancements**           | Extended **`Ui`** to show supported strategy shortcuts in the welcome banner and to print grouped strategy comparison output in block format.                                                                                                             |
| **Defensiveness**             | Added feature-level assertions and logging in **`ParserUtil`**, **`CompareCommand`**, **`StrategyStats`**, and **`Ui`** to improve observability and preserve internal assumptions.                                                                       |
| **Testing**                   | Added and updated JUnit coverage for shortcut parsing, command execution, comparison metrics, parser routing, and UI formatting. Updated the text UI regression fixtures to cover both strategy shortcuts and the `compare` command.                      |
| **Documentation**             | Documented the new workflow, reorganised the User Guide feature list, and updated the Developer Guide diagrams for the shortcut and compare features so the implementation remains fully traceable.                                                       |
