---
name: hilit-code-review
description: >
  Review a pull request against this repository's product, architecture, and process
  contracts, following docs/workflows/pr-review.md. Use when the user asks to review a PR,
  review a pull request, do a code review, or invokes /hilit-code-review.
---

Follow `docs/workflows/pr-review.md` exactly, start to finish: the required reading order
(§3), the full review checklist (§4, every category — including User Data Protection and
Protected Documents), and the output format (§5 — write `review.md` at the repository root,
each finding tagged with both category and severity per §5.1). If a checklist category
doesn't apply, say so explicitly rather than omitting it.

If the user gives a PR number, branch name, or diff range, use it as the input described in
`docs/workflows/pr-review.md` §2. If no input is given, ask which PR/branch/diff to review
before proceeding.
