# User Guide

## 1. Introduction

**TradeLog** provides a CLI-based, systematic way to log trades and test systems that is faster and more efficient than logging trades on Google Sheets. It has the ability to automatically calculate trade details such as Risk:Reward ratio and the Expected Value (EV) of the system. By eliminating the friction of manual spreadsheet entry, TradeLog helps traders maintain a disciplined journal and identify their mathematical edge with precision.

## 2. Quick Start

1. Ensure that you have **Java 17** or above installed on your computer.
2. Download the latest version of `TradeLog.jar` from [here](https://github.com/AY2526S2-CS2113-T11-2/tp/releases).
3. Open your terminal, navigate to the folder containing the file, and run:
   `java -jar TradeLog.jar`

---

## 3. Features

### [Version 1.0] - Core Backtesting Suite
*These features are fully functional in the current release.*

* **Adding a Trade: `add`** – Log new trades with ticker, date, direction, prices, and strategy.
* **Editing a Trade: `edit`** – Update specific fields of existing records by their index.
* **Deleting a Trade: `delete`** – Remove specific trade entries from the log by index.
* **Listing Trades: `list`** – Display all logged trades in a formatted, single-line overview.
* **Performance Summary: `summary`** – View metrics including Win Rate, Average Win/Loss, EV, and Total R.
* **Exit: `exit`** – Terminate the application and view final metrics including Win Rate, Average Win/Loss, EV, and Total R.

### [Version 2.0] - System & Logic Enhancement
*Planned features for advanced strategy management.*

* **Duplicate Warning** – Alerts for duplicate entries of the same ticker, date, and price.
* **Daily Loss Limit** – System warnings when a pre-set daily risk cap is hit.
* **Filtering & Sorting** – Review performance by specific tickers or sort by profit/loss.
* **Streak Tracking** – Monitor win/loss streaks to manage psychological state.
* **Alias Support** – Create short aliases for long ticker symbols.
* **Testing Mode** – Switch between "Backtest" and "Live" modes to separate datasets.

### [Version 3.0] - Advanced Analytics & Export
*Planned features for professional-grade review.*

* **Psychological Tagging** – Log emotional states to identify behavioral patterns.
* **Max Drawdown** – Automatic calculation of worst-case capital decline.
* **CSV Export** – Convert data to CSV for use in external tools like Excel.
* **Reflective Journaling** – Attach reflections and screenshots to each trade record.
* **Pre-trade Checklist** – Enforce plan consistency before saving entries.
* **Bulk Import** – Import historical trades for large-scale system testing.

---

## 4. Parameter Glossary

Before diving into the commands, here is a quick reference for the prefixes used in TradeLog. Commands that require parameters will use these prefixes to identify your data.

> 💡 **Tip:** Parameters can be typed in **any order**, as long as the correct prefix is attached to the value!

| Prefix | Name | Description & Valid Inputs |
| :--- | :--- | :--- |
| `t/` | **Ticker** | The symbol of the asset traded (e.g., `AAPL`, `BTC`, `EURUSD`). |
| `d/` | **Date** | The date the trade was taken. Format: `YYYY-MM-DD` (e.g., `2026-03-18`). |
| `dir/` | **Direction** | The direction of your trade. Valid inputs: `long` or `short`. |
| `e/` | **Entry Price** | The price at which you entered the trade. Must be a positive number. |
| `x/` | **Exit Price** | The price at which you closed the trade. Must be a positive number. |
| `s/` | **Stop Loss** | Your risk level. *Must be lower than Entry for longs, and higher than Entry for shorts.* |
| `o/` | **Outcome** | The result of the trade. Valid inputs: `win`, `loss`, `be` (break-even), or `open`. |
| `strat/`| **Strategy** | The name of the trading setup or system used (e.g., `Breakout`, `Trend`). |

---

## 5. Detailed Command Reference

### 5.1 Adding a Trade: `add`
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

### 5.2 Listing all Trades: `list`
Displays a numbered list of all trades currently stored in your TradeLog. This is useful for reviewing your recent activity and finding the `INDEX` number of a trade you want to edit or delete.

**Format:** `list`

**Expected Output:**

```text
Here are your logged trades:
1. AAPL | 2026-03-18 | Long | E:150 | TP:165 | SL:140 | win | Breakout
1. TSLA | 2026-03-18 | Long | E:150 | TP:165 | SL:140 | win | Breakout
```

### 5.3 Editing a Trade: `edit`
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

### 5.4 Deleting a Trade: `delete`
Permanently removes a trade from your log using its `INDEX` number. Use this to clean up accidental duplicate entries or test data.

**Format**: `delete INDEX`

**Example:**
`delete 2`

**Expected Output:**

```text
Trade deleted successfully:
TSLA | 2026-03-19 | Short | Entry: 200.0 | Exit: 205.0 | -0.50R | BE
```

### 5.5 Viewing Performance Metrics: `summary`
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

## 6. Command Summary

| Action           | Format                                                                                                    |
|:-----------------|:----------------------------------------------------------------------------------------------------------|
| **Add Trade**    | `add t/TICKER d/DATE dir/DIRECTION e/ENTRY x/EXIT s/STOP o/OUTCOME strat/STRATEGY`                        |
| **Edit Trade**   | `edit INDEX [t/TICKER] [d/DATE] [dir/DIRECTION] [e/ENTRY] [x/EXIT] [s/STOP] [o/OUTCOME] [strat/STRATEGY]` |
| **Delete Trade** | `delete INDEX`                                                                                            |
| **List Trades**  | `list`                                                                                                    |
| **Filter**       | `filter [-p] [t/TICKER] [strat/STRATEGY] [d/DATE]`                                                        |
| **Compare**      | `compare`                                                                                                 |
| **Summary**      | `summary`                                                                                                 |
| **Exit**         | `exit`                                                                                                    |

Accepted strategy shortcuts: `BB`, `TBF`, `PB`, `MTR`, `HOD`, `LOD`, `MR`, `TR`, `DB`, `DT`

## 6. Strategy Shortcuts

TradeLog expands common strategy abbreviations automatically before saving or filtering trades.

| Shortcut | Strategy               |
|:---------|:-----------------------|
| `BB`     | `Breakout`             |
| `TBF`    | `Trend Bar Failure`    |
| `PB`     | `Pullback`             |
| `MTR`    | `Major Trend Reversal` |
| `HOD`    | `High of Day`          |
| `LOD`    | `Low of Day`           |
| `MR`     | `Mean Reversion`       |
| `TR`     | `Trading Range`        |
| `DB`     | `Double Bottom`        |
| `DT`     | `Double Top`           |

Example:

`add t/AAPL d/2026-02-18 dir/long e/180 x/190 s/170 o/win strat/BB`

TradeLog stores and displays that strategy as `Breakout`.

## 7. Strategy Comparison

Use `compare` to view grouped performance metrics by strategy.

Example:

`compare`

Expected output format:

```text
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
---

## 8. FAQ

**Q: How do I transfer my data to another computer?**
**A:** TradeLog saves your trades when you exit the application. Simply copy the `data/` folder and place it in the same directory as the `TradeLog.jar` file on your new computer.

**Q: What happens if I enter an invalid date or negative price?**
**A:** TradeLog will display an error message and will not save the invalid entry.

---
