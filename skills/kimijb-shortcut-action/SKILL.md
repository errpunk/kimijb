---
name: kimijb-shortcut-action
description: Implement and maintain kimijb editor shortcut behavior, especially Option+K context injection. Use when requests involve action registration in plugin.xml, current file path or cursor line extraction, shortcut handling, project service wiring, or related tests in this repository.
---

# kimijb-shortcut-action

Implement shortcut changes with this flow:

1. Read `/Users/liutao/workspace/kimijb/AGENTS.md` and `/Users/liutao/workspace/kimijb/README.md`.
2. Inspect action and service files:
   - `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/action/KimiInsertContextAction.kt`
   - `/Users/liutao/workspace/kimijb/src/main/kotlin/com/github/kimijb/service/KimiProjectService.kt`
   - `/Users/liutao/workspace/kimijb/src/main/resources/META-INF/plugin.xml`
3. Define expected injected context format before coding.
4. Add or update tests first for key path, line number, and unavailable-editor edge cases.
5. Implement minimal changes and preserve existing UX unless user requests behavior change.
6. Run `./gradlew test`.

Guardrails:

- Keep shortcut wiring consistent with plugin.xml declarations.
- Keep insertion behavior deterministic and editor-state aware.
- Keep user-facing errors clear when context cannot be collected.
