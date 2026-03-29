# Developer Guide: TradeLog

## 1. Acknowledgements

* **Java Standard Library**: Used for core logic, collections, and I/O operations.
* **Checkstyle**: Enforcement of coding standards (Google Style).
* **Gradle**: Build automation and dependency management.
* **SE-EDU AddressBook-Level 3**: Structural inspiration for CLI parsing and architectural patterns. [Source](https://se-education.org/addressbook-level3/)

---

## 2. Design & Implementation

### 2.1 Architecture Overview

TradeLog follows a modular CLI architecture, separating concerns into four main components:

* **Logic**: Handles prefix-based command parsing (e.g., `t/`, `d/`) and execution flow for commands.
* **Model**: Encapsulates the `Trade` entity and the `TradeList` collection, handling in-memory data representation and ROI calculations.
* **Storage**: Implements an **immediate-save mechanism**. After every successful state-changing command (add, edit, delete), the data is persisted to `tradelog.txt`.
* **UI**: Manages formatted console output and user interaction.

The diagram below shows the high-level flow of a user command through the system:

```
User Input
    │
    ▼
TradeLog (main loop)
    │
    ├──► Parser ──► Command (AddCommand / EditCommand / ...)
    │                   │
    │                   ▼
    │              TradeList (Model)
    │                   │
    │                   ▼
    │              Storage (persist)
    │                   │
    └──► Ui (output to console)
```

---

### 2.2 Design & Implementation

---

#### 2.2.1 UI Component

##### Architecture-Level Description

The `Ui` class is TradeLog's sole output layer. All console interaction — welcome banners, trade displays, error messages, and performance summaries — is centralised here. No other class calls `System.out` directly. This single-responsibility design means that if the output format ever needs to change (e.g., migrating from CLI to a GUI), only `Ui` needs to be modified.

The `Ui` class depends on the `TradeList` and `Trade` model classes for display purposes but has no dependency on `Storage`, `Parser`, or any `Command`. This keeps coupling low and makes the class independently testable.

##### Component-Level Description

`Ui` exposes the following categories of methods:

| Method Category    | Examples                                                          | Purpose                            |
|--------------------|-------------------------------------------------------------------|------------------------------------|
| Lifecycle messages | `showWelcome()`, `showGoodbye()`                                  | Displayed on startup and exit      |
| Trade display      | `printTradeList(TradeList)`, `printTrade(Trade)`                  | Format and print trade data        |
| Feedback messages  | `showTradeAdded()`, `showTradeDeleted()`, `showTradeUpdated(int)` | Confirm successful operations      |
| Summary display    | `showSummary(...)`, `showSummaryEmpty()`                          | Render performance metrics         |
| Error display      | `showError(String)`                                               | Wrap all errors in a divider block |

All output is framed with a fixed 80-character divider line (`DIVIDER`) produced by `"-".repeat(80)`. This gives the CLI a consistent visual structure and separates logical output blocks for readability.

Logging is embedded at the `INFO` level for successful operations and `WARNING` level for errors, using `java.util.logging.Logger`. This means that all UI interactions are traceable in the log output without polluting the console.

##### Sequence Diagram — `list` command triggering `printTradeList`

```
User          TradeLog        Parser        ListCommand        Ui
 │                │               │               │            │
 │─── "list" ────►│               │               │            │
 │                │──parseCommand►│               │            │
 │                │◄──ListCommand─│               │            │
 │                │──────────────execute──────────►│            │
 │                │               │               │─printTradeList(tradeList)──►│
 │                │               │               │            │── prints each trade
 │                │               │               │◄───────────│
 │                │◄──────────────│───────────────│            │
```

##### Design Rationale

The alternative considered was to have each `Command` class print directly to `System.out`. This was rejected because:

1. It would scatter output logic across many classes, making visual consistency hard to enforce.
2. Unit testing would require capturing `System.out` in every command test rather than in one place.
3. Changing the output format (e.g., adding colour codes, or redirecting to a file) would require modifying every command.

Centralising in `Ui` means tests can use a `MockUi` subclass (as seen in `DeleteCommandTest` and `SummaryCommandTest`) to intercept output and assert on values without any `System.out` redirection overhead.

---

#### 2.2.2 ListCommand

##### Architecture-Level Description

`ListCommand` is one of the six core commands in v1.0. It is the simplest non-trivial command: it takes no arguments, performs no mutation of state, and delegates entirely to `Ui` for output. Its role is to bridge the user's request to view all trades with the display logic in `Ui`.

It extends `Command`, the abstract base class that defines the `execute(TradeList, Ui, Storage)` contract. Because `ListCommand` does not exit the application, it inherits the default `isExit()` return value of `false`.

##### Component-Level Description

```
«abstract»
Command
    │
    └── ListCommand
            │
            └── execute(tradeList, ui, storage)
                    │
                    └── ui.printTradeList(tradeList)
```

The `execute` method:

1. Asserts that `tradeList` and `ui` are non-null (defensive programming).
2. Logs the trade count at `INFO` level before delegation.
3. Calls `ui.printTradeList(tradeList)`, which handles both the empty-list case and the populated-list case.
4. Logs successful completion.

The `storage` parameter is accepted by the method signature (to satisfy the `Command` contract) but is deliberately unused, as listing trades requires no persistence interaction.

##### Sequence Diagram — Full `list` execution path

```
TradeLog        ListCommand           Ui               TradeList
    │                │                 │                   │
    │──execute(...)──►│                 │                   │
    │                │──printTradeList──►│                   │
    │                │                 │──size()────────────►│
    │                │                 │◄── int ────────────│
    │                │                 │ [if empty]         │
    │                │                 │── println("No trades logged yet.")
    │                │                 │ [else]             │
    │                │                 │  loop i=0..size-1  │
    │                │                 │──getTrade(i)───────►│
    │                │                 │◄── Trade ──────────│
    │                │                 │── println(trade)   │
    │                │◄────────────────│                    │
    │◄───────────────│                 │                    │
```

##### Design Rationale

An alternative considered was to have `ListCommand` access `TradeList` directly and format the output itself. This was rejected for the same centralisation reason described in the `Ui` section: it would duplicate formatting logic and make the output inconsistent with other commands. The current design keeps `ListCommand` as a thin orchestrator — it knows *when* to display trades, but not *how*.

---

#### 2.2.3 AddCommand

##### Architecture-Level Description

The `AddCommand` is a core state-changing operation responsible for introducing new trades into the TradeLog system. It acts as the primary bridge between the `Parser` component (which supplies the raw user input), the `Model` component (by instantiating new `Trade` objects and updating the in-memory `TradeList`), and the `Storage` component (triggering the immediate-save mechanism to persist the new data).

To adhere to the principle of Separation of Concerns, the execution of the `add` feature is explicitly split into two distinct phases: an initialization/validation phase, and an execution/mutation phase.

##### Component-Level Description

1. Construction & Validation Phase: When the user inputs an `add` command, the `Parser` creates a new `AddCommand(String arguments)`. The constructor immediately passes the raw string to the `ArgumentTokeniser` to map prefixes to their respective string values. It then utilizes `ParserUtil` to strictly validate the financial logic of the inputs (e.g., ensuring a `long` position does not have a stop-loss higher than the entry price, and checking that all prices are valid positive numbers). If any validation fails during this step, a `TradeLogException` is thrown before the `TradeList` or `Storage` is ever accessed.

2. Execution Phase: Once the `AddCommand` is successfully instantiated with a fully valid `Trade` object held in its internal state, the main loop calls `execute(tradeList, ui, storage)`. The command appends the new trade to the `TradeList`, triggers the `Ui` to display a confirmation message with the formatted trade details, and implicitly relies on the main loop's architecture to save the newly updated state to the text file.

```
User        TradeLog         Parser        AddCommand         Trade        TradeList        Ui
│             │               │               │                │              │            │
│─"add t/.."─►│               │               │                │              │            │
│             │─parseCommand─►│               │                │              │            │
│             │               │─new AddCmd()─►│                │              │            │
│             │               │               │──new Trade()──►│              │            │
│             │               │               │◄──Trade────────│              │            │
│             │               │◄──AddCommand──│                │              │            │
│             │◄──AddCommand──│               │                │              │            │
│             │               │               │                │              │            │
│             │────────────execute(tradeList, ui, storage)────►│              │            │
│             │               │               │                │──addTrade(t)►│            │
│             │               │               │                │◄─────────────│            │
│             │               │               │                │──printTrade─►│            │
│             │               │               │                │◄─────────────│            │
│             │               │               │                │──showAdded()►│            │
│             │               │               │                │◄─────────────│            │
│             │◄──────────────────────────────│                │              │            │
```
##### Design Rationale

The alternative considered having the constructor simply store the raw user string, pushing all tokenizing and validation inside `execute()`. This was rejected because it violates the Single Responsibility Principle. It would bloat the `execute()` method with string manipulation, financial logic validation, memory updates, and UI updates all at once, making unit testing significantly more difficult.

#### 2.2.4 DeleteCommand

##### Architecture-Level Description

The `DeleteCommand` is a core state-changing operation responsible for removing existing trades from the TradeLog system. It acts as the bridge between the `Parser` component (which supplies the raw user input), the `Model` component (by locating and deleting the specified `Trade` object from the in-memory `TradeList`), and the `Ui` component (by displaying either a successful deletion message or an error message if the index is invalid at runtime).

To adhere to the principle of Separation of Concerns, the execution of the `delete` feature is explicitly split into two distinct phases: an initialization/validation phase, and an execution/mutation phase.

##### Component-Level Description

1. Construction & Validation Phase: When the user inputs a `delete` command, the `Parser` creates a new `DeleteCommand(String arguments)`. The constructor immediately trims the raw argument string and validates that it is neither missing nor blank. It then attempts to parse the argument into an integer index. If the input is not a valid integer, or if the parsed value is less than or equal to zero, a `TradeLogException` is thrown before the `TradeList` or `Ui` is ever accessed.


2. Execution Phase: Once the `DeleteCommand` is successfully instantiated with a valid positive index stored in its internal state, the main loop calls `execute(tradeList, ui, storage)`. The command converts the user-facing 1-based index into the system’s internal 0-based index and attempts to remove the corresponding `Trade` from the `TradeList`. If the deletion succeeds, the deleted trade is printed and a confirmation message is shown through the `Ui`. If the index is out of bounds, the command catches the resulting `IndexOutOfBoundsException` and displays an error message instead. As with other state-changing commands, persistence is handled by the main loop architecture after successful execution.

```
User        TradeLog         Parser       DeleteCommand       TradeList        Trade         Ui
│             │               │               │                 │              │            │
│─"delete 2"─►│               │               │                 │              │            │
│             │─parseCommand─►│               │                 │              │            │
│             │               │─new DeleteCmd(arguments)───────►│              │            │
│             │               │               │                 │              │            │
│             │               │◄──────DeleteCommand─────────────│              │            │
│             │◄──────────────│               │                 │              │            │
│             │               │               │                 │              │            │
│             │────────────execute(tradeList, ui, storage)─────►│              │            │
│             │               │               │──deleteTrade()─►│              │            │
│             │               │               │◄──deletedTrade──│              │            │
│             │               │               │──printTrade(deletedTrade)─────►│            │
│             │               │               │◄───────────────────────────────│            │
│             │               │               │──showTradeDeleted()───────────►│            │
│             │               │               │◄───────────────────────────────│            │
│             │◄──────────────────────────────│                 │              │            │
```

##### Design Rationale

The alternative considered was having the constructor simply store the raw user string and defer all parsing and validation to `execute()`. This was rejected because it would violate the Single Responsibility Principle. It would cause the `execute()` method to handle input sanitization, integer parsing, validity checks, model mutation, and UI interaction all in one place, making the command less modular and harder to test.

By validating the index during construction, the implementation ensures that only logically valid `DeleteCommand` objects can be created. This makes runtime execution simpler and more focused on state mutation and user feedback. In addition, handling out-of-bounds indices during execution is appropriate because whether an index exists depends on the current state of the `TradeList`, which is only known at runtime.

#### 2.2.5 Testing Strategy for `Ui` and `ListCommand`

Both `Ui` and `ListCommand` are tested using a `captureOutput` helper that temporarily redirects `System.out` to a `ByteArrayOutputStream`. This pattern avoids any dependency on mocking frameworks and works natively with JUnit 5.

The three `UiTest` cases cover:
- Empty list rendering (`printTradeList` with no trades).
- Welcome message format (`showWelcome`).
- Error message wrapping (`showError`).

The two `ListCommandTest` cases cover:
- That the command correctly delegates to `Ui` and produces the empty-list message.
- That `isExit()` returns `false`, confirming it does not terminate the application.

Both test classes confirm that **no state is mutated** by these components — they are pure output operations.

---

#### 2.2.6 [v2.0] Strategy Shortcut Expansion Feature

##### Overview

Power users who log tens of trades per session type strategy names frequently. To reduce friction, v2.0 introduces **strategy shortcut expansion**: a set of predefined abbreviations that are automatically expanded to their full strategy names before a trade is saved.

The supported shortcuts are:

| Shortcut | Expanded Strategy Name |
|----------|------------------------|
| `BB`     | Breakout               |
| `TBF`    | Trend Bar Failure      |
| `PB`     | Pullback               |
| `MTR`    | Major Trend Reversal   |
| `HOD`    | High of Day            |
| `LOD`    | Low of Day             |
| `MR`     | Mean Reversion         |
| `TR`     | Trading Range          |
| `DB`     | Double Bottom          |
| `DT`     | Double Top             |

##### Implementation

The expansion is implemented as a static lookup in a new utility method `ParserUtil.expandStrategyShortcut(String)`. This method is called inside `ParserUtil`'s strategy parsing pipeline, which is already invoked by `AddCommand` and `EditCommand`.

The method uses a `HashMap<String, String>` constant, `STRATEGY_SHORTCUTS`, defined at the class level:

```java
private static final Map<String, String> STRATEGY_SHORTCUTS = new HashMap<>();

static {
    STRATEGY_SHORTCUTS.put("BB",  "Breakout");
    STRATEGY_SHORTCUTS.put("TBF", "Trend Bar Failure");
    STRATEGY_SHORTCUTS.put("PB",  "Pullback");
    STRATEGY_SHORTCUTS.put("MTR", "Major Trend Reversal");
    STRATEGY_SHORTCUTS.put("HOD", "High of Day");
    STRATEGY_SHORTCUTS.put("LOD", "Low of Day");
    STRATEGY_SHORTCUTS.put("MR",  "Mean Reversion");
    STRATEGY_SHORTCUTS.put("TR",  "Trading Range");
    STRATEGY_SHORTCUTS.put("DB",  "Double Bottom");
    STRATEGY_SHORTCUTS.put("DT",  "Double Top");
}

public static String expandStrategyShortcut(String raw) {
    String upper = raw.trim().toUpperCase();
    return STRATEGY_SHORTCUTS.getOrDefault(upper, raw.trim());
}
```

If the input does not match any known shortcut, it is returned unchanged. This means custom strategy names (e.g., `Gap Fill`) continue to work without modification.

##### Sequence Diagram — Strategy shortcut expansion during `add`

```
User           Parser        AddCommand        ParserUtil         Trade
 │               │               │                  │               │
 │─"add ... strat/BB"──►│        │                  │               │
 │               │──new AddCommand(args)──►│         │               │
 │               │               │──expandStrategyShortcut("BB")──►│  │
 │               │               │◄──"Breakout"───────────────────│  │
 │               │               │──new Trade(..., "Breakout")──────────►│
 │               │◄──────────────│                  │               │
```

##### Why Implemented This Way

Expansion is done at parse time (in `AddCommand`'s constructor), not at display time. This means:

1. The expanded name is what gets stored in the file. If the user runs `list`, they see `Breakout`, not `BB`.
2. The `compare` command (see below) groups by the expanded name, so `BB` and `Breakout` entered by different team members are correctly unified.
3. The `Trade` object is always constructed with a clean, canonical strategy name.

**Alternatives considered:**

- **Expand at display time only**: Rejected because stored data would contain abbreviations, making the storage file harder to read and causing grouping bugs in the `compare` command.
- **Store the abbreviation and expand only in reports**: Rejected for the same reasons as above. Canonical data at the source is simpler and safer.
- **Use an enum instead of a HashMap**: Considered, but a `HashMap` is easier to extend at runtime (e.g., user-defined shortcuts in a future version) and does not require recompilation to add new shortcuts.

---

#### 2.2.7 [v2.0] Strategy Comparison Feature (`compare` command)

##### Overview

The `compare` command allows a trader to see performance metrics broken down by strategy. Instead of viewing one aggregate summary across all trades, the user can see exactly how each individual strategy performs — win rate, average win, average loss, and expected value (EV) — in a single command.

**Example output:**

```
compare

Strategy Comparison:

Breakout:
  Trades: 15 | Win Rate: 60% | Avg Win: 2.02R | Avg Loss: 0.95R | EV: +0.832R

Pullback:
  Trades: 20 | Win Rate: 50% | Avg Win: 1.50R | Avg Loss: 1.00R | EV: +0.250R
```

##### Architecture-Level Design

The `compare` command follows the same architecture as every other command in TradeLog. It fits into the existing structure without requiring any changes to `TradeLog`, `Parser`, `TradeList`, `Storage`, or `Ui`.

The new classes and modifications required are:

| Class            | Change                                                   |
|------------------|----------------------------------------------------------|
| `CompareCommand` | New class extending `Command`                            |
| `Parser`         | Add `case "compare"` to the switch                       |
| `Ui`             | Add `showStrategyComparison(Map<String, StrategyStats>)` |

A helper value object `StrategyStats` is introduced to group per-strategy metrics:

```java
class StrategyStats {
    int trades;
    int wins;
    double totalWinR;
    double totalLossR;
}
```

##### Component-Level Description

The `execute` method of `CompareCommand` performs the following steps:

1. **Guard**: If `tradeList` is empty, delegate to `ui.showSummaryEmpty()` and return.
2. **Grouping**: Iterate through all trades. For each trade, look up or create a `StrategyStats` entry in a `LinkedHashMap<String, StrategyStats>` keyed by strategy name. A `LinkedHashMap` is used (instead of `HashMap`) to preserve insertion order so strategies appear in the order they were first logged.
3. **Accumulation**: For each trade, increment the trade count. If the R:R ratio is positive, increment wins and accumulate `totalWinR`; if negative, accumulate `totalLossR`.
4. **Display**: After the loop, pass the populated map to `ui.showStrategyComparison(...)`, which formats and prints each strategy's block.

##### Sequence Diagram — `compare` execution

```
TradeLog     Parser      CompareCommand        TradeList           Ui
    │            │               │                  │               │
    │─"compare"──►│               │                  │               │
    │            │──new CompareCommand()──►│          │               │
    │            │◄──────────────│          │               │
    │────────────execute(tradeList, ui, storage)──────►│               │
    │            │               │──size()─────────────►│               │
    │            │               │◄── n ───────────────│               │
    │            │               │  loop i=0..n-1      │               │
    │            │               │──getTrade(i)────────►│               │
    │            │               │◄── Trade ───────────│               │
    │            │               │── accumulate into StrategyStats map  │
    │            │               │──showStrategyComparison(map)──────────►│
    │            │               │                     │── prints each strategy block
    │◄───────────│◄──────────────│                     │               │
```

##### Class Diagram — CompareCommand and its dependencies

```
«abstract»
Command
    │
    └── CompareCommand
            │ uses
            ├──────────► TradeList
            │                │ contains
            │                └──────────► Trade
            │                                │ getRiskRewardRatio()
            │                                │ getStrategy()
            │ uses
            └──────────► Ui
                            │
                            └── showStrategyComparison(map)
```

##### Design Rationale

**Why a `LinkedHashMap` and not sorting alphabetically?**
Traders tend to think of their strategies in the order they used them, not alphabetically. Preserving insertion order makes the output feel natural. A future `compare sort/alpha` variant could sort alphabetically if desired.

**Why not add grouping logic to `TradeList`?**
`TradeList` is a model class that should only manage the collection — add, delete, get, and size. Adding grouping logic there would violate single responsibility. `CompareCommand` is the correct place for this aggregation, consistent with how `SummaryCommand` handles its own calculations.

**Why not reuse `SummaryCommand`'s logic?**
`SummaryCommand` calculates one aggregate result. `CompareCommand` calculates `n` independent results (one per strategy). Though the per-strategy arithmetic is similar, merging them into a single class would make both harder to read, test, and extend independently.

**Alternatives considered:**

- **A `filterByStrategy` method on `TradeList`**: This was considered to avoid iterating through all trades in `CompareCommand`. However, it would require multiple passes (one per unique strategy), making it O(n × k) where k is the number of strategies. The single-pass accumulation approach is O(n) and simpler.
- **Storing `StrategyStats` inside `TradeList` as a cached field**: Rejected because it would couple the model to a specific reporting concept and require cache invalidation on every add/edit/delete.

---

## 3. Product Scope

### 3.1 Target User Profile

**Daniel** is a proprietary trader who works independently and relies heavily on data to refine his trading strategies. He spends most of his day analysing charts and executing trades, and prefers fast, keyboard-based tools over graphical interfaces. He values efficiency, accuracy, and structured data analysis to improve his trading performance.

### 3.2 Value Proposition

Provides a CLI-based, systematic way to log trades and test systems that is faster and more efficient than logging trades on Google Sheets. Has the ability to automatically calculate trade details such as Risk:Reward ratio, ROI, expected value (EV) of the system with varying timeframes.

### 3.3 Scope

TradeLog helps financial trading professionals systematically log, manage, and analyze their trading data through a fast CLI-based system. It enables users to:

* Log and manage trades efficiently.
* Calculate key trading metrics (ROI, Risk:Reward, EV, YTD performance).
* Filter and analyze trades by strategy, ticker, or timeframe.
* Test and compare trading systems.
* Monitor risk exposure.

---

## 4. User Stories

| Version  | As a ...             | I want to ...                                                      | So that I can ...                                         |
|:---------|:---------------------|:-------------------------------------------------------------------|:----------------------------------------------------------|
| **v1.0** | trader               | Log my trading data                                                | I can call on the data to run tests on trading systems    |
| **v1.0** | trader               | Automatically calculate my Year-To-Date (YTD) ROI                  | Easily track my progress and performance                  |
| **v1.0** | trader               | delete an incorrectly entered set of data                          | my statistics remain accurate                             |
| **v1.0** | trader               | edit previously logged trades                                      | I can correct mistakes in my data                         |
| **v1.0** | forgetful trader     | trades are saved automatically after every command                 | I don't lose recent entries due to distraction or fatigue |
| **v2.0** | trader               | Filter my trades by a specific ticker symbol                       | Review my performance on a single asset                   |
| **v2.0** | trader               | Save trading systems                                               | I can easily test them on different datasets              |
| **v2.0** | trader               | Automatically calculate my risk:reward ratio                       | Quickly decide if I want to confirm a trade               |
| **v2.0** | trader               | Switch between testing mode and live trading mode                  | I can separate live trades from backtest trades           |
| **v2.0** | trader               | Automatically calculate Expected Value (EV) of a specific strategy | I know the mathematical advantage of my system            |
| **v2.0** | trader               | Automatically convert and export my data to CSV                    | Better review my performance and use other tools          |
| **v2.0** | trader               | Set a Daily Loss Limit that warns me                               | Prevents me from taking unnecessarily large risk          |
| **v2.0** | trader               | tag trades with a specific strategy name                           | I can group and evaluate them easily                      |
| **v2.0** | trader               | sort trades by profit or loss                                      | I can quickly identify my biggest wins and losses         |
| **v2.0** | trader               | Automatically calculate the EV of multiple strategies              | Decide which strategy has the best performance            |
| **v2.0** | trader               | see my current win or loss streak                                  | I remain aware of potential overconfidence or tilt        |
| **v2.0** | careless trader      | be warned if I enter a duplicate record                            | I don't accidentally double-count the same fill           |
| **v2.0** | expert trader        | set short aliases for long tickers                                 | I don't type dots and hyphens hundreds of times           |
| **v2.0** | trader               | see tickers I've looked up but didn't trade                        | So that I can quickly enter them if I circle back         |
| **v2.0** | trader               | mark a ticker as "watched but not taken"                           | Remember which setups I passed on during review           |
| **v2.0** | power user           | use shortcut codes for strategy names (e.g., BB, PB)               | I can log trades faster without typing full names         |
| **v2.0** | trader               | compare performance across all strategies in one view              | I can identify which strategy has the best edge           |
| **v3.0** | trader               | tag each trade with my emotional state                             | I can identify psychological patterns                     |
| **v3.0** | trader               | view a summary over a selected date range                          | I can analyze short-term results                          |
| **v3.0** | trader               | view my win rate for a specific strategy                           | I can assess its consistency                              |
| **v3.0** | trader               | calculate average risk per trade                                   | I can monitor my risk management discipline               |
| **v3.0** | trader               | back up my trading data locally                                    | I do not lose my records                                  |
| **v3.0** | trader               | load previously saved trading sessions                             | I can continue my analysis seamlessly                     |
| **v3.0** | trader               | complete a pre-trade checklist before entry                        | I follow my trading plan consistently                     |
| **v3.0** | trader               | view multiple strategies side-by-side                              | Objectively compare their performance                     |
| **v3.0** | trader               | automatically calculate maximum drawdown                           | I understand my worst-case risk exposure                  |
| **v3.0** | trader               | export trades from a specific date range to CSV                    | Share selected periods with my mentor or accountant       |
| **v3.0** | trader               | automatically calculate the R-multiple                             | I evaluate performance relative to risk                   |
| **v3.0** | trader               | review a summary and confirm before saving                         | So that I catch typos before they enter my records        |
| **v3.0** | inexperienced trader | see how many trades I've taken today                               | So that I know if I'm overtrading                         |
| **v3.0** | trader               | receive an alert if win rate drops below threshold                 | I can review and adjust my strategy promptly              |
| **v3.0** | trader               | write reflections for each trade                                   | I can improve my decision-making process                  |
| **v3.0** | trader               | filter and analyze trades by time of day                           | I can identify when I perform best                        |
| **v3.0** | trader               | Bulk import historical trades                                      | I can test my trading systems on other datasets           |
| **v3.0** | trader               | attach a chart screenshot to each trade                            | I can visually review my entry and exit decisions         |
| **v3.0** | trader               | see my total capital currently at risk                             | I avoid overexposure                                      |

---

## 5. Non-Functional Requirements

1. **Platform Independence**: Must run on any OS with Java 17 or higher installed.
2. **Performance**: Statistics calculation (EV, ROI) should take <100ms for up to 2,000 trades.
3. **Data Persistence**: Immediate auto-save to `tradelog.txt` after every valid state-changing command.
4. **Offline Capability**: All trade data must be stored locally without requiring cloud connectivity.

---

## 6. Glossary

* **Ticker**: Unique symbol representing a traded asset (e.g., AAPL).
* **R:R (Risk:Reward)**: The ratio of potential profit to potential loss.
* **EV (Expected Value)**: The average amount a trader can expect to win or lose per trade.
* **ROI (Return on Investment)**: Percentage return relative to capital.
* **R-multiple**: A trade's profit or loss expressed as a multiple of the initial risk (e.g., a 2R win means the trade made twice the amount risked).
* **Strategy shortcut**: A predefined abbreviation (e.g., `BB`) that the system automatically expands to a full strategy name (e.g., `Breakout`) at parse time.

---

## 7. Instructions for Manual Testing

### 7.1 Initial Launch

1. Ensure the `data/` folder is empty.
2. Run `java -jar TradeLog.jar`.
3. Verify that the application creates a fresh `tradelog.txt` file.

### 7.2 Testing CRUD (v1.0)

1. **Add**: `add t/TSLA d/2026-03-18 dir/long e/200 x/220 s/190 o/win strat/Trend`
2. **Edit**: `edit 1 x/230`
3. **Delete**: `delete 1`
4. **List**: `list` (Verify it reflects changes immediately in the console).

### 7.3 Testing Strategy Shortcuts (v2.0)

1. Run: `add t/AAPL d/2026-03-18 dir/long e/150 x/165 s/140 o/win strat/BB`
2. Run: `list`
3. Verify the stored strategy name is `Breakout`, not `BB`.
4. Run with an unrecognised shortcut: `add t/TSLA d/2026-03-18 dir/long e/200 x/220 s/190 o/win strat/CustomStrat`
5. Verify the strategy is stored as `CustomStrat` unchanged.

### 7.4 Testing Strategy Comparison (v2.0)

1. Add at least two trades with different strategy names (or shortcuts).
2. Run: `compare`
3. Verify that each strategy appears as a separate block with correct trade count, win rate, and EV figures.
4. Run `compare` on an empty trade list and verify the empty-list message is shown.