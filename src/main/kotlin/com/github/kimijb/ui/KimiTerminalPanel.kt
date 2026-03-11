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
import java.awt.CardLayout
import java.awt.BorderLayout
import java.awt.event.ComponentAdapter
import java.awt.event.ComponentEvent
import java.nio.charset.StandardCharsets
import kotlin.concurrent.thread
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.SwingConstants
import javax.swing.SwingUtilities

class KimiTerminalPanel(private val project: Project) : Disposable {

    private companion object {
        const val CARD_TERMINAL = "terminal"
        const val CARD_STATUS = "status"
        const val CONTINUE_MISSING_SESSION_EXIT_CODE = 2
        const val CONTINUE_FAILURE_FALLBACK_WINDOW_MILLIS = 2_000L
    }

    private val LOG = Logger.getInstance(KimiTerminalPanel::class.java)
    private lateinit var terminalWidget: JediTermWidget
    private lateinit var rootPanel: JPanel
    private lateinit var contentPanel: JPanel
    private lateinit var statusLabel: JLabel
    private val processManager = KimiProcessManager()
    private var currentProcess: KimiProcess? = null
    private var currentTtyConnector: TtyConnector? = null
    private var currentWorkDir: String? = null

    fun createComponent(): JComponent {
        rootPanel = JPanel(BorderLayout())
        contentPanel = JPanel(CardLayout())

        // Prefer JetBrains terminal widget to inherit IDE terminal theme behavior.
        terminalWidget = createTerminalWidget()
        val terminalContainer = JPanel(BorderLayout())
        terminalContainer.add(terminalWidget, BorderLayout.CENTER)
        contentPanel.add(terminalContainer, CARD_TERMINAL)

        statusLabel = JLabel("", SwingConstants.CENTER)
        val statusPanel = JPanel(BorderLayout())
        statusPanel.add(statusLabel, BorderLayout.CENTER)
        contentPanel.add(statusPanel, CARD_STATUS)

        rootPanel.add(contentPanel, BorderLayout.CENTER)

        return rootPanel
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

            showTerminal()
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
                    command = buildKimiCommand(executable, continueSession = true),
                    fallbackCommand = buildKimiCommand(executable, continueSession = false)
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

    fun showWelcomeScreenEmptyState() {
        showMessage(welcomeScreenMessage())
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

    internal fun welcomeScreenMessage(): String {
        return """
             _  ___           _
            | |/ (_)_ __ ___ (_)
            | ' /| | '_ ` _ \| |
            | . \| | | | | | | |
            |_|\_\_|_| |_| |_|_|

            Open or create a project first.
            Then reopen the Kimi tool window.
        """.trimIndent()
    }

    internal fun shouldFallbackToFreshSession(
        command: List<String>,
        exitCode: Int,
        runtimeMillis: Long
    ): Boolean {
        return command.contains("--continue") &&
            exitCode == CONTINUE_MISSING_SESSION_EXIT_CODE &&
            runtimeMillis <= CONTINUE_FAILURE_FALLBACK_WINDOW_MILLIS
    }

    private fun startProcessWithCommand(
        workDir: String,
        command: List<String>,
        fallbackCommand: List<String>? = null
    ) {
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

        showTerminal()
        LOG.info("Terminal started with command: ${command.joinToString(" ")}")
        scheduleFallbackToFreshSessionIfNeeded(
            workDir = workDir,
            command = command,
            process = process,
            ttyConnector = ttyConnector,
            fallbackCommand = fallbackCommand
        )
    }

    private fun scheduleFallbackToFreshSessionIfNeeded(
        workDir: String,
        command: List<String>,
        process: KimiProcess,
        ttyConnector: TtyConnector,
        fallbackCommand: List<String>?
    ) {
        if (fallbackCommand == null) {
            return
        }

        val startedAtMillis = System.currentTimeMillis()
        thread(
            start = true,
            isDaemon = true,
            name = "kimi-refit-session-monitor"
        ) {
            val exitCode = process.process.waitFor()
            val runtimeMillis = System.currentTimeMillis() - startedAtMillis
            if (!shouldFallbackToFreshSession(command, exitCode, runtimeMillis)) {
                return@thread
            }

            LOG.info(
                "kimi --continue exited quickly with code=$exitCode after ${runtimeMillis}ms; " +
                    "retrying without previous-session restore"
            )

            SwingUtilities.invokeLater {
                restartWithoutContinueIfCurrent(process, ttyConnector, workDir, fallbackCommand)
            }
        }
    }

    private fun restartWithoutContinueIfCurrent(
        failedProcess: KimiProcess,
        failedTtyConnector: TtyConnector,
        workDir: String,
        fallbackCommand: List<String>
    ) {
        if (currentProcess != failedProcess) {
            return
        }

        failedTtyConnector.close()
        currentTtyConnector = null
        currentProcess = null
        terminalWidget.stop()
        startProcessWithCommand(workDir, fallbackCommand)
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
        if (!::statusLabel.isInitialized || !::contentPanel.isInitialized) {
            return
        }
        statusLabel.text = """
            <html>
            <div style='text-align:center;'>${message.trim().replace("\n", "<br/>")}</div>
            </html>
        """.trimIndent()
        (contentPanel.layout as CardLayout).show(contentPanel, CARD_STATUS)
        contentPanel.revalidate()
        contentPanel.repaint()
    }

    private fun showTerminal() {
        if (!::contentPanel.isInitialized) {
            return
        }
        (contentPanel.layout as CardLayout).show(contentPanel, CARD_TERMINAL)
        contentPanel.revalidate()
        contentPanel.repaint()
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
        if (::terminalWidget.isInitialized) {
            stopCurrentSession()
        }
    }
}
