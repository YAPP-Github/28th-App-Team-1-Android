# PR Code Review Workflow

Procedural workflow for reviewing a pull request against this repository's product,
architecture, and process contracts. Any agent reading this repository (Claude, Codex, or
another automated workflow) can follow this document directly.

## 1. Scope and Priority

This is a workflow manual, not a policy document. Where a step here conflicts with
`docs/CONSTITUTION.md`, `docs/ARCHITECTURE.md`, or `AGENTS.md`, those documents prevail
(see `AGENTS.md` §1).

## 2. Inputs

- A PR number, branch name, or explicit diff range.
- Resolve the diff before reading anything else: `gh pr diff <PR#>` or
  `git diff <base>...<head>`.

## 3. Required Reading

Before commenting on any line:

1. `docs/CONSTITUTION.md`
2. `docs/ARCHITECTURE.md`
3. Every detailed document selected by the task routing table in `docs/ARCHITECTURE.md` §5,
   based on which modules or areas the diff touches.
4. `AGENTS.md`, particularly §5 (User Data) and §6 (Validation).
5. Existing source and tests in the touched modules, to judge whether the diff follows
   established patterns rather than introducing a new one (`AGENTS.md` §3).

## 4. Review Checklist

Evaluate the diff against every category below. If a category doesn't apply, say so
explicitly — an omitted category reads as "not checked," not "nothing found."

### 4.1 Correctness

- Logic errors, incorrect edge-case handling, off-by-one, incorrect state transitions.
- Concurrency/lifecycle issues (coroutine scope, ViewModel lifecycle, Compose recomposition).
- Cite a concrete input or state that triggers the failure — not a hypothetical.

### 4.2 Reuse / Simplification / Efficiency

- Duplicated logic that should reuse an existing utility, composable, or module.
- Unnecessary abstraction, premature generalization, or dead code introduced by the diff.
- Do not flag style already enforced by spotless/detekt — only what their config misses.

### 4.3 Repository Contract Compliance

- Module boundary violations (a change reaching into a module it shouldn't depend on).
- MVI, Navigation, Design System, or Catalog contract violations, per the documents read in
  §3.
- Catalog Preview/Story additions that pull in Hilt, Lifecycle, Navigation, network, file
  access, or real user data (`AGENTS.md` §4 forbids this).
- Test function names that are not Korean sentences describing expected behavior
  (`AGENTS.md` §4).

### 4.4 User Data Protection — stop condition, not just a finding

Trigger this section whenever the diff touches resumes, interview video or audio, STT
transcripts, feedback, reports, storage, retention, deletion, sharing, upload, external
transmission, logging, analytics, crash reporting, fixtures, sample assets, screenshots,
documentation, or prompt examples.

- Reread `docs/CONSTITUTION.md` §4 (User Data Protection) before evaluating these lines.
- Flag plaintext logging of sensitive data, real or identifying sample data in tests or
  fixtures, and any storage/retention/sharing behavior not traceable to an explicit policy
  decision.
- Report these as stop conditions, not suggestions, if the underlying policy is ambiguous or
  missing — do not infer a policy to fill the gap.

### 4.5 Validation

- Confirm whether the PR's CI (`spotlessCheck detekt testDebugUnitTest lintDebug
  assembleDebug`, per `.github/workflows/ci.yml`) is actually green — check the PR's status,
  don't assume.
- If CI is not green, or the diff changes Gradle/Catalog/Convention Plugins in a way CI
  wouldn't catch (e.g. `:catalog:compileKotlinWasmJs`, `:catalog:wasmJsBrowserDistribution`),
  state what still needs to run and by whom.

### 4.6 Protected Documents

If the diff modifies `docs/CONSTITUTION.md`, `docs/ARCHITECTURE.md`, `docs/architecture/**`,
`AGENTS.md`, or `README.md`, confirm the PR description states this was a user-requested,
explicitly accepted change (`AGENTS.md` §3.1). If it doesn't say so, flag it — do not assume
authorization.

## 5. Output Format

Write the review to `review.md` at the repository root, overwriting any previous run's
file. Report findings as a flat list, ordered by severity (상 → 중 → 하). For each finding,
give:

- `file:line`
- A one-sentence summary of the defect.
- The concrete failure scenario (input/state → wrong output or crash).
- Category: `correctness | simplification | efficiency | compliance | user-data |
  validation | protected-doc`.
- Severity: `상 | 중 | 하` (see §5.1).

Close with a short verdict — approve / approve-with-comments / request-changes — and the
one or two reasons driving it.

### 5.1 Severity Scale

- **상** — critical: the code doesn't run (crash, build failure, broken core flow).
- **중** — runs, but causes mid-to-long-term harm — maintainability cost, hidden bugs,
  architecture drift.
- **하** — minor, no functional or maintainability impact (naming, minor style).

## 6. Stop Conditions

Stop and ask rather than guessing when:

- The diff's data handling can't be evaluated without a product decision that isn't
  documented anywhere.
- The diff crosses a module or architecture boundary and it's unclear whether that's an
  approved target-architecture step or a violation.
