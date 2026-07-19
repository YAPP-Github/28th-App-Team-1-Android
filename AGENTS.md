# Agent Instructions

This document defines execution rules for AI coding agents working in the D-14 Android repository.
It applies to Codex, Antigravity, Claude, and any other automated coding workflow that reads this
repository.

This document does not define product principles or architecture rules. Those rules are defined by
`docs/CONSTITUTION.md` and `docs/ARCHITECTURE.md`.

---

## 1. Purpose

AI coding agents must use this document as the operational guide for repository work.

The purpose of this document is to define:

- which documents must be read before making changes;
- how agents must limit work scope;
- how agents must apply architecture and module rules during implementation;
- how agents must handle sensitive user data tasks;
- which validation commands must be used;
- how agents must report completion, skipped checks, and unresolved risks;
- when agents must stop and request clarification.

Agents must not treat this document as permission to override `docs/CONSTITUTION.md` or
`docs/ARCHITECTURE.md`.

---

## 2. Document Priority

Documents must be interpreted in the following priority order:

1. `docs/CONSTITUTION.md`
2. `docs/ARCHITECTURE.md`
3. `AGENTS.md`
4. `README.md`

When documents conflict, the higher-priority document prevails.

Use each document for its intended role:

- `docs/CONSTITUTION.md` defines non-negotiable project rules and prohibited changes.
- `docs/ARCHITECTURE.md` defines architecture, module responsibilities, dependency rules, MVI,
  Navigation 3, design system, catalog, error handling, and project structure.
- `AGENTS.md` defines agent execution rules.
- `README.md` defines the public-facing service overview.

---

## 3. Required Reading

Before modifying code, agents must read:

1. `docs/CONSTITUTION.md`
2. `docs/ARCHITECTURE.md`
3. relevant source files
4. relevant tests
5. relevant build files when changing dependencies, Gradle configuration, modules, CI, catalog,
   or convention plugins
6. relevant feature specifications under `specs/` when that directory exists for active
   implementation work

`README.md` is not required for every code task. Read it when the task concerns service description,
public-facing documentation, feature positioning, or repository presentation.

Before changing architecture, module boundaries, navigation, design system, catalog, error handling,
user data behavior, storage, networking, logging, or AI/media processing behavior, agents must
reread
`docs/CONSTITUTION.md` and the relevant section of `docs/ARCHITECTURE.md`.

`docs/ARCHITECTURE.md` describes the approved target architecture. During bootstrap or staged
implementation, modules documented there may be absent from the current Gradle build. Treat those
absences as planned future work unless current repository code contradicts a Constitution or
Architecture rule.

---

## 4. Work Scope

Agents must keep changes within the user-requested scope.

Agents must not perform unrelated refactoring, formatting, renaming, dependency updates, module
reorganization, or documentation edits unless the user explicitly requests them.

Agents must preserve existing public behavior unless the user explicitly requests a behavior change.

Agents must follow existing code style, module structure, naming patterns, and test patterns before
introducing new patterns.

Agents must not change generated files, build outputs, local machine files, IDE files, or secret
configuration unless the user explicitly requests it and the change is repository-appropriate.

### 4.1 Document edit permissions

Agents must follow these document permissions:

| Path                   | Permission                                                                                                          |
|------------------------|---------------------------------------------------------------------------------------------------------------------|
| `docs/CONSTITUTION.md` | Must not be modified unless the user explicitly requests a change and explicitly accepts the proposed modification. |
| `docs/ARCHITECTURE.md` | May be modified only when the user requests an architecture change or architecture document update.                 |
| `README.md`            | Must not be modified by agents. The user edits this file directly.                                                  |
| `AGENTS.md`            | May be modified only when the user requests an agent instruction update.                                            |
| code and tests         | May be modified within the requested task scope.                                                                    |

If a task requires changing a protected document, agents must stop and report the required document
change instead of applying it silently.

---

## 5. Implementation Rules

Agents must implement changes according to `docs/CONSTITUTION.md` and `docs/ARCHITECTURE.md`.

### 5.1 Architecture and dependency checks

Before adding an import, module dependency, DI binding, Gradle dependency, or module reference,
agents must verify that the change does not violate project dependency rules.

Agents must enforce these checks during implementation:

- `feature:*` must not depend on `data`.
- `feature:*` must not depend on `app`.
- `feature:*:impl` must not depend on another feature's `impl` module.
- `feature:*:impl` may depend on another feature's `api` module for route or entry contracts only.
- `domain` must not depend on Android Framework APIs.
- `domain` must not depend on `data` or `feature:*`.
- `data` must not depend on `feature:*` or `app`.
- `designsystem` must not depend on Android Framework APIs, Hilt, Android Navigation,
  Android Lifecycle APIs, Android resource APIs, `feature:*`, or `app`.
- `catalog` must not depend on Android Framework APIs or `app`.
- A separate `:navigation` module must not be introduced.
- A separate `:feature:navigator` module must not be introduced.

`app` may depend on `data` only for composition root and Hilt binding purposes. Agents must not use
that permission to place business logic or direct repository implementation usage in `app`.

### 5.2 Navigation rules

The project uses Navigation 3.

Agents must keep app-level Navigation 3 assembly in `app`.

Feature modules may define route or entry contracts and expose them upward. Feature modules must not
perform app-level navigation assembly.

ViewModels must not execute navigation directly. ViewModels must emit navigation effects, and the UI
layer must perform navigation execution.

### 5.3 MVI rules

When modifying feature UI, agents must preserve MVI structure.

Agents must follow these rules:

- User actions and lifecycle-triggered events enter the ViewModel through `Intent`.
- Persistent UI data is represented as `State`.
- One-time events such as navigation, Toast, Snackbar, and dialog requests are represented as
  `Effect` or a global app event.
- State updates use immutable state-copying semantics.
- ViewModel-connected `Screen` logic remains separated from ViewModel-free `Content` UI.
- Previewable or catalog-exposed UI must not require Hilt, Android Lifecycle, Android Navigation,
  or a real ViewModel instance.

### 5.4 Design system and catalog rules

When modifying `designsystem`, agents must keep all shared UI Compose Multiplatform-compatible.

Agents must not introduce Android-only APIs into `designsystem`, including `Context`, `Activity`,
`Intent`, Toast calls, Android resource access, Android Lifecycle APIs, Android Navigation, Hilt, or
Android platform-specific side effects.

Platform-specific behavior required by shared UI must be represented through platform-independent
state or callbacks and handled by `app` or `feature:*`.

When modifying reusable UI, agents must consider whether a catalog story must be updated. If a story
is not updated, the completion report must explain why.

When modifying `catalog`, agents must keep it as a design system review and communication artifact.
`catalog` must not include Android app runtime logic, real API calls, Hilt ViewModels, Android
Lifecycle dependencies, Android Navigation, or product runtime behavior.

Catalog stories must use `designsystem` components or Android-independent `Content`-level UI.

### 5.5 Error handling rules

When implementing error handling, agents must follow the project error policy.

Agents must enforce these rules:

- Client errors are handled by the relevant feature through feature `State` or feature `Effect`.
- Network errors are routed through the global app event mechanism.
- Server errors are routed through the global app event mechanism.
- Unknown errors are routed through the global app event mechanism.
- `domain` must not determine UI presentation for errors.
- `data` must convert or propagate external exceptions according to the project error model.
- `app` collects global app events and renders app-level Dialog, Toast, or Snackbar UI.

Agents must not invent a new error handling mechanism when the existing global event policy applies.

### 5.6 Build and dependency rules

Gradle Convention Plugins are the standard mechanism for shared build configuration.

Agents must not duplicate shared Gradle configuration across modules when the same rule belongs in
`build-logic`.

Agents must not add a dependency only because it makes a local implementation easier.

Dependencies affecting architecture boundaries, user data handling, media processing, AI
integration, networking, logging, analytics, or storage require explicit justification in the
completion report.

Convention Plugins must follow the repository responsibility model:

- Base plugins own only platform and compiler defaults.
- Capability plugins own one feature such as Compose, Preview, shared resources, Hilt, Navigation,
  or testing.
- Quality leaf plugins own one tool, while quality bundle plugins only compose leaf plugins and
  task ordering.
- Composite plugins must not repeat DSL configuration or dependencies owned by their child plugins.
- `dminus14.android.feature` is the standard composite plugin for `:feature:*:impl` only.
- All repository modules must apply the platform-appropriate Kotlin or Android quality plugin.

Preview and shared-resource capability plugins have restricted targets:

- Apply `dminus14.compose.preview` only to `:app`, `:feature:*:impl`, and `:designsystem`.
- Preview functions must render ViewModel-free UI and must not require Hilt, Lifecycle, Navigation,
  network, file access, or real user data.
- Apply `dminus14.compose.resources` only to `:app`, `:feature:*:impl`, and `:designsystem`.
- Do not apply Preview or shared-resource capability plugins to `:catalog` or `:core:resources`.
- `:catalog` must keep catalog-only resources and must not depend directly on `:core:resources`.

Android test conventions are separated by responsibility. Use `dminus14.android.test` for the
general JUnit/AndroidX test stack and add `dminus14.android.compose.test` only when the module uses
Compose UI tests.

---

## 6. User Data Rules

Agents must treat user data rules as stop-condition rules, not as implementation details to infer.

When a task touches user media, PDF resumes, interview video, interview audio, STT transcripts,
interview feedback, reports, storage, retention, deletion, sharing, upload, external transmission,
logging, analytics, crash reporting, test fixtures, sample assets, screenshots, documentation, or
prompt examples, agents must consult `docs/CONSTITUTION.md` before making changes.

Agents must not decide sensitive data behavior on behalf of the user.

Agents must stop and request clarification when storage, transmission, sharing, logging, retention,
deletion, access, or external processing policy is incomplete or ambiguous.

Agents must not use real user data in source code, tests, fixtures, screenshots, catalog stories,
documentation, examples, prompts, CI logs, build logs, analytics events, or crash reports.

Agents must not log sensitive user data in plaintext.

Agents must not add sample sensitive data unless it is clearly synthetic, non-identifying, and
appropriate for the requested task.

---

## 7. Validation

After code changes, agents must run the most relevant available validation command.

The CI validation command is:

```bash
./gradlew --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug
```

When `catalog` or `designsystem` changes affect the Web/WASM catalog, agents must also run:

```bash
./gradlew :catalog:wasmJsBrowserDistribution
```

When only a narrower validation is appropriate, agents may run a targeted command first. However,
the completion report must state whether the full CI validation command was run.

If validation cannot be run, agents must report:

- the command that should have been run;
- why it was not run;
- what risk remains;
- whether the change is unvalidated, partially validated, or fully validated.

Agents must not claim that validation passed unless the relevant command completed successfully.

Agents must not hide failing checks. If a check fails, agents must report the failure and either fix
it within scope or stop when the fix would exceed scope or violate a higher-priority document.

---

## 8. Reporting

Completion reports must be concise and must include:

- summary of changes;
- files changed;
- validation commands and results;
- skipped checks and reasons;
- unresolved risks, assumptions, or required follow-up decisions.

When the user requests Korean, report in Korean. Otherwise, use the language that best matches the
working context.

When a change affects architecture, module dependencies, navigation, design system, catalog, error
handling, sensitive data behavior, storage, logging, networking, AI integration, or media
processing,
the report must explicitly mention the relevant risk area and how the change stayed compliant.

If no files were changed, the report must say so directly.

Agents must not overstate completion. Partial work must be reported as partial work.

---

## 9. Stop Conditions

Agents must stop and request clarification when any of the following conditions occurs:

- The task conflicts with `docs/CONSTITUTION.md`.
- The task conflicts with `docs/ARCHITECTURE.md` and cannot be implemented without changing the
  architecture.
- The task requires weakening architecture boundaries.
- The task requires adding a prohibited module dependency.
- The task requires creating a separate `:navigation` module.
- The task requires creating a separate `:feature:navigator` module.
- The task requires moving app-level Navigation 3 assembly out of `app`.
- The task requires adding Android dependencies to `designsystem` or `catalog`.
- The task requires modifying `docs/CONSTITUTION.md`.
- Sensitive data storage, transmission, sharing, logging, retention, deletion, access, or external
  processing policy is unclear.
- Product behavior or requirements are too ambiguous to implement without human clarification.
- Validation results cannot be interpreted safely.
- Fixing a validation failure would require work outside the requested scope.

When work stops, agents must report:

- the blocking condition;
- the exact missing decision or conflict;
- the safest available options, if any;
- the files or rules involved.

Agents must not proceed by guessing when a stop condition applies.
