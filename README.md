# TradeLog

TradeLog is a desktop application for traders who prefer using a Command Line Interface (CLI). It allows users to log trades, review performance in `R` multiples, compare strategy results, and store their data in password-protected local profiles.

## Quick Start

1. Ensure that you have Java 17 or above installed.
2. Run the application with:

```bash
java -jar TradeLog.jar
```

3. Enter a password to create or load a profile.

## Main Features

- Add, list, edit, and delete trades
- Filter trades by ticker or strategy
- View overall performance summaries
- Compare performance across strategies
- Undo the most recent add, edit, or delete
- Switch between `BACKTEST` and `LIVE` modes
- Use supported strategy shortcuts such as `BB`, `PB`, and `MTR`, with canonical strategy normalization
- Store data under the `data/` directory, with optional encryption controlled by the user

## Build and Test

- Build the runnable jar: `.\gradlew shadowJar`
- Run JUnit tests: `.\gradlew test`
- Run text UI regression tests from `text-ui-test/`

## Documentation

- [User Guide](docs/UserGuide.md)
- [Developer Guide](docs/DeveloperGuide.md)
- [About Us](docs/AboutUs.md)
