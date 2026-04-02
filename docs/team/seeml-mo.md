# Pan Huangyu's Project Portfolio Page

## Overview
**TradeLog** is a Java-based CLI application for financial journaling. It replaces manual spreadsheets with a high-performance, keyboard-centric interface, ensuring data integrity through a localized, immediate-save architecture and automated risk metric calculations (e.g., Risk:Reward, ROI).

## Summary of Contributions
* **Code Contributed**: [RepoSense link](https://nus-cs2113-ay2526-s2.github.io/tp-dashboard/?search=&sort=groupTitle&sortWithin=title&timeframe=commit&mergegroup=&groupSelect=groupByRepos&breakdown=true&checkedFileTypes=docs~functional-code~test-code~other&since=2026-02-20T00%3A00%3A00&filteredFileName=&tabOpen=true&tabType=authorship&tabAuthor=seeml-mo&tabRepo=AY2526S2-CS2113-T11-2%2Ftp%5Bmaster%5D&authorshipIsMergeGroup=false&authorshipFileTypes=docs~functional-code~test-code&authorshipIsBinaryFileTypeChecked=false&authorshipIsIgnoredFilesChecked=false)

* **Enhancements Implemented**:
    * **Command Logic (v1.0 & v2.0)**: Implemented the core **`ListCommand`** for structured data retrieval and the initial **`EditCommand`**. In v2.0, refactored `EditCommand` to ensure **Atomicity**, guaranteeing that a `Trade` instance is only updated if all fields pass simultaneous validation, preventing inconsistent states from partial updates.
    * **Data Integrity & Model**: Designed a **Transaction-like validation flow** with exhaustive pre-update checks. Added essential data flow methods to `Trade` and `TradeList` while strictly adhering to the **KISS principle**.
    * **Quality Assurance**: Authored the **`EditCommandTest` suite**, including specialized **"All-or-Nothing"** assertions to verify that any failed part of a multi-field edit leaves the original object entirely unchanged.

* **Contributions to the User Guide (UG)**:
    * **Scaffolding**: Authored the **entire `.md` framework**, establishing the structure, TOC, and formatting standards that served as the baseline for all subsequent feature documentation.
    * **Core Documentation**: Drafted instructions for `list` and `edit` commands. Added a v2.0 **"Data Integrity" guide** to explain atomic update logic and validation triggers (e.g., non-zero price requirements).

* **Contributions to the Developer Guide (DG)**:
    * **Technical Framework**: Developed the **base `.md` skeleton** and chapter layout (Design, Implementation, Appendix) for the team.
    * **Logic Design**: Documented the **Transactional Edit Pattern** and architecture rationale for immediate-save data persistence during high-stress trading sessions.
    * **UML Diagrams**: Created a **Sequence Diagram** (Parser to `EditCommand` flow) documenting internal validation, and updated the **Class Diagram** to reflect Logic-Model associations for the final implementation.

* **Contributions to Team-Based Tasks**:
    * **Issue Tracker Standardization**: Established and enforced the project's **Issue and Milestone protocols**, defining required formats and ensuring tasks were properly linked before development to facilitate organized sprints.
    * **Global Documentation**: Authored the "Target User Profile", "Value Proposition", and "Getting Started" sections to provide a cohesive entry point.
    * **Workflow Optimization**: Integrated **Checkstyle** and custom linting rules to automate code quality (e.g., "Egyptian style" braces and explicit imports).

* **Review/Mentoring Contributions**:
    * **PR Reviews**: Conducted thorough reviews focused on **Naming Conventions** (noun-based commands) and **Standards Enforcement** (replacing magic numbers with `static final` constants). [PR #18](https://github.com/AY2526S2-CS2113-T11-2/tp/pull/18)
    * **Technical Support**: Resolved Gradle build conflicts and IntelliJ IDEA environment setup issues for team members.

* **Contributions Beyond the Project Team**:
    * **Forum Leadership**: Shared insights on **PPP formatting standards** and advised the cohort on **linking Issues to Milestones** before initiating PRs to improve project management practices across teams.