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

    @Test
    fun `continue refit falls back to fresh session when kimi exits quickly with missing-session code`() {
        assertEquals(
            true,
            panel.shouldFallbackToFreshSession(
                command = listOf("kimi", "--continue"),
                exitCode = 2,
                runtimeMillis = 500
            )
        )
    }

    @Test
    fun `continue refit does not fall back after longer-running session exit`() {
        assertEquals(
            false,
            panel.shouldFallbackToFreshSession(
                command = listOf("kimi", "--continue"),
                exitCode = 2,
                runtimeMillis = 5_000
            )
        )
    }

    @Test
    fun `non-continue command does not trigger fresh-session fallback`() {
        assertEquals(
            false,
            panel.shouldFallbackToFreshSession(
                command = listOf("kimi"),
                exitCode = 2,
                runtimeMillis = 500
            )
        )
    }

    @Test
    fun `welcome screen message tells user to open or create a project first`() {
        val message = panel.welcomeScreenMessage()

        assertEquals(
            """
                 _  ___           _
                | |/ (_)_ __ ___ (_)
                | ' /| | '_ ` _ \| |
                | . \| | | | | | | |
                |_|\_\_|_| |_| |_|_|

                Open or create a project first.
                Then reopen the Kimi tool window.
            """.trimIndent(),
            message
        )
    }
}
