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

#### 2.2.2 Parser Component

##### Architecture-Level Description

The parsing architecture is responsible for translating raw user input from the CLI into executable `Command` objects. To maintain separation of concerns and ensure financial data integrity, this logic is decoupled into three distinct utility classes: `Parser` (the command router), `ArgumentTokeniser` (the string extractor), and `ParserUtil` (the domain validator).

It takes the raw string, identifies the user's intent, extracts the variable data, enforces strict mathematical trading rules, and ultimately outputs a safe, validated command ready for execution.

##### Component-Level Description

```
«utility»
Parser
  |
  |-- parseCommand(userInput)
        |
        |-- routes to specific Command constructor
        \-- throws TradeLogException on unknown command

«utility»
ArgumentTokeniser
  |
  \-- tokenise(userInput, prefixes)
        |
        \-- returns HashMap<String, String> of prefix-value pairs

«utility»
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

---

#### 2.2.3 ListCommand

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

#### 2.2.4 AddCommand

##### Architecture-Level Description

The `AddCommand` is a core state-changing operation responsible for introducing new trades into the TradeLog system. It acts as the primary bridge between the `Parser` component (which supplies the raw user input), the `Model` component (by instantiating new `Trade` objects and updating the in-memory `TradeList`), and the `Storage` component (triggering the immediate-save mechanism to persist the new data).

To adhere to the principle of Separation of Concerns, the execution of the `add` feature is explicitly split into two distinct phases: an initialization/validation phase, and an execution/mutation phase.

##### Component-Level Description

1. Construction & Validation Phase: When the user inputs an `add` command, the `Parser` creates a new `AddCommand(String arguments)`. The constructor immediately passes the raw string to the `ArgumentTokeniser` to map prefixes to their respective string values. It then utilizes `ParserUtil` to strictly validate the financial logic of the inputs (e.g., ensuring a `long` position does not have a stop-loss higher than the entry price, and checking that all prices are valid positive numbers). If any validation fails during this step, a `TradeLogException` is thrown before the `TradeList` or `Storage` is ever accessed.

2. Execution Phase: Once the `AddCommand` is successfully instantiated with a fully valid `Trade` object held in its internal state, the main loop calls `execute(tradeList, ui, storage)`. The command appends the new trade to the `TradeList`, triggers the `Ui` to display a confirmation message with the formatted trade details, and implicitly relies on the main loop's architecture to save the newly updated state to the text file.

##### Sequence Diagram — Full `add` execution path

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

---

#### 2.2.5 DeleteCommand

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

An alternative considered letting `DeleteCommand` throw the `IndexOutOfBoundsException` back up to the main `TradeLog` execution loop. This was rejected because the main loop would then need specific catch blocks for every possible internal data structure error across all commands. Keeping the error handling localized to the command ensures the main loop remains clean and strictly focused on high-level orchestration.

---

#### 2.2.6 SummaryCommand

##### Architecture-Level Description

`SummaryCommand` calculates and displays an aggregate mathematical performance report across the entire `TradeList`. Like `ListCommand`, it is a non-mutating operation; it reads the application's state to perform calculations but does not alter the data or interact with `Storage`.

##### Component-Level Description

When `execute()` is called, `SummaryCommand` first guards against an empty `TradeList`, triggering an early exit via `Ui.showSummaryEmpty()` if no trades exist.

If populated, it iterates through every trade in the list exactly once. During this single `O(n)` pass, it maintains running totals for total trades, winning trades, losing trades, total positive R-multiples, and total negative R-multiples. Break-even trades (where Risk/Reward equals 0) are safely skipped in the specific win/loss tallies but are correctly factored into the total trade count and Expected Value (EV) denominator.

After the loop completes, it calculates the win rate, average win, average loss, and EV, passing these final primitive floating-point values directly to `Ui.showSummary()` for formatting.

##### Sequence Diagram — `summary` execution and calculation

```
TradeLog        SummaryCommand        TradeList             Ui
│                  │                   │                 │
│────execute()────►│                   │                 │
│                  │──isEmpty()───────►│                 │
│                  │◄──boolean─────────│                 │
│                  │                   │                 │
│                  │ [if not empty]    │                 │
│                  │──size()──────────►│                 │
│                  │◄──int─────────────│                 │
│                  │                   │                 │
│                  │ loop [for every trade in list]      │
│                  │──getTrade(i)─────►│                 │
│                  │◄──Trade───────────│                 │
│                  │──getRiskReward()─►│                 │
│                  │◄──double──────────│                 │
│                  │                   │                 │
│                  │──showSummary(metrics)──────────────►│
│                  │◄────────────────────────────────────│
│◄─────────────────│                   │                 │
```

##### Design Rationale

The alternative considered was having the constructor simply store the raw user string and defer all parsing and validation to `execute()`. This was rejected because it would violate the Single Responsibility Principle. It would cause the `execute()` method to handle input sanitization, integer parsing, validity checks, model mutation, and UI interaction all in one place, making the command less modular and harder to test.

By validating the index during construction, the implementation ensures that only logically valid `DeleteCommand` objects can be created. This makes runtime execution simpler and more focused on state mutation and user feedback. In addition, handling out-of-bounds indices during execution is appropriate because whether an index exists depends on the current state of the `TradeList`, which is only known at runtime.

---

#### 2.2.7 Testing Strategy for `Ui` and `ListCommand`

Both `Ui` and `ListCommand` are tested using a `captureOutput` helper that temporarily redirects `System.out` to a `ByteArrayOutputStream`. This pattern avoids any dependency on mocking frameworks and works natively with JUnit 5.

The three `UiTest` cases cover:
- Empty list rendering (`printTradeList` with no trades).
- Welcome message format (`showWelcome`).
- Error message wrapping (`showError`).

The two `ListCommandTest` cases cover:
- That the command correctly delegates to `Ui` and produces the empty-list message.
- That `isExit()` returns `false`, confirming it does not terminate the application.

Both test classes confirm that **no state is mutated** by these components — they are pure output operations.

#### 2.2.8 EditCommand

##### Architecture-Level Description
The `EditCommand` allows users to modify existing trades within the `TradeList`. To minimize user friction, it supports **Partial Updates**, where only specified prefixes (e.g., `t/`, `e/`) are modified while others remain unchanged. The implementation prioritizes **Atomicity**: the command validates the entire "new state" of the trade before any internal data is overwritten.

##### Component-Level Description
The `execute(tradeList, ui, storage)` method performs the following logic:

1.  **Retrieval**: Fetches the existing `Trade` object from the `TradeList` using `targetIndex`.
2.  **Defensive Assertions**: Employs **Java Assertions** to verify that `tradeList` and `ui` are not null, and that `tradeToEdit` was successfully retrieved.
3.  **Staging**: Pre-computes updated values using `parsedArgs`. If a prefix is present, `ParserUtil` is used to parse the new value; otherwise, the existing value from the `Trade` object is used.
4.  **Validation**: Calls `ParserUtil.validatePrices()` and `ParserUtil.validateStopLoss()` on the staged variables to ensure the proposed edit is financially logical.
5.  **Commitment**: Once validated, it calls the respective `set` methods on the `Trade` object and triggers the `Ui` to display the updated record.



##### Sequence Diagram — `edit` execution path

````
TradeLog        EditCommand           TradeList              Trade          Ui
│                 │                     │                   │               │
│──execute(...)──►│                     │                   │               │
│                 │──getTrade(idx)────► │                   │               │
│                 │◄── tradeToEdit ──── │                   │               │
│                 │                     │                   │               │
│                 │─── [Assert tradeToEdit != null] ───────►│               │
│                 │─── [Validate staged variables] ────────►│               │
│                 │                     │                   │               │
│                 │──setTicker(...)────►│                   │               │
│                 │──setEntry(...)─────►│                   │               │
│                 │                     │                   │               │
│                 │──showTradeUpdated──►│                   │               │
│                 │◄────────────────────│                   │               │
│◄────────────────│                     │                   │               │

````
##### Design Rationale
* **Partial Updates**: Chosen over full replacement because trades contain 8+ fields; forcing a user to re-input all data to fix one typo (e.g., in a Ticker) is inefficient for a CLI tool.
* **Validation before Mutation**: Ensures that the `Model` never enters an invalid state (e.g., a Long position with a stop-loss above entry), maintaining data integrity.
* **Assertions**: Used for internal invariants. If `tradeToEdit` is null despite passing the index check, it indicates a critical failure in the `Model`'s list management that requires immediate developer attention.

#### 2.2.9 Testing Strategy for `EditCommand` and Assertions

The `EditCommandTest` class ensures that the "Read-Validate-Commit" cycle works as intended.

**Key Test Cases:**
* **Statelessness of Unedited Fields**: Verifies that fields not specified in the `edit` command remain identical to their original values.
* **Boundary Validation**: Confirms that `TradeLogException` is thrown if an edit results in an invalid price relationship (e.g., Entry == Stop Loss).
* **Assertion Verification**: Although `assert` is typically for development, test environments are configured with `-ea` (enable assertions) to ensure that the internal null-checks added to `EditCommand` and `ListCommand` trigger correctly if invalid dependencies are provided.
---

#### 2.2.10 [v2.0] Strategy Shortcut Expansion Feature

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

The expansion is implemented as a static lookup in `ParserUtil.parseStrategy(String)`.
This strategy parsing pipeline is used by `AddCommand`, `EditCommand`, and `FilterCommand`.

The feature uses an immutable `Map<String, String>` constant, `STRATEGY_SHORTCUTS`,
defined at the class level:

```java
private static final Map<String, String> STRATEGY_SHORTCUTS =
        createStrategyShortcuts();

public static String parseStrategy(String strategy) {
    String trimmedStrategy = strategy.trim();
    return STRATEGY_SHORTCUTS.getOrDefault(
            trimmedStrategy.toUpperCase(), trimmedStrategy);
}
```

If the input does not match any known shortcut, it is returned unchanged. This means custom strategy names (e.g., `Gap Fill`) continue to work without modification.

##### Sequence Diagram - Strategy shortcut expansion during `add`

![Strategy shortcut expansion sequence](diagrams/strategy-shortcut-add-sequence.png)

##### Why Implemented This Way

Expansion is done at parse time, not at display time. This means:

1. The expanded name is what gets stored in the file. If the user runs `list`, they see `Breakout`, not `BB`.
2. The `compare` command (see below) groups by the expanded name, so `BB` and `Breakout` entered by different team members are correctly unified.
3. The `Trade` object is always constructed with a clean, canonical strategy name.

**Alternatives considered:**

- **Expand at display time only**: Rejected because stored data would contain abbreviations, making the storage file harder to read and causing grouping bugs in the `compare` command.
- **Store the abbreviation and expand only in reports**: Rejected for the same reasons as above. Canonical data at the source is simpler and safer.
- **Use an enum instead of a lookup map**: Considered, but a lookup map keeps the parsing logic lightweight and easy to extend.

---

#### 2.2.11 [v2.0] Strategy Comparison Feature (`compare` command)

##### Overview

The `compare` command allows a trader to see performance metrics broken down by strategy. Instead of viewing one aggregate summary across all trades, the user can see exactly how each individual strategy performs - win rate, average win, average loss, and expected value (EV) - in a single command.

**Example output:**

```
compare

Strategy Comparison:

Breakout:
Trades: 15
Win Rate: 60%
Average Win: 2.02R
Average Loss: 0.95R
EV: +0.832R

Pullback:
Trades: 20
Win Rate: 50%
Average Win: 1.50R
Average Loss: 1.00R
EV: +0.250R
```

##### Architecture-Level Design

The `compare` command follows the same architecture as every other command in TradeLog. It fits into the existing structure without requiring any changes to `TradeLog`, `TradeList`, or `Storage`.

The new classes and modifications required are:

| Class            | Change                                                   |
|------------------|----------------------------------------------------------|
| `CompareCommand` | New class extending `Command`                            |
| `StrategyStats`  | New helper class for per-strategy aggregates             |
| `Parser`         | Add `case "compare"` to the switch                       |
| `Ui`             | Add `showStrategyComparison(Map<String, StrategyStats>)` |

A helper value object `StrategyStats` is introduced to group per-strategy metrics:

```java
class StrategyStats {
    int tradeCount;
    int winCount;
    int lossCount;
    double totalWinR;
    double totalLossR;
}
```

##### Component-Level Description

The `execute` method of `CompareCommand` performs the following steps:

1. **Guard**: If `tradeList` is empty, delegate to `ui.showSummaryEmpty()` and return.
2. **Grouping**: Iterate through all trades. For each trade, call `trade.getStrategy()` and use `strategyComparison.computeIfAbsent(...)` on a `LinkedHashMap<String, StrategyStats>` to look up or create the corresponding accumulator. A `LinkedHashMap` is used to preserve insertion order so strategies appear in the order they were first logged.
3. **Accumulation**: Call `trade.getRiskRewardRatio()` and pass the result to `strategyStats.addTrade(...)` so the per-strategy counts and totals are updated in one place.
4. **Display**: After the loop, pass the populated map to `ui.showStrategyComparison(...)`, which formats and prints each strategy block.

##### Sequence Diagram - `compare` execution

![Compare execution sequence](diagrams/compare-sequence.png)

##### Class Diagram - CompareCommand and its dependencies

![Compare command class diagram](diagrams/compare-class-diagram.png)

##### Design Rationale

**Why a `LinkedHashMap` and not sorting alphabetically?**
Traders tend to think of their strategies in the order they used them, not alphabetically. Preserving insertion order makes the output feel natural. A future `compare sort/alpha` variant could sort alphabetically if desired.

**Why not add grouping logic to `TradeList`?**
`TradeList` is a model class that should only manage the collection - add, delete, get, and size. Adding grouping logic there would violate single responsibility. `CompareCommand` is the correct place for this aggregation, consistent with how `SummaryCommand` handles its own calculations.

**Why not reuse `SummaryCommand`'s logic?**
`SummaryCommand` calculates one aggregate result. `CompareCommand` calculates `n` independent results (one per strategy). Though the per-strategy arithmetic is similar, merging them into a single class would make both harder to read, test, and extend independently.

**Alternatives considered:**

- **A `filterByStrategy` method on `TradeList`**: This was considered to avoid iterating through all trades in `CompareCommand`. However, it would require multiple passes (one per unique strategy), making it O(n x k) where `k` is the number of strategies. The single-pass accumulation approach is O(n) and simpler.
- **Storing `StrategyStats` inside `TradeList` as a cached field**: Rejected because it would couple the model to a specific reporting concept and require cache invalidation on every add/edit/delete.

---

#### 2.2.12 Storage Component (Encrypted Persistence)

##### Architecture-Level Description

`Storage` is the sole class responsible for reading and writing trade data to disk. All data is encrypted at rest using **AES-128** symmetric encryption. The encryption key is derived from the user's password via a **SHA-256** hash, with only the first 16 bytes used to form the AES key. No plaintext trade records are ever written to the file.

The file format is:

```
<SHA-256 password hash (Base64)>
<AES-encrypted trade line 1 (Base64)>
<AES-encrypted trade line 2 (Base64)>
...
```

The first line is the Base64-encoded SHA-256 hash of the password. This is used on load to verify that the correct password has been provided before any decryption is attempted.

`Storage` has no dependency on `Ui`, `Parser`, or any `Command`. It only depends on `TradeList` and `Trade` from the model layer, keeping coupling minimal.

##### Component-Level Description

| Method | Responsibility |
|---|---|
| `setPassword(String)` | Derives the AES key and stores the password hash. Must be called before `saveTrades` or `loadTrades`. |
| `saveTrades(TradeList)` | Creates parent directories if needed, writes the password hash on line 1, then writes one AES-encrypted, Base64-encoded trade string per line. |
| `loadTrades()` | Reads line 1 and compares it to `passwordHash`. If it matches, decrypts each subsequent line, parses the 8-field pipe-delimited format, and populates a `TradeList`. |
| `exists()` | Returns whether the underlying file is present on disk. Used by `ProfileManager` during startup. |

The encryption and decryption use Java's `javax.crypto.Cipher` in `AES/ECB` mode (the default single-block `"AES"` transformation). Each trade's `toStorageString()` output (pipe-delimited) is individually encrypted and Base64-encoded before being written as a line.

##### Sequence Diagram — `saveTrades` on exit
![Save Trades on exit Sequence Diagram](diagrams/save-trades-on-exit-diagram.png)

##### Sequence Diagram — `loadTrades` on startup
![Load Trades Diagram](diagrams/load-trades-diagram.png)

##### Design Rationale

**Why derive the key from a password hash rather than storing the key directly?**
The password hash acts as both the AES key seed and the per-file identity marker (the first line of each profile file). This allows `ProfileManager` to determine which file belongs to which user without storing any plaintext credential.

**Why AES-128 and not AES-256?**
AES-128 (16-byte key) is sufficient for protecting trade records from casual access. Moving to AES-256 would require only changing the `Arrays.copyOf` length from 16 to 32; the rest of the implementation is unchanged.

**Why encrypt each trade line independently rather than the whole file?**
Individual-line encryption makes the format robust: a single corrupted line affects only that trade, not the rest of the file. It also maps naturally to the line-by-line read loop in `loadTrades`.

**Alternatives considered:**
- **Storing plaintext**: Rejected. A trader's position sizes, entry/exit prices, and strategies are commercially sensitive. Plaintext storage would expose this data to anyone with filesystem access.
- **Storing only a password prompt and trusting the key**: Rejected because without the stored hash on line 1, `ProfileManager` would have no way to distinguish a wrong-password decryption failure from a corrupted file.

---

#### 2.2.13 ProfileManager (Multi-Profile Support)

##### Architecture-Level Description

`ProfileManager` is the startup component that resolves which storage file belongs to the current user. It sits between `TradeLog`'s constructor and the `Storage` class, and is the only class that knows how multiple profile files are named or how to scan them.

The password is **not** passed in from `TradeLog`. Instead, `ProfileManager` reads it interactively from the user via `Ui.readPassword()` at startup, displaying a context-sensitive prompt depending on whether any profile files already exist.

Profile files follow the naming convention:

```
<baseDir>/<baseName>.txt          ← index 0 (the default)
<baseDir>/<baseName>_1.txt        ← index 1
<baseDir>/<baseName>_2.txt        ← index 2
...
```

Each file belongs to exactly one password (identified by the SHA-256 hash stored on its first line). `findNextAvailablePath()` determines the path for a new profile by scanning for the first suffix index whose file does not yet exist.

##### Component-Level Description

The constructor `ProfileManager(String baseDir, String baseName, Ui ui)` runs the following logic:

1. **Determine whether any profile files exist** by checking if `<baseDir>/<baseName>.txt` is present on disk.
2. **Prompt for a password** via `ui.readPassword(prompt)`:
   - If no files exist: `"No profiles found. Create a new password:"`
   - If files exist: `"Enter password to load your profile (or create a new one):"`
3. **Branch on file existence:**
   - **No files exist** → call `createNewProfile(...)` immediately with message `"No existing profile found. Creating new profile..."` and exit.
   - **Files exist** → call `tryLoadExistingProfile(...)`:
     - **Returns `true`** (password matched a file): constructor exits successfully.
     - **Returns `false`** (no file matched the password): prompt the user `"No profile found for the entered password. Create a new profile? (yes/no):"`.
       - If the user answers `"yes"`: call `createNewProfile(...)` with message `"Creating new profile..."` and exit.
       - If the user answers anything else: loop back to step 2 and re-prompt for the password.

After the constructor completes, `getActiveStorage()` and `getLoadedTrades()` pass the result to `TradeLog`.

| Method | Responsibility |
|---|---|
| `ProfileManager(String, String, Ui)` | Interactive startup: prompts for password, finds or creates the matching profile. |
| `tryLoadExistingProfile(String, String, String, Ui)` | Iterates over existing numbered files, attempts `setPassword` + `loadTrades` on each; returns `true` on a hash match. |
| `createNewProfile(String, String, String, Ui, String)` | Finds the next available file path, initialises a fresh `Storage`, calls `setPassword`, and sets `loadedTrades` to a new empty `TradeList`. |
| `findNextAvailablePath(String, String)` | Returns the first `<baseDir>/<baseName>_N.txt` path (or `<baseName>.txt` for index 0) that does not yet exist on disk. |
| `getActiveStorage()` | Returns the `Storage` instance resolved during construction. |
| `getLoadedTrades()` | Returns the `TradeList` loaded (or newly created) during construction. |

##### Sequence Diagram — startup with an existing matching profile
![Existing Matching Profile Diagram](diagrams/existing-matching-profile-diagram.png)

##### Sequence Diagram — startup when password does not match, user opts to create new profile
![Password Mismatch and New Profile Creation Diagram](diagrams/password-mismatch-and-new-profile-creation-diagram.png)

##### Design Rationale

**Why does `ProfileManager` read the password interactively rather than receiving it as a constructor argument?**
Keeping password acquisition inside `ProfileManager` avoids passing a sensitive credential through `TradeLog`'s constructor. `ProfileManager` owns the full login loop — prompting, validating, and retrying — without exposing that state to its caller.

**Why scan files sequentially rather than encoding the profile index in the file itself?**
Keeping profile selection implicit (driven purely by password matching) means the user never needs to remember a profile number. The password is the sole credential.

**Why prompt the user before creating a new profile when no match is found?**
Silently creating a new profile on a password mismatch would produce spurious empty profiles from typographical errors. The `yes/no` confirmation lets the user retry their password instead, preventing unintended profile proliferation.

**Alternatives considered:**
- **Single file for all users**: Rejected. A single file would require a more complex internal structure to separate users' data and would make per-user password protection harder.
- **Using a directory per user**: Considered but rejected for simplicity. The sequential suffix convention is easy to implement and requires no directory management.

---

#### 2.2.14 FilterCommand

##### Architecture-Level Description

`FilterCommand` provides read-only querying of the in-memory `TradeList` without modifying any state. It supports filtering by up to three independent criteria — **ticker**, **strategy**, and **date** — applied as a logical AND. It also supports an optional **partial-match mode** (`-p` flag) that uses substring/case-insensitive matching instead of exact equality.

After displaying the matched trades, `FilterCommand` delegates to `SummaryCommand` on the filtered subset, giving the user performance metrics for just the filtered trades without any extra command.

##### Component-Level Description

The constructor parses the argument string in two steps:

1. `ArgumentTokeniser.tokenise` extracts the values for `t/`, `strat/`, and `d/`. Missing prefixes default to empty strings.
2. The `-p` flag is detected by checking whether the raw argument array contains the literal string `"-p"`.
3. If all three criteria are empty after parsing, a `TradeLogException` is thrown: at least one filter must be provided.

The `execute` method:

1. Iterates through all trades in `tradeList`.
2. For each trade, evaluates three boolean conditions (`matchesTicker`, `matchesStrategy`, `matchesDate`). An empty criterion always evaluates to `true` (i.e., it is not applied).
3. **Exact mode** (default): uses `equals` for ticker and date, `equalsIgnoreCase` for strategy.
4. **Partial mode** (`-p`): uses `contains` for ticker and date, `toLowerCase().contains(toLowerCase())` for strategy.
5. Matching trades are collected into both an index list (for display with their original 1-based numbers) and a new `TradeList` (for the summary calculation).
6. If no matches are found, `ui.showMessage("No trades match the filter criteria.")` is called.
7. If matches are found, the matched trades are printed with their original indices, then `SummaryCommand.execute(filteredTrades, ui, storage)` is called on the subset.

##### Sequence Diagram — `filter t/AAPL` with two trades in list
![Filtering Trades Diagram](diagrams/filtering-trades-diagram.png)

##### Supported Filter Criteria

| Prefix | Field matched | Exact mode | Partial mode (`-p`) |
|--------|--------------|-----------|---------------------|
| `t/`   | Ticker symbol | `equals` | `contains` |
| `strat/` | Strategy name | `equalsIgnoreCase` | case-insensitive `contains` |
| `d/`   | Trade date | `equals` | `contains` (useful for filtering by year or month, e.g., `d/2026-03`) |

##### Usage Examples

```
filter t/AAPL                        → exact ticker match
filter strat/Breakout d/2026-03      → trades with Breakout strategy in March 2026
filter -p t/AA                       → all tickers containing "AA" (e.g., AAPL, AAVE)
filter -p strat/break                → case-insensitive partial strategy match
```

##### Design Rationale

**Why delegate the summary to `SummaryCommand` rather than duplicating the logic?**
`SummaryCommand` already computes win rate, average win/loss, EV, and total R from a `TradeList`. Delegating avoids duplication and guarantees that the filtered-subset metrics are always consistent with the full-list metrics produced by `summary`.

**Why use original 1-based indices (from the full list) when displaying filtered results?**
Displaying the original index allows the user to immediately act on a filtered result — for example, using `edit 3` or `delete 3` on a trade found via `filter` without needing to re-run `list` to look up the index.

**Why require at least one criterion instead of allowing `filter` with no arguments to return all trades?**
`filter` with no criteria would be functionally identical to `list`. Requiring at least one criterion prevents accidental no-op calls and keeps the command's intent clear.

**Alternatives considered:**
- **Separate `filter-partial` command**: Rejected. Having `-p` as an inline flag keeps the command surface small and the user does not need to remember two separate command names.
- **Chained filter pipeline (filter feeds into another filter)**: Considered as a future feature. The current AND-of-criteria design handles the most common cases; a pipeline could be introduced if users need OR logic.

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
| **v2.1** | trader               | tag each trade with my emotional state                             | I can identify psychological patterns                     |
| **v2.1** | trader               | view a summary over a selected date range                          | I can analyze short-term results                          |
| **v2.1** | trader               | view my win rate for a specific strategy                           | I can assess its consistency                              |
| **v2.1** | trader               | calculate average risk per trade                                   | I can monitor my risk management discipline               |
| **v2.1** | trader               | back up my trading data locally                                    | I do not lose my records                                  |
| **v2.1** | trader               | load previously saved trading sessions                             | I can continue my analysis seamlessly                     |
| **v2.1** | trader               | complete a pre-trade checklist before entry                        | I follow my trading plan consistently                     |
| **v2.1** | trader               | view multiple strategies side-by-side                              | Objectively compare their performance                     |
| **v2.1** | trader               | automatically calculate maximum drawdown                           | I understand my worst-case risk exposure                  |
| **v2.1** | trader               | export trades from a specific date range to CSV                    | Share selected periods with my mentor or accountant       |
| **v2.1** | trader               | automatically calculate the R-multiple                             | I evaluate performance relative to risk                   |
| **v2.1** | trader               | review a summary and confirm before saving                         | So that I catch typos before they enter my records        |
| **v2.1** | inexperienced trader | see how many trades I've taken today                               | So that I know if I'm overtrading                         |
| **v2.1** | trader               | receive an alert if win rate drops below threshold                 | I can review and adjust my strategy promptly              |
| **v2.1** | trader               | write reflections for each trade                                   | I can improve my decision-making process                  |
| **v2.1** | trader               | filter and analyze trades by time of day                           | I can identify when I perform best                        |
| **v2.1** | trader               | Bulk import historical trades                                      | I can test my trading systems on other datasets           |
| **v2.1** | trader               | attach a chart screenshot to each trade                            | I can visually review my entry and exit decisions         |
| **v2.1** | trader               | see my total capital currently at risk                             | I avoid overexposure                                      |

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
5. **Summary**: `summary`
6. **Exit**: `exit`

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

