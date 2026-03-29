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
* **Strategy Comparison: `compare`** – View win rate, average win/loss, and EV grouped by strategy.

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

## 4. FAQ

**Q: How do I transfer my data to another computer?**
**A:** TradeLog saves your trades when you exit the application. Simply copy the `data/` folder and place it in the same directory as the `TradeLog.jar` file on your new computer.

**Q: What happens if I enter an invalid date or negative price?**
**A:** TradeLog will display an error message and will not save the invalid entry.

---

## 5. Command Summary

| Action           | Format                                                                                                    |
|:-----------------|:----------------------------------------------------------------------------------------------------------|
| **Add Trade**    | `add t/TICKER d/DATE dir/DIRECTION e/ENTRY x/EXIT s/STOP o/OUTCOME strat/STRATEGY`                        |
| **Edit Trade**   | `edit INDEX [t/TICKER] [d/DATE] [dir/DIRECTION] [e/ENTRY] [x/EXIT] [s/STOP] [o/OUTCOME] [strat/STRATEGY]` |
| **Delete Trade** | `delete INDEX`                                                                                            |
| **List Trades**  | `list`                                                                                                    |
| **Compare**      | `compare`                                                                                                 |
| **Summary**      | `summary`                                                                                                 |
| **Exit**         | `exit`                                                                                                    |
