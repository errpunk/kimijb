# Contributing to kimijb

Thanks for your interest in contributing.

## Development Setup

1. Install JDK 17.
2. Clone the repository.
3. Run tests:

```bash
./gradlew test
```

4. Run IDE sandbox for manual checks:

```bash
./gradlew runIde
```

## What to Work On

- Bug fixes in terminal interaction and process startup
- Improvements to `Option+K` context insertion behavior
- Test coverage and reliability improvements
- UX and error-message polish

## Coding Guidelines

- Use Kotlin for implementation.
- Keep changes minimal and focused.
- Preserve compatibility with GoLand / IntelliJ Platform 2024.3+.
- Do not introduce regressions in PTY behavior.

## Testing Policy (Mandatory)

- Follow TDD when adding/changing behavior.
- Add or update tests first, then implement.
- Ensure all tests pass before submitting:

```bash
./gradlew test
```

## Pull Request Checklist

- [ ] Code compiles and tests pass
- [ ] Behavior changes are covered by tests
- [ ] Documentation is updated when needed
- [ ] No unrelated changes are included
- [ ] Plugin still works in `runIde` sandbox

## Reporting Issues

When filing issues, include:
- IDE version and OS
- `kimi-cli` version (`kimi --version`)
- Steps to reproduce
- Expected vs actual behavior
- Relevant logs/screenshots
