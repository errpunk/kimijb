---
name: kimijb-plugin-dev
description: Use this skill for normal development work in the kimijb repository. It defines the standard workflow for implementing, testing, reviewing, packaging, and releasing changes to the JetBrains GoLand plugin while preserving PTY terminal behavior, Option+K context injection, and user-facing plugin quality.
---

# kimijb-plugin-dev

Use this as the default project workflow.

Workflow:

1. Read `/Users/liutao/workspace/kimijb/AGENTS.md` and `/Users/liutao/workspace/kimijb/README.md` before changing code.
2. For any non-trivial task, read `/Users/liutao/workspace/kimijb/docs/lessons-learned.md` before choosing an implementation.
3. Start development on a dedicated branch instead of working directly on `main`. Treat `main` as the protected release branch.
4. Name development branches clearly for the task. If Codex creates the branch itself, use the required `codex/` prefix.
5. Find the smallest change that solves the user request without widening scope.
6. Use TDD as the default development method. When behavior changes, add or update tests first, make them fail for the intended reason, then implement until they pass.
7. Implement in small steps and preserve existing plugin goals unless the user explicitly changes them.
8. Run `./gradlew test` after every coding change. Do not hand off with failing tests.
9. Run `./gradlew buildPlugin` when packaging, installability, or release confidence matters.
10. Merge finished work back into `main` only after tests are green and the branch is ready to release or integrate.
11. Use rebase merge when integrating a branch into `main`. Do not use squash merge or merge commits unless the user explicitly overrides that rule for a specific case.
12. If the task changes a stable engineering rule or tooling lesson, update `/Users/liutao/workspace/kimijb/docs/lessons-learned.md` in the same task.
13. If the task affects release content, update `/Users/liutao/workspace/kimijb/CHANGELOG.md` and keep release notes user-facing rather than implementation-heavy.

TDD expectation:

- Prefer red -> green -> refactor instead of coding first and adding tests later.
- Do not skip the red step unless the repository already has a tighter existing test harness that makes the failing case explicit.
- If a change is hard to drive with tests, call out the seam and add the smallest useful regression coverage before handoff.

Code management expectation:

- Keep `main` stable and releaseable. Do not treat it as the scratch branch for ongoing development.
- Protect `main` in the remote repository and require branch-based integration.
- Do daily feature work on short-lived task branches, then merge back into `main` after review and validation.
- Default to rebase merge for branch integration so history stays linear without squash rewriting.
- Keep commits focused so merge history stays understandable for release and rollback decisions.

Respect these invariants:

- Keep terminal startup interactive via PTY (`pty4j`).
- Keep terminal I/O connected through `ProcessTtyConnector`.
- Preserve login-shell `PATH` merge logic in process environment handling.
- Keep `Option+K` context injection behavior stable unless explicitly requested.
- Keep user-facing failure messages actionable (for example when `kimi` is missing).
- Prefer GoLand-compatible plugin behavior without depending on GoLand-only hacks unless necessary.

Use these files as the primary map:

- Tool window entry: `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/ui/KimiToolWindowFactory.kt`
- Terminal panel and injection: `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/ui/KimiTerminalPanel.kt`
- Process manager: `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/terminal/KimiProcessManager.kt`
- Option+K action: `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/action/KimiInsertContextAction.kt`
- Project service: `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/service/KimiProjectService.kt`
- Plugin declarations and shortcuts: `/Users/liutao/workspace/kimijb/src/main/resources/META-INF/plugin.xml`
- Release workflow: `/Users/liutao/workspace/kimijb/docs/release.md`
- Release notes source: `/Users/liutao/workspace/kimijb/CHANGELOG.md`

When unsure about IntelliJ APIs, prefer official JetBrains documentation. If offline, use `/Users/liutao/workspace/kimijb/docs/intellij-platform-sdk.md` as fallback.
