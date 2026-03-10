# Changelog

## [0.0.4] - 2026-03-10

- Added a title-bar `Refit Layout` action to reflow `kimi` against the current tool window width.
- Restarted `kimi` with `--continue` during layout refit so the current session context is preserved.
- Improved session shutdown during layout refit by attempting graceful exit (`Ctrl+D`, then `/exit`) before forcing process termination.
- Passed the measured terminal size into PTY startup so `kimi` can render against the initial panel width more accurately.

