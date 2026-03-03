# Lessons Learned

## Early Gradle Setup Detours

During early development, Gradle was not available in the local environment. Multiple alternative attempts were made before settling on the standard Gradle Wrapper flow.

Final policy adopted for this repository:

1. Use the project wrapper (`./gradlew`) as the single build entry point.
2. Avoid committing workaround code created only to bypass missing local tooling.
3. Keep useful context in documentation, not in runtime code paths.
4. Treat one-off setup explorations as historical notes, not long-term implementation.

This preserves maintainability while keeping operational knowledge available.
