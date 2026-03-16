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

- Status: `done`
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

### BL-002 Option+K Trailing Space And Caret Placement

- Status: `doing`
- Priority: `medium`
- Target version: `0.0.6`
- Summary: When `Option+K` inserts file context into the Kimi panel, append a trailing space after the inserted context and leave the input caret after that space.
- Problem:
  - Today `Option+K` inserts the file context, but the follow-up typing position does not align with the inserted path in a natural input flow.
  - Without a trailing space after the inserted context, the next typed text runs directly against `@filePath` or `@filePath:lineNumber`.
  - If the current editor context cannot provide a file path, `Option+K` currently has no useful fallback behavior in the Kimi panel.
- Desired behavior:
  - Triggering `Option+K` should insert the context in the form `@filePath ` or `@filePath:lineNumber `.
  - After insertion, the terminal input caret should remain immediately after the trailing space so the user can keep typing naturally.
  - The behavior should work both when only the file path is available and when `filePath:lineNumber` is available.
  - If the file path cannot be resolved, `Option+K` should still move focus to the Kimi panel and place the input caret there so the shortcut remains useful.
- Acceptance criteria:
  - Triggering `Option+K` inserts context with one trailing space after `@filePath` or `@filePath:lineNumber`.
  - After insertion, the input caret is positioned after that trailing space, ready for continued typing.
  - The resulting text remains correctly formatted for both `@filePath` and `@filePath:lineNumber`.
  - If no file path can be extracted, the shortcut still focuses the Kimi panel and leaves the input caret ready for typing without inserting broken context text.
  - Existing context extraction behavior remains unchanged outside the insertion formatting and caret placement.
- Notes:
  - The trailing space is part of the inserted context format and should be inserted consistently with the file context text.
  - Prefer implementing caret placement in the terminal input path itself instead of relying on timing-sensitive synthetic key events.
