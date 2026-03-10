# Lessons Learned

## Early Gradle Setup Detours

During early development, Gradle was not available in the local environment. Multiple alternative attempts were made before settling on the standard Gradle Wrapper flow.

Final policy adopted for this repository:

1. Use the project wrapper (`./gradlew`) as the single build entry point.
2. Avoid committing workaround code created only to bypass missing local tooling.
3. Keep useful context in documentation, not in runtime code paths.
4. Treat one-off setup explorations as historical notes, not long-term implementation.

This preserves maintainability while keeping operational knowledge available.

## Terminal Theme Consistency

When embedding terminal UI in a JetBrains plugin, prefer `com.intellij.terminal.JBTerminalWidget` with `org.jetbrains.plugins.terminal.JBTerminalSystemSettingsProvider`. This path aligns with IDE terminal behavior and theme colors more reliably than constructing raw `JediTermWidget` directly.

Keep a safe fallback (`JediTermWidget` + `DefaultSettingsProvider`) for headless/unit-test contexts where full IDE application services may be unavailable.

If runtime code references classes from bundled JetBrains plugins (for example `org.jetbrains.plugins.terminal`), declare matching `<depends>` entries in `META-INF/plugin.xml`. Gradle `bundledPlugin` alone is not enough for plugin classloader resolution at runtime.

## Shortcut Customization

For JetBrains actions, prefer reusing the IDE `Keymap` settings instead of building plugin-local shortcut storage. If users need a different shortcut, documenting where the action appears in `Settings | Keymap` is often enough and avoids adding extra UI surface that does not expand actual capability.

## Terminal Startup Sizing

If a CLI adjusts its startup layout from PTY size, do not rely on a single `invokeLater` to make terminal dimensions available. Wait until the embedded terminal component reports a positive measured size, then pass those columns and rows into `PtyProcessBuilder` as the initial PTY size.

Tool-window scoped actions such as layout refit belong in the tool window gear menu. That keeps terminal content uncluttered and lets session restarts reuse the same startup path, including any PTY sizing logic.

## Marketplace Change Notes

Keep Marketplace release notes in a repository file instead of retyping them in the Marketplace UI. Wiring the latest `CHANGELOG.md` entry into Gradle `pluginConfiguration.changeNotes` makes versioned release notes reviewable in PRs and keeps IDE "What's New" content consistent with published releases.
