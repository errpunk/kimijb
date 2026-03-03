---
name: kimijb-debug-terminal
description: Diagnose and fix kimijb embedded terminal issues for kimi-cli startup and interaction. Use when requests involve PTY behavior, ProcessTtyConnector bridging, PATH/environment mismatches, missing executable errors, terminal input glitches, or startup failure messaging in this repository.
---

# kimijb-debug-terminal

Follow this debugging sequence:

1. Read `/Users/liutao/workspace/kimijb/AGENTS.md` and `/Users/liutao/workspace/kimijb/docs/lessons-learned.md`.
2. Inspect terminal path: `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/ui/KimiTerminalPanel.kt`.
3. Inspect process startup and env merge: `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/terminal/KimiProcessManager.kt`.
4. Reproduce with the smallest reliable scenario.
5. Add or update tests for the failing behavior first.
6. Implement minimal fix while preserving PTY + ProcessTtyConnector architecture.
7. Run `./gradlew test` and report user-visible behavior changes.

Do not regress these constraints:

- Keep PTY startup (`pty4j`) for interactive CLI behavior.
- Keep `ProcessTtyConnector` as terminal bridge.
- Keep login-shell `PATH` merge logic to reduce GUI-launched IDE env drift.
- Keep failure output readable and actionable for missing `kimi` or broken startup.
