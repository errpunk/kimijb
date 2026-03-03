---
name: kimijb-plugin-dev
description: Maintain and extend the kimijb JetBrains GoLand plugin with minimal, test-first changes. Use when requests involve Kimi tool window behavior, PTY terminal integration, Option+K context injection, plugin.xml registration, process environment/PATH handling, plugin packaging, or test/build validation in this repository.
---

# kimijb-plugin-dev

Follow this workflow:

1. Read `/Users/liutao/workspace/kimijb/AGENTS.md` and `/Users/liutao/workspace/kimijb/README.md`.
2. For non-trivial changes, read `/Users/liutao/workspace/kimijb/docs/lessons-learned.md`.
3. Scope the minimum viable change to satisfy the user request.
4. Add or update tests first when behavior changes.
5. Implement in small steps and keep behavior compatible with existing plugin goals.
6. Run `./gradlew test` after code changes.
7. If packaging/distribution is relevant, run `./gradlew buildPlugin`.

Respect these invariants:

- Keep terminal startup interactive via PTY (`pty4j`).
- Keep terminal I/O connected through `ProcessTtyConnector`.
- Preserve login-shell `PATH` merge logic in process environment handling.
- Keep `Option+K` context injection behavior stable unless explicitly requested.
- Keep user-facing failure messages actionable (for example when `kimi` is missing).

Use these files as the primary map:

- Tool window entry: `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/ui/KimiToolWindowFactory.kt`
- Terminal panel and injection: `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/ui/KimiTerminalPanel.kt`
- Process manager: `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/terminal/KimiProcessManager.kt`
- Option+K action: `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/action/KimiInsertContextAction.kt`
- Project service: `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/service/KimiProjectService.kt`
- Plugin declarations and shortcuts: `/Users/liutao/workspace/kimijb/src/main/resources/META-INF/plugin.xml`

When unsure about IntelliJ APIs, prefer official JetBrains documentation. If offline, use `/Users/liutao/workspace/kimijb/docs/intellij-platform-sdk.md` as fallback.
