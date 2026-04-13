# Ranganathan's Project Portfolio Page

## Project: TradeLog

TradeLog is a desktop application for traders who prefer using a CLI. It allows users to log trades, review performance in `R` multiples, compare strategy results, and store their data in password-protected local profiles.

### Features implemented

1. **List/display workflow and shared UI**: implemented `ListCommand`, the `Command` abstraction, key parts of the `TradeLog` run loop, and the shared `Ui` used for trade display, summaries, undo feedback, and errors. This established the product's common CLI output style, centralized formatting logic, and later included bug-fix work around output formatting, startup handling, and run-loop reliability. Relevant PRs: [#9](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/9), [#10](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/10), [#19](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/19), [#25](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/25), [#26](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/26), [#27](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/27), [#129](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/129), [#136](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/136).
2. **Strategy shortcut support**: implemented canonical parsing and validation for shortcut inputs such as `BB`, `PB`, and `MTR`, and integrated them into `add`, `edit`, `filter`, and related tests. This improved logging speed while keeping stored strategy names normalized for filtering, comparison, and future maintenance. Relevant PR: [#44](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/44).
3. **Strategy comparison analytics**: implemented `compare` using `CompareCommand` and `StrategyStats` to show per-strategy trade count, win rate, average win, average loss, and EV. The implementation uses single-pass aggregation and canonicalized strategy names so variants do not split into separate groups, and it is supported by targeted tests and documentation. Relevant PR: [#46](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/46).

### Code contributed

[RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=RANGANATHAN21&tabRepo=AY2526S2-CS2113-T11-2%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code~other&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

### Documentation

- **User Guide**: reworked the UG into an AB3-style structure, updated command documentation, and kept the sections for strategy shortcuts, `filter`, `compare`, encryption, and startup/save behavior aligned with the product.
- **Developer Guide**: documented the UI component, `ListCommand`, the strategy shortcut workflow, the `compare` feature, and the manual testing steps for the implemented features.
- **Project Portfolio Page**: updated my PPP to reflect the final implemented scope and linked PR history.
- These updates were aimed at keeping the docs aligned with the actual product instead of planned or outdated behavior, which reduced mismatch bugs during peer testing.

### Diagrams

Added or updated: `ui-print-trade-list-sequence`, `list-command-sequence`, `list-command-logic-diagram`, `strategy-shortcut-add-sequence`, `compare-sequence`, `compare-class-diagram`, `compare-object-diagram`.

### Team-based tasks

- Helped maintain consistent CLI output, documentation, and test coverage across the project.
- Contributed project setup, documentation, release-readiness, and integration work in [#3](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/3), [#30](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/30), [#31](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/31), [#34](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/34), [#36](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/36), [#39](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/39), [#62](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/62), and [#136](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/136).
- Took part in final bug-fix and verification work to reduce peer-testing issues in the user-facing parts of the app.

### Review/mentoring contributions

- Reviewed teammates' changes during integration, helped catch behavior-documentation mismatches, and approved PRs such as [#11](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/11), [#16](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/16), [#17](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/17), [#21](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/21), [#28](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/28), [#32](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/32), [#35](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/35), [#43](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/43), [#59](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/59), [#60](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/60), [#77](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/77), [#132](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/132), and [#134](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/134).
