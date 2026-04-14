# Cydric - Project Portfolio Page

## Overview

TradeLog is a Command Line Interface (CLI) application designed for proprietary financial traders who rely heavily on mathematical data to refine their trading systems. It provides a fast, keyboard-centric way to systematically log trades, replacing slow and error-prone spreadsheet entry. The application automatically calculates critical trading metrics such as Risk:Reward (R) ratios, Win Rates, and Expected Value (EV), allowing traders to identify their mathematical edge with precision. TradeLog is built in Java and features an immediate-save, localized file architecture to ensure no data is lost during high-stress trading sessions.

## Summary of Contributions

**Code Contributed:** [Cydriccwx Team Contributions](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=cydriccwx&breakdown=true&sort=groupTitle%20dsc&sortWithin=title&since=2026-02-20T00%3A00%3A00&timeframe=commit&mergegroup=&groupSelect=groupByRepos&checkedFileTypes=docs~functional-code~test-code~other&filteredFileName=)

### Enchancements Implemented to TradeLog
| Category                      | Contribution Details                                                                                                                                                                                                                                                                                                             |
|:------------------------------|:---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Application Architecture**  | Established the foundational project structure by creating and organising all core packages (`tradelog.logic`, `tradelog.model`, `tradelog.storage`, `tradelog.ui`, `tradelog.exception`).                                                                                                                                       |
| **Core Domain Models**        | Designed and built the central `Trade` entity, encapsulating all trade data (ticker, entry, exit, stop loss, strategy). Implemented essential trading logic within the class, including automated Risk:Reward ratio calculations and storage string formatting.                                                                  |
| **Command Logic**             | Implemented core execution features including `AddCommand` for safely storing added trades, `SummaryCommand` for iterating through the logged trades to compute advanced statistics (Win Rate, Average Win/Loss, Expected Value, Total R), and `ExitCommand` for clean application termination.                                  |
| **Parser & Validation**       | Implemented the entire `Parser` component from scratch. Implemented `ArgumentTokeniser` to dynamically extract user prefixes. Implemented **`Parser`** for command routing and **`ParserUtil`** for reusable data parsing and validation helpers (price parsing, ticker formatting, direction validation, stop loss validation). |
| **Defensiveness**             | Added exception handling across `AddCommand` and `SummaryCommand`.                                                                                                                                                                                                                                                               |
| **Standards & QA**            | Followed the team's coding standards across all owned files, and wrote JUnit tests for `AddCommandTest`, `ParserTest`, `ArgumentTokeniserTest`, `SummaryCommandTest`, `ListCommandTest`, `DeleteCommandTest`, `CommandTest`, `TradeTest` and `TradeListTest`.                                                                      |

### Contributions to the UG

1. Authored the Parameter Glossary to help users understand the financial terminology required by the application.

2. Wrote the detailed command references, including expected inputs, constraints, and visual examples for the following commands: `add`, `list`, `edit`, `delete`, `summary`, and `exit`.

### Contributions to the DG

1. Authored the architecture and implementation details for the Parser Component, AddCommand, and SummaryCommand.

2. Designed and integrated UML Sequence Diagrams and Class Diagrams to visually explain the command execution flow and prefix tokenisation process.

### Contributions to Team-Based Tasks:

1. Set up the foundation of the project structure, including the creation of all core packages and classes.

2. Maintained code quality standards across the codebase and wrote comprehensive JUnit tests for `AddCommandTest`, `ParserTest`, `ArgumentTokeniserTest`, `SummaryCommandTest`, `ListCommandTest`, `DeleteCommandTest`, `CommandTest`, `TradeTest` and `TradeListTest`.

3. Took part in final bug-fix and final adjustments to the final version of the app.

### Review/Mentoring Contributions:

1. Reviewed and approved PRs for teammates contributions. Also, provided feedback when needed.

### Contributions Beyond the Project Team:

**Project leadership:** As the project involved financial terms and some math, I helped my teammates understand the core of the project and answered their queries along the way. Also, ensured that the project deadlines were met by initiating group meetings to get the ball rolling.

**Evidence of teamwork:** Helped teammates merge conflicts at times and cooperated with teammates to ensure a smooth sailing project.

---

### Contributions to the User Guide (Extracts)
The following is an extract showcasing my contribution to the command documentation in the User Guide, demonstrating my focus on clear, user-centric instructions.

#### 5.1 Adding a Trade: `add`
Logs a new completed or open trade into your journal. All parameters are required. TradeLog will automatically calculate your Risk:Reward (R) multiple based on your entry, exit, and stop-loss prices.

**Format:** `add t/TICKER d/DATE dir/DIRECTION e/ENTRY x/EXIT s/STOP o/OUTCOME strat/STRATEGY`

**Example:**
`add t/AAPL d/2026-03-18 dir/long e/150 x/165 s/140 o/win strat/Breakout`

**Expected Output:**
```text
Trade successfully added.
--------------------------------------------------------------------------------
Trade Summary:
Ticker: AAPL
Date: 2026-03-18
Direction: Long
Entry: 150
Exit: 165
Stop: 140
Strategy: Breakout

Risk:Reward: +1.50R
--------------------------------------------------------------------------------
Trade successfully added
```

---

### Contributions to the Developer Guide (Extracts)
The following is an extract from my section in the Developer Guide explaining the AddCommand architecture I designed.

#### 2.7 AddCommand

##### Architecture-Level Description

The `AddCommand` is a core state-changing operation responsible for introducing new trades into the TradeLog system. It acts as the primary bridge between the `Parser` component (which supplies the raw user input), the `Model` component (by instantiating new `Trade` objects and updating the in-memory `TradeList`), and the `Ui` component (by showing the resulting trade summary and confirmation message).

To adhere to the principle of Separation of Concerns, the execution of the `add` feature is explicitly split into two distinct phases: an initialization/validation phase, and an execution/mutation phase.

##### Component-Level Description

1. Construction & Validation Phase: When the user inputs an `add` command, the `Parser` creates a new `AddCommand(String arguments)`. The constructor immediately passes the raw string to the `ArgumentTokeniser` to map prefixes to their respective string values. It then utilizes `ParserUtil` to strictly validate the financial logic of the inputs (e.g., ensuring a `long` position does not have a stop-loss higher than the entry price, and checking that all prices are valid positive numbers). If any validation fails during this step, a `TradeLogException` is thrown before the `TradeList` or `Storage` is ever accessed.

2. Execution Phase: Once the `AddCommand` is successfully instantiated with a fully valid `Trade` object held in its internal state, the main loop calls `execute(tradeList, ui, storage)`. The command appends the new trade to the `TradeList` and triggers the `Ui` to display a confirmation message with the formatted trade details. Persistence is handled separately by the application's shutdown flow.

##### Sequence Diagram — Full `add` execution path

![AddCommand sequence diagram](diagrams/add-command-sequence.png)

##### Design Rationale

The alternative considered having the constructor simply store the raw user string, pushing all tokenizing and validation inside `execute()`. This was rejected because it violates the Single Responsibility Principle. It would bloat the `execute()` method with string manipulation, financial logic validation, memory updates, and UI updates all at once, making unit testing significantly more difficult.

