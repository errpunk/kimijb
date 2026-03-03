package com.github.kimijb.terminal

import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Test

class KimiProcessTest {

    @Test
    fun `KimiProcess holds correct process and workDir after construction`() {
        val mockProcess = mockk<Process>()
        val kimiProcess = KimiProcess(mockProcess, "/workspace")
        assertEquals(mockProcess, kimiProcess.process)
        assertEquals("/workspace", kimiProcess.workDir)
    }

    @Test
    fun `KimiProcess equality based on value`() {
        val mockProcess = mockk<Process>()
        val p1 = KimiProcess(mockProcess, "/workspace")
        val p2 = KimiProcess(mockProcess, "/workspace")
        assertEquals(p1, p2)
    }

    @Test
    fun `KimiProcess copy creates independent instance`() {
        val mockProcess = mockk<Process>()
        val original = KimiProcess(mockProcess, "/workspace")
        val copied = original.copy(workDir = "/other")
        assertNotEquals(original, copied)
        assertEquals("/workspace", original.workDir)
        assertEquals("/other", copied.workDir)
    }
}
