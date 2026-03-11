# AGENTS Guide (kimijb)

This document defines how AI agents should work in this repository.

## 1. Product Scope

`kimijb` is a JetBrains GoLand plugin for `kimi-cli` interoperability.

Core goals:
1. Provide a sidebar tool window that behaves like an interactive terminal running `kimi`.
2. Provide `Option+K` to inject current file path and cursor line into the panel input context.

## 2. Knowledge Baseline

Important files:
- `src/main/kotlin/com/github/kimijb/ui/KimiToolWindowFactory.kt`: tool window entry
- `src/main/kotlin/com/github/kimijb/ui/KimiTerminalPanel.kt`: terminal UI and input injection
- `src/main/kotlin/com/github/kimijb/terminal/KimiProcessManager.kt`: process startup and env/PATH handling
- `src/main/kotlin/com/github/kimijb/action/KimiInsertContextAction.kt`: `Option+K` behavior
- `src/main/kotlin/com/github/kimijb/service/KimiProjectService.kt`: panel registration and context insertion
- `src/main/resources/META-INF/plugin.xml`: extensions and shortcut declarations
- `docs/backlog.md`: product backlog, target versions, and acceptance criteria
- `docs/intellij-platform-sdk.md`: local IntelliJ SDK reference (fallback only; see rule below)
- `docs/lessons-learned.md`: project lessons and reusable engineering learnings

Documentation source priority:
- Always prefer the official JetBrains documentation as the source of truth.
- For feature planning and progress tracking, use `docs/backlog.md` as the source of truth inside the repository.
- If online access is unavailable, use `docs/intellij-platform-sdk.md` as a local fallback reference.
- Always read `docs/lessons-learned.md` before making non-trivial implementation decisions.
- When a new stable lesson is discovered (for example tooling pitfalls, environment gotchas, or workflow improvements), update `docs/lessons-learned.md` in the same task.

Terminal implementation constraints (do not regress):
- Use PTY (`pty4j`) for interactive CLI startup.
- Use `ProcessTtyConnector` for terminal I/O bridging.
- Merge login-shell `PATH` into process environment to avoid missing tools (for example `npx`) that can break MCP server startup.

## 3. Working Model

Default sequence:
1. Read `README.md` and this file.
2. If the task is a product change or new feature, read `docs/backlog.md` and align the work with the relevant backlog item, target version, and acceptance criteria.
3. Inspect relevant code/tests and define minimum-change scope.
4. Implement in small, verifiable steps.
5. Fix reproducible issues first, then optimize.
6. Validate against goals before finishing.

Execution rule:
- Continue until task completion unless blocked by external dependency, unresolved architecture decision, or hard technical blocker.

## 4. Testing and Quality Gate (Mandatory)

TDD flow:
1. Add/update tests first (red).
2. Implement until tests pass (green).
3. Refactor while keeping tests green.

Required after every coding change:
```bash
./gradlew test
```

Rules:
- Do not skip, delete, or comment out failing tests to pass CI.
- If tests fail, fix them before moving to the next task.
- Before handoff, ensure `./gradlew test` is fully green.

Recommended packaging check:
```bash
./gradlew buildPlugin
```

## 5. Definition of Done

- [ ] Tool window is visible and usable in GoLand.
- [ ] Embedded `kimi` terminal is interactive (typing, Enter, Backspace).
- [ ] `Option+K` is registered and triggers correctly.
- [ ] Current file path is injected correctly.
- [ ] Current cursor line is injected correctly.
- [ ] Failure states are user-readable (for example missing `kimi`).
- [ ] `./gradlew test` passes.

## 6. Development Notes

- Keep Kotlin code clear, minimal, and low side-effect.
- Prioritize GoLand compatibility while staying IntelliJ-platform friendly.
- Always consider GUI-launched IDE environment differences (`PATH`, shell env).
- When behavior changes, add/update tests accordingly.
- Write changelog / change-notes for ordinary users first. Prefer user-visible outcomes and avoid low-level implementation detail unless it changes user expectations.
- Keep execution focused: prefer one task, one branch, and one primary goal at a time.
- Add new product ideas to `docs/backlog.md` instead of leaving them only in chat history.
- For implemented backlog items, keep status, target version, and acceptance criteria in sync with the actual code and release plan.

## 7. Learn and Record User Preferences (Mandatory)

Rule:
- During collaboration, learn stable user habits and preferences.
- Record confirmed preferences in `## 8. User Preference Log`.
- Record only project-relevant preferences. Do not store private or unrelated data.
- If the user corrects a preference, update the log immediately.

Record format:
- `- YYYY-MM-DD: Preference statement (source: explicit user request)`

## 8. User Preference Log

- 2026-03-03: User prefers conservative versioning and requested `0.0.x` starting point (source: explicit request to use `0.0.1`).
- 2026-03-10: User wants changelog / change-notes written for ordinary users, not implementation-heavy release notes (source: explicit request).
- 2026-03-10: User wants `main` treated as the protected release branch, with day-to-day development on short-lived branches that merge back into `main` (source: explicit request).
- 2026-03-10: User wants branch integration into `main` to default to rebase merge rather than squash merge or merge commits (source: explicit request).
- 2026-03-10: User wants work kept to one task, one branch, and one primary goal at a time to reduce AI scope drift (source: explicit request).
- 2026-03-11: User wants user-facing welcome-state copy to stay JetBrains-IDE-generic and avoid GoLand-specific wording when the behavior applies across IDEs (source: explicit request).
