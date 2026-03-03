package com.github.kimijb.service

import com.github.kimijb.ui.KimiTerminalPanel
import com.intellij.openapi.project.Project
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KimiProjectServiceTest {

    private val mockProject = mockk<Project>(relaxed = true)
    private val mockPanel = mockk<KimiTerminalPanel>(relaxed = true)
    private val mockPanel2 = mockk<KimiTerminalPanel>(relaxed = true)
    private lateinit var service: KimiProjectService

    @BeforeEach
    fun setup() {
        service = KimiProjectService(mockProject)
    }

    @Test
    fun `getPanel returns null when no panel registered`() {
        assertNull(service.getPanel())
    }

    @Test
    fun `registerPanel stores panel and getPanel returns it`() {
        service.registerPanel(mockPanel)
        assertEquals(mockPanel, service.getPanel())
    }

    @Test
    fun `registerPanel replaces previous panel on re-registration`() {
        service.registerPanel(mockPanel)
        service.registerPanel(mockPanel2)
        assertEquals(mockPanel2, service.getPanel())
    }

    @Test
    fun `insertContext calls setInputText when panel registered`() {
        service.registerPanel(mockPanel)
        service.insertContext("a.go", 5)
        verify { mockPanel.setInputText("a.go:5") }
    }

    @Test
    fun `insertContext does nothing when panel is null`() {
        // Should not throw
        service.insertContext("a.go", 5)
    }

    @Test
    fun `insertContext with null filePath does not call setInputText`() {
        service.registerPanel(mockPanel)
        service.insertContext(null, 5)
        verify(exactly = 0) { mockPanel.setInputText(any()) }
    }
}
