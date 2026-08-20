# Branch Self-Review and PR Request Workflow

Procedural workflow for reviewing the current branch's changes against `develop` and
preparing a pull request. Any agent reading this repository (Claude, Codex, or another
automated workflow) can follow this document directly.

## 1. Scope and Priority

This is a workflow manual, not a policy document. Where a step here conflicts with
`docs/CONSTITUTION.md`, `docs/ARCHITECTURE.md`, or `AGENTS.md`, those documents prevail
(see `AGENTS.md` §1).

## 2. Preconditions

- Confirm the current branch is not `develop` (the main branch per `AGENTS.md`).
- Run `git status` and `git diff develop...HEAD` (or the actual base branch) — never assume
  the diff without checking real state.
- If the working tree has unrelated dirty state, do not fold it into this PR; leave it as
  found (global instruction: preserve user changes in a dirty worktree).

## 3. Self-Review Pass

Apply [`pr-review.md`](./pr-review.md) §4 (Review Checklist) to `git diff develop...HEAD` —
every category, including User Data Protection and Protected Documents. Reviewing your own
branch is not a reason to skip a category.

## 4. Validation

Run the full CI command (`AGENTS.md` §6):

```bash
./gradlew --stacktrace --continue spotlessCheck detekt testDebugUnitTest lintDebug assembleDebug
```

If Catalog or Design System changes affect Web/WASM output, also run
`./gradlew :catalog:wasmJsBrowserDistribution`.

Report actual pass/fail — never claim a check passed without running it (`AGENTS.md` §6). If
only a targeted subset ran instead of the full command, say so explicitly in the PR
description and in any completion report.

## 4.1 Loop Until Clean

Treat §3 and §4 as one loop, not a single pass:

1. Run §3 (self-review). If it surfaces a correctness, compliance, or user-data finding
   that must be fixed before merge, fix it within the branch's existing scope.
2. Run §4 (validation). If any command fails, fix the failure within scope.
3. If either step changed any file, go back to step 1.
4. Stop looping only when one full pass produces no blocking findings in §3 and every
   validation command in §4 passes — or when a §8 stop condition applies (the fix would
   exceed scope), in which case stop and report instead of looping indefinitely.

Do not proceed to §5 while §3 or §4 is still failing.

## 5. Commit Hygiene

- Confirm each commit's staged files match its stated intent (`git status`,
  `git diff --staged`).
- Confirm no secrets, credentials, or local machine files are included.
- Do not amend or force-push existing commits without an explicit user request.

### 5.1 Commit Message Format

Every commit message must follow `label: message`:

- `label` — exactly one of:
  - `docs` — documentation work
  - `feat` — new feature development
  - `refactor` — code change with no behavior change
  - `fix` — bug fix
  - `chore` — non-development work (file moves/deletions, dependency changes, etc.)
  - `test` — test code
  - `design` — UI changes
- `message` — a summary of what the commit actually contains.

No other label may be used. If a change doesn't fit one label, split it into multiple
commits rather than inventing a label or combining two.

### 5.2 Commit Size

Keep each commit to 10 files or fewer where practical, and never exceed 15 files. If the
branch's diff is larger than that, split it into multiple commits along logical boundaries
before moving to §6.

## 6. PR Body Assembly

Use `.github/PULL_REQUEST_TEMPLATE.md` verbatim as the section structure:

- `# 🚩 연관 이슈` — `closed #<issue>` if the branch or commit history references an issue
  number. This repo's commit convention is `type(#issue): description`.
- `# 📝 작업 내용` — summarize the actual diff, not the original task request.
- `# 🏞️ 스크린샷 (선택)` — fill in only if the diff touches UI; otherwise keep the heading
  with no content.
- `# 🗣️ 리뷰 요구사항 (선택)` — call out anything from §3/§4 the reviewer should double-check
  (e.g. a checklist category that couldn't be fully validated).

### 6.1 PR Title Format

Every PR title must follow `tag/#num title`:

- `tag` — same allowed values as the commit `label` in §5.1 (`docs | feat | refactor | fix |
  chore | test | design`).
- `#num` — the issue number the PR closes (the number used in §6's "연관 이슈" section).
- `title` — a short summary of the PR, in the same language as the rest of the PR body.

Example: `design/#174 면접 준비 UI와 PR 워크플로 개선`.

If the issue number can't be determined, that is the §8 stop condition — don't invent one
to satisfy the format.

## 7. Push and PR Creation — Confirmation Gate

Creating a PR requires pushing the branch. A request to "review changes and request a PR"
does not itself authorize the push (`AGENTS.md` §3: a commit request does not authorize
push). Before pushing or creating the PR:

- State the target branch, the commits/diff about to be pushed, and the base branch
  (`develop`) the PR will target.
- Get explicit user confirmation for this specific push, given after the actual diff and
  validation results are known — not before.

Once confirmed:

1. `git push` the branch (set upstream if needed).
2. Create the PR with `gh pr create --base develop --title "<tag/#num title>" --body
   "<§6 body>"`, where the title follows §6.1.
3. If `gh` is unavailable — not installed, not authenticated, or the command fails for a
   reason unrelated to the PR content — do not treat that as a workflow failure. Instead,
   write the §6.1 title followed by the assembled §6 body to `PR.md` at the repository root,
   and report that the PR could not be created automatically; the user (or Codex, in a
   `gh`-enabled environment) creates it from `PR.md`.

## 8. Stop Conditions

- Full CI validation fails and the fix would exceed the branch's original scope.
- The issue number can't be determined from the branch or commit history and the PR
  template requires it.
- The diff includes changes that belong in a separate PR (unrelated scope).
