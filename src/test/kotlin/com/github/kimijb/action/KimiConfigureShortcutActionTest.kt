package com.github.kimijb.action

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test

class KimiConfigureShortcutActionTest {

    @Test
    fun `actionPerformed opens shortcut settings for insert context action`() {
        val navigator = mockk<ShortcutSettingsNavigator>(relaxed = true)
        val project = mockk<Project>(relaxed = true)
        val action = KimiConfigureShortcutAction(navigator)
        val event = mockk<AnActionEvent> {
            every { this@mockk.project } returns project
        }

        action.actionPerformed(event)

        verify { navigator.open(project, KimiActionIds.INSERT_CONTEXT) }
    }
}
