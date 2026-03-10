package com.github.kimijb.ui

import com.github.kimijb.terminal.KimiProcessManager
import com.github.kimijb.terminal.KimiProcess
import com.intellij.terminal.JBTerminalWidget
import com.jediterm.core.util.TermSize
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.jediterm.terminal.ProcessTtyConnector
import com.jediterm.terminal.TtyConnector
import com.jediterm.terminal.ui.JediTermWidget
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider
import org.jetbrains.plugins.terminal.JBTerminalSystemSettingsProvider
import java.awt.BorderLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.nio.charset.StandardCharsets
import javax.swing.JComponent
import javax.swing.JPanel
import javax.swing.SwingUtilities

class KimiTerminalPanel(private val project: Project) : Disposable {

    private val LOG = Logger.getInstance(KimiTerminalPanel::class.java)
    private lateinit var terminalWidget: JediTermWidget
    private val processManager = KimiProcessManager()
    private var currentProcess: KimiProcess? = null
    private var currentTtyConnector: TtyConnector? = null
    private var currentWorkDir: String? = null

    fun createComponent(): JComponent {
        val panel = JPanel(BorderLayout())

        // Prefer JetBrains terminal widget to inherit IDE terminal theme behavior.
        terminalWidget = createTerminalWidget()
        panel.add(terminalWidget, BorderLayout.CENTER)

        return panel
    }

    internal fun createTerminalWidget(): JediTermWidget {
        return try {
            JBTerminalWidget(project, JBTerminalSystemSettingsProvider(), this)
        } catch (t: Throwable) {
            LOG.warn("Falling back to default JediTerm widget", t)
            JediTermWidget(
                80,
                24,
                DefaultSettingsProvider()
            )
        }
    }

    fun startProcess(workDir: String) {
        LOG.info("Starting kimi terminal in: $workDir")
        currentWorkDir = workDir

        val executable = processManager.findKimiExecutable()
        if (executable == null) {
            LOG.error("Kimi not found")
            showError("""
                ⚠️  kimi-cli not found

                Please install: pip install kimi-cli
                Or ensure it's in: ~/.local/bin/kimi
            """.trimIndent())
            return
        }

        val version = processManager.getKimiVersion(executable)
        LOG.info("Found kimi $version at: $executable")

        try {
            val initialSize = resolveInitialTerminalSize()
            LOG.info("Resolved initial terminal size: $initialSize")
            val command = buildKimiCommand(executable, continueSession = false)
            val process = processManager.start(
                workDir,
                command,
                initialSize?.columns,
                initialSize?.rows
            )
            currentProcess = process
            val ttyConnector = createTtyConnector(process.process)
            currentTtyConnector = ttyConnector

            if (terminalWidget is JBTerminalWidget) {
                (terminalWidget as JBTerminalWidget).start(ttyConnector)
            } else {
                terminalWidget.setTtyConnector(ttyConnector)
                terminalWidget.start()
            }

            LOG.info("Terminal started with kimi")

        } catch (e: Exception) {
            LOG.error("Failed to start kimi", e)
            showError("Error starting kimi: ${e.message}")
        }
    }

    fun startProcessWhenReady(workDir: String) {
        currentWorkDir = workDir
        val controller = KimiTerminalStartupController(
            isReady = { isTerminalReady() },
            start = { startProcess(workDir) }
        )

        controller.requestStart { onRetry ->
            val terminalPanel = terminalWidget.terminalPanel
            val listener = object : ComponentAdapter() {
                override fun componentShown(e: ComponentEvent) = onRetry()

                override fun componentResized(e: ComponentEvent) = onRetry()
            }
            terminalPanel.addComponentListener(listener)
            return@requestStart { terminalPanel.removeComponentListener(listener) }
        }

        SwingUtilities.invokeLater {
            controller.retry()
        }
    }

    fun refitLayout() {
        val workDir = currentWorkDir ?: return
        val executable = processManager.findKimiExecutable() ?: run {
            showError("""
                ⚠️  kimi-cli not found

                Please install: pip install kimi-cli
                Or ensure it's in: ~/.local/bin/kimi
            """.trimIndent())
            return
        }

        stopCurrentSession(graceful = true)

        val controller = KimiTerminalStartupController(
            isReady = { isTerminalReady() },
            start = {
                startProcessWithCommand(
                    workDir = workDir,
                    command = buildKimiCommand(executable, continueSession = true)
                )
            }
        )

        controller.requestStart { onRetry ->
            val terminalPanel = terminalWidget.terminalPanel
            val listener = object : ComponentAdapter() {
                override fun componentShown(e: ComponentEvent) = onRetry()

                override fun componentResized(e: ComponentEvent) = onRetry()
            }
            terminalPanel.addComponentListener(listener)
            return@requestStart { terminalPanel.removeComponentListener(listener) }
        }

        SwingUtilities.invokeLater {
            controller.retry()
        }
    }

    fun canRefitLayout(): Boolean {
        val process = currentProcess ?: return false
        return processManager.isRunning(process)
    }

    internal fun resolveInitialTerminalSize(): TermSize? {
        val size = terminalWidget.terminalPanel.getTerminalSizeFromComponent()
        return size?.takeIf { it.columns > 0 && it.rows > 0 }
    }

    internal fun isTerminalReady(): Boolean {
        val terminalPanel = terminalWidget.terminalPanel
        return terminalPanel.isShowing && resolveInitialTerminalSize() != null
    }

    internal fun buildKimiCommand(executable: String, continueSession: Boolean): List<String> {
        return buildList {
            add(executable)
            if (continueSession) {
                add("--continue")
            }
        }
    }

    private fun startProcessWithCommand(workDir: String, command: List<String>) {
        currentWorkDir = workDir
        val initialSize = resolveInitialTerminalSize()
        LOG.info("Resolved initial terminal size: $initialSize")
        val process = processManager.start(
            workDir,
            command,
            initialSize?.columns,
            initialSize?.rows
        )
        currentProcess = process
        val ttyConnector = createTtyConnector(process.process)
        currentTtyConnector = ttyConnector

        if (terminalWidget is JBTerminalWidget) {
            (terminalWidget as JBTerminalWidget).start(ttyConnector)
        } else {
            terminalWidget.setTtyConnector(ttyConnector)
            terminalWidget.start()
        }

        LOG.info("Terminal started with command: ${command.joinToString(" ")}")
    }

    private fun stopCurrentSession(graceful: Boolean = false) {
        val ttyConnector = currentTtyConnector
        val process = currentProcess
        if (graceful && process != null) {
            processManager.stopGracefully(process, ttyConnector)
        } else {
            ttyConnector?.close()
            process?.let { processManager.stop(it) }
        }
        currentTtyConnector?.close()
        currentTtyConnector = null
        currentProcess = null
        terminalWidget.stop()
    }

    private fun showError(message: String) {
        showMessage(message)
    }

    private fun showMessage(message: String) {
        // Add a label above terminal to show status
        val parent = terminalWidget.parent as? JPanel
        parent?.add(javax.swing.JLabel("<html><pre>$message</pre></html>"), java.awt.BorderLayout.NORTH)
        parent?.revalidate()
    }

    private fun createTtyConnector(process: Process): TtyConnector {
        return object : ProcessTtyConnector(process, StandardCharsets.UTF_8) {
            override fun getName(): String = "kimi"
        }
    }

    fun setInputText(text: String) {
        currentTtyConnector?.write(text)
    }

    fun appendInputText(text: String) {
        currentTtyConnector?.write(" " + text)
    }

    override fun dispose() {
        LOG.info("Disposing terminal panel")
        stopCurrentSession()
    }
}
