# Agent Instructions

This document defines execution rules for AI coding agents working in the D-14 Android repository.
It applies to Codex, Antigravity, Claude, and any other automated workflow that reads this
repository.

This document does not define product or architecture policy. Those contracts are owned by
`docs/CONSTITUTION.md`, `docs/ARCHITECTURE.md`, and the detailed documents routed from the
Architecture entry point.

## 1. Document Priority

Interpret repository documents in this order:

1. `docs/CONSTITUTION.md`
2. `docs/ARCHITECTURE.md` and the detailed Architecture documents it incorporates
3. `AGENTS.md`
4. `README.md`

When documents conflict, the higher-priority document prevails. Detailed Architecture documents
must be discovered through `docs/ARCHITECTURE.md`; do not build a second routing table here.

Use each document for its intended role:

- `docs/CONSTITUTION.md`: non-negotiable product, data, and architecture principles
- `docs/ARCHITECTURE.md`: single Architecture entry point and task-based reading routes
- `docs/architecture/*.md`: detailed Architecture contracts incorporated by the entry point
- `AGENTS.md`: agent scope, permissions, validation, reporting, and stop procedures
- `README.md`: public-facing service overview

`docs/ARCHITECTURE_OLD.md` is a permanent, non-authoritative snapshot from before the Architecture
documentation split. Do not use it as the current implementation contract.

## 2. Required Reading

Before modifying code, read:

1. `docs/CONSTITUTION.md`
2. `docs/ARCHITECTURE.md`
3. every detailed Architecture document selected by its task routing table
4. relevant source files
5. relevant tests
6. relevant build files when changing dependencies, Gradle, modules, CI, Catalog, or Convention
   Plugins
7. relevant Feature specifications under `specs/` when present for active implementation work

When a task spans multiple Architecture areas, read every applicable detailed document. Reread the
Constitution and relevant detailed contracts before changing architecture, module boundaries,
Navigation, Design System, Catalog, error handling, user data, storage, networking, logging, AI, or
media processing.

`README.md` is required only for service description, public documentation, Feature positioning, or
repository presentation work.

The Architecture documents describe an approved target. A target module may be absent or only
partially implemented during staged development. Do not weaken a target contract to match bootstrap
code.

## 3. Work Scope

- Keep changes within the user-requested scope.
- Do not perform unrelated refactoring, formatting, renaming, dependency updates, module moves, or
  documentation edits.
- Preserve public behavior unless the user requests a behavior change.
- Follow existing source, naming, module, and test patterns before introducing a new pattern.
- Preserve user changes in a dirty worktree and do not revert or include unrelated changes.
- Do not modify generated files, build output, local machine files, IDE files, or secret
  configuration unless explicitly requested and repository-appropriate.
- Read-only requests do not authorize file, Git index, external service, or environment changes.
- A commit request does not authorize push.

### 3.1 Document Edit Permissions

| Path | Permission |
|---|---|
| `docs/CONSTITUTION.md` | Do not modify unless the user explicitly requests the change and explicitly accepts the proposed modification. |
| `docs/ARCHITECTURE.md` | Modify only for a user-requested Architecture or Architecture-document change. |
| `docs/architecture/**` | Apply the same permission boundary as `docs/ARCHITECTURE.md`. |
| `docs/ARCHITECTURE_OLD.md` | Permanent read-only snapshot. Do not modify. |
| `AGENTS.md` | Modify only for a user-requested agent-instruction update. |
| `README.md` | Do not modify; the user edits this file directly. |
| code and tests | Modify within the requested implementation scope. |

If a task requires a protected document change that is not authorized, stop and report it instead
of editing silently.

## 4. Implementation Workflow

Before implementation:

1. Identify the modules and contracts affected by the request.
2. Use the routing table in `docs/ARCHITECTURE.md` to select detailed documents.
3. Inspect relevant source, tests, and build configuration.
4. Verify imports, module dependencies, DI bindings, Gradle dependencies, and module references
   against the selected contracts.
5. Stop rather than guessing if product behavior, data policy, a token selection, or an architecture
   boundary is ambiguous.

During implementation:

- Keep each change in the module that owns the responsibility.
- Do not add a dependency merely because it makes a local implementation easier.
- Preserve MVI, Navigation, Design System, Catalog, error, and build contracts by following their
  detailed Architecture documents.
- Keep Preview and Catalog examples free of Hilt, Lifecycle, Navigation, network, file access, and
  real user data.
- Test function names must be Korean sentences that describe the expected behavior.

When adding a Catalog Controls adapter, generated Args and Controls become available through Wasm
KSP compilation. Run the following when generated declarations are needed by the IDE:

```text
./gradlew :catalog:compileKotlinWasmJs
```

The adapter and Story may be authored together and resolved by the same compilation; a separate
intermediate build is not mandatory.

## 5. User Data

Treat user-data rules as stop conditions, not implementation details to infer.

Before work involving resumes, interview video or audio, STT transcripts, feedback, reports,
storage, retention, deletion, sharing, upload, external transmission, logging, analytics, crash
reporting, fixtures, sample assets, screenshots, documentation, or prompt examples, reread the User
Data Protection section of `docs/CONSTITUTION.md`.

- Do not decide storage, transmission, sharing, logging, retention, deletion, access, or external
  processing policy on behalf of the user.
- Stop when any required sensitive-data policy is incomplete or ambiguous.
- Do not use real user data in source, tests, fixtures, screenshots, Catalog Stories,
  documentation, examples, prompts, CI logs, build logs, analytics, or crash reports.
- Do not log sensitive user data in plaintext.
- Synthetic samples must be non-identifying and appropriate for the requested task.

## 6. Validation

After code changes, run the most relevant available validation command. The full CI validation is:

```text
./gradlew --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug
```

When Catalog or Design System changes affect Web/WASM output, also run:

```text
./gradlew :catalog:wasmJsBrowserDistribution
```

A targeted command may be run first when appropriate, but the completion report must state whether
the full CI command ran.

For documentation-only changes, run at least:

```text
git diff --check
```

Also inspect changed Markdown links and stale path or section references. Do not run Gradle only to
validate prose unless the documentation change also modifies or claims build behavior that requires
it.

If validation cannot run, report:

- the command that should have run
- why it did not run
- the remaining risk
- whether the work is unvalidated, partially validated, or fully validated

Never claim a check passed unless the command completed successfully. Do not hide failures. Fix a
failure within scope or stop if the fix would exceed scope or violate a higher-priority contract.

## 7. Reporting

Completion reports must be concise and include:

- summary of changes
- files changed
- validation commands and actual results
- skipped checks and reasons
- unresolved risks, assumptions, or required follow-up decisions

Use Korean when requested; otherwise match the working context. If a change affects architecture,
module dependencies, Navigation, Design System, Catalog, error handling, sensitive data, storage,
logging, networking, AI, or media processing, state the risk area and how the change remained
compliant.

If no files changed, say so directly. Do not overstate partial or unvalidated work.

## 8. Stop Conditions

Stop and request clarification when:

- the task conflicts with the Constitution or selected Architecture contracts
- implementation requires weakening a module or dependency boundary
- a protected document change lacks authorization
- sensitive-data policy is incomplete or ambiguous
- product behavior is too ambiguous to implement safely
- a Design System specification does not identify the product token to use
- validation results cannot be interpreted safely
- fixing a failure would require unrelated or unauthorized work

When stopping, report the blocking condition, exact missing decision or conflict, safest options,
and affected files or rules. Do not proceed by guessing.
