---
name: kimijb-release
description: Prepare and execute kimijb plugin release tasks with conservative versioning and repeatable checks. Use when requests involve bumping plugin version, validating plugin metadata, building distributable zip, release readiness checks, or publishing workflow preparation for this repository.
---

# kimijb-release

Execute release work in this order:

1. Read `/Users/liutao/workspace/kimijb/AGENTS.md`, `/Users/liutao/workspace/kimijb/README.md`, and `/Users/liutao/workspace/kimijb/build.gradle`.
2. Confirm current versioning context in `/Users/liutao/workspace/kimijb/gradle.properties` and keep conservative increments unless explicitly overridden.
3. Apply minimal version and metadata edits needed for the release task.
4. Run `./gradlew test` before packaging.
5. Run `./gradlew buildPlugin` and verify output artifact under `build/distributions/`.
6. Summarize changed files, resulting version, and artifact path.

Keep release changes focused:

- Avoid unrelated refactors during release preparation.
- Keep plugin compatibility constraints intact unless the user requests a compatibility change.
- Ensure release-facing notes are concrete and reproducible.
