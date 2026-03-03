# Open Source Cleanup Checklist

This checklist keeps the main branch clean and maintainable before public release.

## Keep in Main Branch

- Production code required for current behavior
- Tests that verify real behavior and prevent regressions
- Build scripts and plugin metadata required to build/release
- User-facing documentation (`README`, `CONTRIBUTING`, `LICENSE`)

## Do Not Keep in Main Branch

- Throwaway experiments and trial implementations
- Temporary scripts used only for one-time local setup
- Dead code paths preserved only for historical reasons
- Local-only artifacts (`build/`, `.gradle/`, sandbox caches, IDE runtime files)

## Historical Context Policy

- Keep implementation history in Git history.
- If a failed path has learning value, document it in `docs/lessons-learned.md`.
- Do not keep non-production workaround code in runtime paths.

## Pre-release Verification

- `./gradlew test`
- `./gradlew buildPlugin`
- Verify generated package under `build/distributions/`

## Documentation Verification

- `README.md` is the primary English doc
- `README.zh-CN.md` is available for Chinese readers
- `AGENTS.md` is English and states source-of-truth doc policy
