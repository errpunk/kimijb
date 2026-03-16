# Changelog

## [0.0.6] - 2026-03-16

- Updated `Option+K` to insert file context with a trailing space so you can keep typing naturally right after the inserted path.
- Made `Option+K` refocus the Kimi input after insertion so the typing cursor stays ready in the plugin.
- Added a fallback for missing file paths so `Option+K` still focuses the Kimi input even when no valid path can be inserted.

## [0.0.5] - 2026-03-11

- Stopped launching `kimi` from the JetBrains IDE welcome screen, so the tool window no longer attaches to an IDE placeholder workspace.
- Added an ASCII empty state in the `Kimi` tool window that tells you to open or create a project first.
- Kept normal in-project startup unchanged, so `kimi` still starts from the real project context after a project is opened.
- Fixed `Refit Layout` so it falls back to a fresh `kimi` session when `--continue` has no previous session to restore.

## [0.0.4] - 2026-03-10

- Added a title-bar `Refit Layout` action to reflow `kimi` against the current tool window width.
- Restarted `kimi` with `--continue` during layout refit so the current session context is preserved.
- Improved session shutdown during layout refit by attempting graceful exit (`Ctrl+D`, then `/exit`) before forcing process termination.
- Passed the measured terminal size into PTY startup so `kimi` can render against the initial panel width more accurately.
