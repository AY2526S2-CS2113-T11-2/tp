# Cydric - Project Portfolio Page

## Overview

TradeLog is a Command Line Interface (CLI) application designed for proprietary financial traders who rely heavily on mathematical data to refine their trading systems. It provides a fast, keyboard-centric way to systematically log trades, replacing slow and error-prone spreadsheet entry. The application automatically calculates critical trading metricsâ€”such as Risk:Reward (R) ratios, Win Rates, and Expected Value (EV)â€”allowing traders to identify their mathematical edge with precision. TradeLog is built in Java and features an immediate-save, localized file architecture to ensure no data is lost during high-stress trading sessions.

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

#### 5.2 Listing all Trades: `list`
Displays a numbered list of all trades currently stored in your TradeLog. This is useful for reviewing your recent activity and finding the `INDEX` number of a trade you want to edit or delete.

**Format:** `list`

**Expected Output:**

```text
Here are your logged trades:
1. AAPL | 2026-03-18 | Long | E:150 | TP:165 | SL:140 | win | Breakout
1. TSLA | 2026-03-18 | Long | E:150 | TP:165 | SL:140 | win | Breakout
```

#### 5.3 Editing a Trade: `edit`
Updates specific details of a previously logged trade. You must provide the `INDEX` of the trade (which you can find using the `list` command) followed by only the prefixes you wish to change.

**Format**: `edit INDEX [PREFIX/VALUE]...`

**Example:**
You accidentally logged the TSLA exit price as 210, but it was actually 205. You also want to change the stop loss price.
edit 2 x/205 s/205

**Expected Output:**

```text
Trade 2 updated successfully.
--------------------------------------------------------------------------------
Trade Summary:
Ticker: TSLA
Date: 2026-03-19
Direction: Short
Entry: 200.0
Exit: 205.0
Stop: 205.0
Strategy: Trend

Risk:Reward: -1.00R
--------------------------------------------------------------------------------
```

#### 5.4 Deleting a Trade: `delete`
Permanently removes a trade from your log using its `INDEX` number. Use this to clean up accidental duplicate entries or test data.

**Format**: `delete INDEX`

**Example:**
`delete 2`

**Expected Output:**

```text
Trade deleted successfully:
TSLA | 2026-03-19 | Short | Entry: 200.0 | Exit: 205.0 | -0.50R | BE
```

#### 5.5 Viewing Performance Metrics: `summary`
Analyzes your entire `TradeList` and calculates key mathematical metrics to help you understand the performance and edge of your trading system.

**Format**: `summary`

**Expected Output:**

```text
--------------------------------------------------------------------------------
Overall Performance:

Total Trades: 3
Win Rate: 100%
Average Win: 3.83R
Average Loss: 0.00R
Overall EV: +3.83R
Total R: +11.50R
--------------------------------------------------------------------------------
```

### 5.6 Exiting the Program: `exit`
Safely saves your data to the local text file and shuts down TradeLog.

**Format**: `exit`

---

### Contributions to the Developer Guide (Extracts)
The following is an extract from my section in the Developer Guide explaining the parsing architecture I designed.

#### 2.2.2 Parser Component

##### Architecture-Level Description

The parsing architecture is responsible for translating raw user input from the CLI into executable `Command` objects. To maintain separation of concerns and ensure financial data integrity, this logic is decoupled into three distinct utility classes: `Parser` (the command router), `ArgumentTokeniser` (the string extractor), and `ParserUtil` (the domain validator).

It takes the raw string, identifies the user's intent, extracts the variable data, enforces strict mathematical trading rules, and ultimately outputs a safe, validated command ready for execution.

##### Component-Level Description

```
Â«utilityÂ»
Parser
  |
  |-- parseCommand(userInput)
        |
        |-- routes to specific Command constructor
        \-- throws TradeLogException on unknown command

Â«utilityÂ»
ArgumentTokeniser
  |
  \-- tokenise(userInput, prefixes)
        |
        \-- returns HashMap<String, String> of prefix-value pairs

Â«utilityÂ»
ParserUtil
  |
  |-- parsePrice(priceString, fieldName)
  |-- parseTicker(ticker)
  |-- parseDirection(direction)
  |-- parseStrategy(strategy)
  |-- validatePrices(entryPrice, stopLossPrice)
  \-- validateStopLoss(direction, entryPrice, stopLossPrice)
```

The general parsing sequence for a complex command follows these steps:

1. `Parser` intercepts the raw string, splitting it into a `commandWord` and an `arguments` string.

2. A `switch` expression routes the `arguments` string to the corresponding `Command` constructor (e.g., `AddCommand`).

3. The command constructor delegates the `arguments` string to `ArgumentTokeniser`, which extracts a map of prefix-value pairs (e.g., `t/` -> `AAPL`).

4. The command extracts the mapped values and passes them to `ParserUtil` to enforce type safety (e.g., converting a string to a `double`) and trading logic (e.g., verifying stop-loss validity).

5. Once fully validated, the fully instantiated `Command` object is returned to the logic manager.

![Parser Sequence Diagram](diagrams/parser-sequence.png)

##### Design Rationale

**Centralisation of Validation:** An alternative considered was placing validation logic directly inside the respective `Command` classes (e.g., hardcoding the stop-loss verification inside `AddCommand`). This was rejected because it would lead to heavy code duplication across other commands that mutate trade states, such as `EditCommand`. Centralising this in `ParserUtil` keeps the commands as thin orchestrators and ensures mathematical trading rules are uniformly applied.

**Tokenisation Data Structure:** `ArgumentTokeniser` returns a `HashMap<String, String>` rather than a `List` of pairs. This was chosen to provide O(1) lookup time when commands need to retrieve their required parameters by prefix key, improving code readability and performance during high-speed data entry.
