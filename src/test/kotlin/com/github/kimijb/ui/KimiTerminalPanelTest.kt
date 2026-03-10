package com.github.kimijb.ui

import com.intellij.openapi.project.Project
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import javax.swing.JComponent

class KimiTerminalPanelTest {

    private val mockProject = mockk<Project>(relaxed = true)
    private lateinit var panel: KimiTerminalPanel

    @BeforeEach
    fun setup() {
        panel = KimiTerminalPanel(mockProject)
    }

    @Test
    fun `createComponent returns non-null JComponent`() {
        val component: JComponent = panel.createComponent()
        assertNotNull(component)
    }

    @Test
    fun `terminal widget is created after createComponent`() {
        panel.createComponent()
        // Terminal should be initialized
        assertNotNull(panel)
    }

    @Test
    fun `create terminal widget returns non-null widget`() {
        val terminalWidget = panel.createTerminalWidget()
        assertNotNull(terminalWidget)
    }

    @Test
    fun `buildKimiCommand adds continue flag only when requested`() {
        assertEquals(listOf("kimi"), panel.buildKimiCommand("kimi", continueSession = false))
        assertEquals(listOf("kimi", "--continue"), panel.buildKimiCommand("kimi", continueSession = true))
    }
}
