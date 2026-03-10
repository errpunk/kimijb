# Product Backlog

This file tracks open product and workflow ideas for `kimijb`.

Status values:
- `todo`: not started
- `doing`: in progress
- `done`: implemented and released
- `wontdo`: intentionally dropped

Recommended item fields:
- `Status`
- `Priority`
- `Target version`
- `Summary`
- `Problem`
- `Desired behavior`
- `Acceptance criteria`
- `Notes`

## Items

### BL-001 Welcome Screen Empty State

- Status: `todo`
- Priority: `high`
- Target version: `0.0.5`
- Summary: Do not start `kimi` on the GoLand welcome screen. Show a clear empty state instead.
- Problem:
  - On the welcome screen, the plugin can end up starting `kimi` against an IDE-managed placeholder workspace.
  - This creates a confusing experience because the user has not opened a real project yet.
- Desired behavior:
  - The `Kimi` tool window may remain visible on the welcome screen.
  - Opening it on the welcome screen should not start `kimi`.
  - The panel should show a user-readable empty state similar to Junie, telling the user to open or create a project first.
  - Once the user opens a real project, `kimi` should start normally from the real project context.
- Acceptance criteria:
  - On the GoLand welcome screen, opening the `Kimi` tool window does not launch a `kimi` process.
  - The panel shows a readable empty state that tells the user to open or create a project.
  - After opening a real project, `Kimi` starts from that real project context instead of an IDE-managed placeholder workspace.
  - Existing project behavior remains unchanged outside the welcome-screen case.
- Notes:
  - Prefer a real project-context check such as `project.isDefault` and project availability signals.
  - Avoid brittle path-based heuristics for IDE-managed placeholder workspaces.
