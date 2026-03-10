# kimijb

`kimijb` is a JetBrains (GoLand) plugin that brings `kimi-cli` directly into the IDE.

Chinese version: [README.zh-CN.md](./README.zh-CN.md)

## Features

1. A `Kimi` Tool Window with an interactive terminal running `kimi`.
2. `Option+K` shortcut (macOS) that injects the current file path and cursor line into the `kimi` input context.
3. A `Configure Kimi Shortcut` action under `Tools` that opens `Settings | Keymap` so users can assign their own shortcut.

## Goals

- Make in-IDE `kimi` interaction consistent with a normal terminal (typing, Enter, Backspace).
- Avoid manual copy/paste of file path and line number when asking coding questions.

## Requirements

- GoLand / JetBrains 2024.3+ (IntelliJ Platform `sinceBuild=243`)
- `kimi-cli` installed locally
- Recommended sanity check: `kimi --version`

## Build and Install

### 1. Build the plugin

```bash
./gradlew buildPlugin
```

Output:
`build/distributions/kimijb-<version>.zip`

### 2. Install in GoLand

1. Open `Settings/Preferences` -> `Plugins`
2. Click the gear icon -> `Install Plugin from Disk...`
3. Select the zip in `build/distributions` and restart IDE

## Usage

1. Open the `Kimi` tool window on the right side.
2. Interact with `kimi` directly in the embedded terminal.
3. Press `Option+K` in an editor to inject `filePath:lineNumber` context.
4. If you want a different shortcut, run `Tools | Configure Kimi Shortcut` and rebind `Insert Kimi Context` in the IDE keymap.

## Development

### Common commands

```bash
./gradlew test
./gradlew runIde
./gradlew buildPlugin
```

### Project layout

- `src/main/kotlin/com/github/kimijb/ui/`: Tool window and terminal UI
- `src/main/kotlin/com/github/kimijb/terminal/`: `kimi` process and environment handling
- `src/main/kotlin/com/github/kimijb/action/`: Shortcut action
- `src/main/kotlin/com/github/kimijb/service/`: Project-level service
- `src/main/resources/META-INF/plugin.xml`: Plugin declarations and keymap
- `src/test/kotlin/...`: Unit tests

## Known Notes

- IDE apps launched from GUI can have a different environment from shell sessions. The plugin merges login-shell `PATH` to reduce MCP failures caused by missing tools like `npx`.
- If `kimi` is not installed or executable, the panel shows an explicit error message.

## References

- JetBrains plugin development docs:
  [https://plugins.jetbrains.com/docs/intellij/developing-plugins.html](https://plugins.jetbrains.com/docs/intellij/developing-plugins.html)
- kimi-cli repository:
  [https://github.com/MoonshotAI/kimi-cli](https://github.com/MoonshotAI/kimi-cli)
- Open-source cleanup checklist:
  [docs/open-source-cleanup.md](./docs/open-source-cleanup.md)
- Project lessons learned:
  [docs/lessons-learned.md](./docs/lessons-learned.md)
- Release guide:
  [docs/release.md](./docs/release.md)

## Contributing

See [CONTRIBUTING.md](./CONTRIBUTING.md).

## License

This project is licensed under the MIT License. See [LICENSE](./LICENSE).
