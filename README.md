# TradeLog

TradeLog is a desktop application for traders who prefer using a Command Line Interface (CLI). It allows users to log trades, review performance in `R` multiples, compare strategy results, and store their data in encrypted local profiles.

## Quick Start

1. Ensure that you have Java 17 or above installed.
2. Run the application with:

```bash
java -jar TradeLog.jar
```

3. Enter a password to create or load a profile.

## Main Features

- Add, list, edit, and delete trades
- Filter trades by ticker, strategy, and date
- View overall performance summaries
- Compare performance across strategies
- Undo the most recent add, edit, or delete
- Use supported strategy shortcuts such as `BB`, `PB`, and `MTR`, with canonical strategy normalization
- Store data in encrypted files under the `data/` directory

## Build and Test

- Build the runnable jar: `.\gradlew shadowJar`
- Run JUnit tests: `.\gradlew test`
- Run text UI regression tests from `text-ui-test/`

## Documentation

- [User Guide](docs/UserGuide.md)
- [Developer Guide](docs/DeveloperGuide.md)
- [About Us](docs/AboutUs.md)
