---
name: hilit-pull-request
description: >
  Self-review the current branch's changes against develop, validate, and prepare/create a
  pull request, following docs/workflows/pull-request.md. Use when the user asks to review
  branch changes and open a PR, request a PR, or invokes /hilit-pull-request.
---

Follow `docs/workflows/pull-request.md` exactly, start to finish: preconditions (§2), the
self-review + validation loop (§3–§4.1 — repeat until one full pass is clean), commit
hygiene and the `label: message` commit format (§5), PR body assembly from
`.github/PULL_REQUEST_TEMPLATE.md` (§6), and the confirmation gate before push/PR creation
(§7).

Do not skip the §7 confirmation gate even if the user's original request already said
"open a PR" — confirm the target branch, diff, and base branch explicitly once the actual
diff and validation results are known, not before. If `gh` is unavailable when creating the
PR, follow §7 step 3 and write `PR.md` at the repository root instead.
