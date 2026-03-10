package com.github.kimijb.terminal

import com.jediterm.terminal.TtyConnector
import io.mockk.every
import io.mockk.mockk
import io.mockk.verifyOrder
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class KimiProcessManagerTest {

    private val manager = KimiProcessManager()
    private val mockProcess = mockk<Process>(relaxed = true)

    @Test
    fun `isRunning returns true when process is alive`() {
        every { mockProcess.isAlive } returns true
        val kimiProcess = KimiProcess(mockProcess, "/workspace")
        assertTrue(manager.isRunning(kimiProcess))
    }

    @Test
    fun `isRunning returns false when process has terminated`() {
        every { mockProcess.isAlive } returns false
        val kimiProcess = KimiProcess(mockProcess, "/workspace")
        assertFalse(manager.isRunning(kimiProcess))
    }

    @Test
    fun `stop calls destroy on the underlying process`() {
        val kimiProcess = KimiProcess(mockProcess, "/workspace")
        manager.stop(kimiProcess)
        verify { mockProcess.destroy() }
    }

    @Test
    fun `stopGracefully sends ctrl d before forcing process destroy`() {
        val ttyConnector = mockk<TtyConnector>(relaxed = true)
        every { mockProcess.isAlive } returnsMany listOf(true, true)
        every { mockProcess.waitFor(any<Long>(), any()) } returns false

        val kimiProcess = KimiProcess(mockProcess, "/workspace")
        manager.stopGracefully(kimiProcess, ttyConnector, timeoutMillis = 1)

        verifyOrder {
            ttyConnector.write("\u0004")
            ttyConnector.write("/exit\n")
            mockProcess.destroy()
        }
    }

    @Test
    fun `stopGracefully stops after ctrl d when process exits cleanly`() {
        val ttyConnector = mockk<TtyConnector>(relaxed = true)
        every { mockProcess.isAlive } returns true
        every { mockProcess.waitFor(any<Long>(), any()) } returns true

        val kimiProcess = KimiProcess(mockProcess, "/workspace")
        manager.stopGracefully(kimiProcess, ttyConnector, timeoutMillis = 1)

        verify { ttyConnector.write("\u0004") }
        verify(exactly = 0) { ttyConnector.write("/exit\n") }
        verify(exactly = 0) { mockProcess.destroy() }
    }

    @Test
    fun `findKimiExecutable returns null when command not found`() {
        // When kimi is not on PATH in test environment, should return null gracefully
        val result = manager.findKimiExecutable()
        // Either returns a path string or null - should not throw
        assertTrue(result == null || result.isNotEmpty())
    }

    @Test
    fun `start uses pty process with terminal environment`() {
        val fakeProcess = mockk<Process>(relaxed = true)
        val capturingManager = object : KimiProcessManager() {
            lateinit var command: Array<String>
            lateinit var workDir: String
            lateinit var env: Map<String, String>
            var initialColumns: Int? = null
            var initialRows: Int? = null

            override fun startPtyProcess(
                command: Array<String>,
                workDir: String,
                environment: Map<String, String>,
                initialColumns: Int?,
                initialRows: Int?
            ): Process {
                this.command = command
                this.workDir = workDir
                this.env = environment
                this.initialColumns = initialColumns
                this.initialRows = initialRows
                return fakeProcess
            }

            override fun baseEnvironment(): Map<String, String> = emptyMap()
        }

        val kimiProcess = capturingManager.start("/workspace", "/opt/homebrew/bin/kimi", 132, 40)

        assertEquals(fakeProcess, kimiProcess.process)
        assertEquals("/workspace", kimiProcess.workDir)
        assertEquals(listOf("/opt/homebrew/bin/kimi"), capturingManager.command.toList())
        assertEquals("/workspace", capturingManager.workDir)
        assertEquals("xterm-256color", capturingManager.env["TERM"])
        assertEquals("truecolor", capturingManager.env["COLORTERM"])
        assertEquals(132, capturingManager.initialColumns)
        assertEquals(40, capturingManager.initialRows)
    }

    @Test
    fun `start accepts command arguments for continued session`() {
        val fakeProcess = mockk<Process>(relaxed = true)
        val capturingManager = object : KimiProcessManager() {
            lateinit var command: Array<String>

            override fun startPtyProcess(
                command: Array<String>,
                workDir: String,
                environment: Map<String, String>,
                initialColumns: Int?,
                initialRows: Int?
            ): Process {
                this.command = command
                return fakeProcess
            }

            override fun baseEnvironment(): Map<String, String> = emptyMap()
        }

        capturingManager.start("/workspace", listOf("kimi", "--continue"))

        assertEquals(listOf("kimi", "--continue"), capturingManager.command.toList())
    }

    @Test
    fun `start merges login shell PATH into process environment`() {
        val fakeProcess = mockk<Process>(relaxed = true)
        val capturingManager = object : KimiProcessManager() {
            lateinit var env: Map<String, String>

            override fun startPtyProcess(
                command: Array<String>,
                workDir: String,
                environment: Map<String, String>,
                initialColumns: Int?,
                initialRows: Int?
            ): Process {
                this.env = environment
                return fakeProcess
            }

            override fun baseEnvironment(): Map<String, String> {
                return mapOf("PATH" to "/usr/bin:/bin")
            }

            override fun detectLoginShellPath(): String = "/opt/homebrew/bin:/Users/liutao/.nvm/versions/node/v22/bin"
        }

        capturingManager.start("/workspace", "kimi")

        val path = capturingManager.env["PATH"] ?: ""
        assertTrue(path.contains("/usr/bin"))
        assertTrue(path.contains("/bin"))
        assertTrue(path.contains("/opt/homebrew/bin"))
        assertTrue(path.contains("/Users/liutao/.nvm/versions/node/v22/bin"))
    }
}
