package com.github.kimijb.ui

import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.Presentation
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RefitLayoutActionTest {

    @Test
    fun `actionPerformed refits kimi layout`() {
        val panel = mockk<KimiTerminalPanel>(relaxed = true)
        val action = RefitLayoutAction(panel)
        val event = mockk<AnActionEvent>(relaxed = true)

        action.actionPerformed(event)

        verify { panel.refitLayout() }
    }

    @Test
    fun `update disables action when panel cannot refit layout`() {
        val panel = mockk<KimiTerminalPanel>()
        every { panel.canRefitLayout() } returns false
        val presentation = Presentation()
        val action = RefitLayoutAction(panel)
        val event = mockk<AnActionEvent> {
            every { getPresentation() } returns presentation
        }

        action.update(event)

        assertFalse(presentation.isEnabled)
    }

    @Test
    fun `update enables action when panel can refit layout`() {
        val panel = mockk<KimiTerminalPanel>()
        every { panel.canRefitLayout() } returns true
        val presentation = Presentation()
        val action = RefitLayoutAction(panel)
        val event = mockk<AnActionEvent> {
            every { getPresentation() } returns presentation
        }

        action.update(event)

        assertTrue(presentation.isEnabled)
    }
}
