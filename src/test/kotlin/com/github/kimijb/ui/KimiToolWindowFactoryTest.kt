package com.github.kimijb.ui

import com.intellij.openapi.project.Project
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KimiToolWindowFactoryTest {

    private val factory = KimiToolWindowFactory()

    @Test
    fun `shouldStartTerminal returns false for default welcome-screen project`() {
        val project = mockk<Project>()
        every { project.isDefault } returns true

        assertFalse(factory.shouldStartTerminal(project))
    }

    @Test
    fun `shouldStartTerminal returns true for regular project`() {
        val project = mockk<Project>()
        every { project.isDefault } returns false

        assertTrue(factory.shouldStartTerminal(project))
    }
}
