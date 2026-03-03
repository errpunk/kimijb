package com.github.kimijb.ui

import com.github.kimijb.terminal.KimiProcessManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.jediterm.terminal.ProcessTtyConnector
import com.jediterm.terminal.TtyConnector
import com.jediterm.terminal.ui.JediTermWidget
import com.jediterm.terminal.ui.settings.DefaultSettingsProvider
import java.awt.BorderLayout
import java.nio.charset.StandardCharsets
import javax.swing.JComponent
import javax.swing.JPanel

class KimiTerminalPanel(private val project: Project) : Disposable {

    private val LOG = Logger.getInstance(KimiTerminalPanel::class.java)
    private lateinit var terminalWidget: JediTermWidget
    private val processManager = KimiProcessManager()
    private var currentTtyConnector: TtyConnector? = null

    fun createComponent(): JComponent {
        val panel = JPanel(BorderLayout())

        // Create JediTerm widget with proper constructor
        terminalWidget = JediTermWidget(
            80,  // columns
            24,  // rows
            DefaultSettingsProvider()
        )
        panel.add(terminalWidget, BorderLayout.CENTER)

        return panel
    }

    fun startProcess(workDir: String) {
        LOG.info("Starting kimi terminal in: $workDir")

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
            val process = processManager.start(workDir, executable)
            val ttyConnector = createTtyConnector(process.process)
            currentTtyConnector = ttyConnector

            terminalWidget.setTtyConnector(ttyConnector)
            terminalWidget.start()

            LOG.info("Terminal started with kimi")

        } catch (e: Exception) {
            LOG.error("Failed to start kimi", e)
            showError("Error starting kimi: ${e.message}")
        }
    }

    private fun showError(message: String) {
        // Add a label above terminal to show error
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
        currentTtyConnector?.close()
        terminalWidget.stop()
    }
}
