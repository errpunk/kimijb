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
