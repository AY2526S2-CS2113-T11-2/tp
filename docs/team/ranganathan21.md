# Ranganathan's Project Portfolio Page

## Project: TradeLog

TradeLog is a desktop application for traders who prefer using a CLI. It allows users to log trades, review performance in `R` multiples, compare strategy results, and store their data in encrypted local profiles.

Given below are my contributions to the project.

### Features implemented

1. Added the list/display workflow and core UI behavior.

What it does: provides the `list` command and the full shared UI layer used to present trades, summaries, comparison output, undo feedback, and error messages in a consistent CLI format.

Justification: This was foundational to the product because nearly all user-facing features depend on clear, consistent output and a stable application flow.

Highlights: I implemented `ListCommand`, the `Command` abstraction, key parts of the `TradeLog` run loop, and the `Ui` class. This work also included defensive assertions, logging, and test coverage for `Ui`, `ListCommand`, and related command behavior.
Relevant PRs: [#9](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/9), [#10](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/10), [#19](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/19), [#25](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/25)

2. Added strategy shortcut support.

What it does: allows users to enter supported short strategy codes such as `BB`, `PB`, and `MTR`, or supported full strategy names, which are validated and normalized into canonical stored strategy names.

Justification: This improves the product because frequent users can log trades faster while the app still stores normalized strategy names for filtering and comparison.

Highlights: I implemented this in `ParserUtil` and integrated it into the parsing flow used by `add`, `edit`, and `filter`, while also ensuring `compare` groups known case variants under the same canonical strategy name. I updated the welcome UI to display the supported shortcuts, and added regression and unit tests for the shortcut behavior.
Relevant PR: [#44](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/44)

3. Added strategy comparison analytics.

What it does: adds a `compare` command that groups trades by strategy and shows trade count, win rate, average win, average loss, and EV for each strategy.

Justification: This improves the product because traders need to compare strategies directly instead of relying only on an overall summary.

Highlights: I implemented this through `CompareCommand` and `StrategyStats` using a single-pass aggregation approach. The feature also required parser routing, UI rendering, tests, and documentation updates.
Relevant PR: [#46](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/46)

### Code contributed

[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=RANGANATHAN21&tabRepo=AY2526S2-CS2113-T11-2%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

### Documentation

#### User Guide:

- Reworked the User Guide into an AB3-style structure.
- Documented the current command format and feature set more precisely.
- Updated the sections for strategy shortcuts, `filter`, and `compare`.
- Marked unimplemented features clearly as `Coming soon`.

#### Developer Guide:

- Added and updated documentation for the UI component and `ListCommand`.
- Documented the strategy shortcut workflow and the strategy comparison feature.
- Updated the manual testing section so implemented v1 and v2 features are covered clearly.
- Cleaned up outdated DG wording so it matches the current product behavior.

### Diagrams

Added or updated the following UML diagrams:

- `ui-print-trade-list-sequence`
- `list-command-sequence`
- `list-command-logic-diagram`
- `strategy-shortcut-add-sequence`
- `compare-sequence`
- `compare-class-diagram`
- `compare-object-diagram`

### Team-based tasks

- Helped maintain consistent CLI output and command flow across user-facing features.
- Helped align the UG and DG with the implemented behavior as the product evolved.
- Contributed to test coverage for both unit tests and text UI regression tests.
- Contributed documentation and release-readiness updates in [#30](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/30), [#34](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/34), [#36](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/36), [#39](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/39) and [#62](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/62).

### Review/mentoring contributions

- Reviewed teammates' changes during integration and helped catch behavior-documentation mismatches.
- Helped refine examples, manual test steps, and feature descriptions so they matched the actual implementation more closely.
- Reviewed and/or approved pull requests such as [#11](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/11), [#16](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/16), [#17](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/17), [#21](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/21), [#28](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/28), [#32](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/32), [#35](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/35), [#43](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/43), [#59](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/59), and [#60](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/60).
