package com.github.kimijb.service

import com.github.kimijb.context.ContextExtractor
import com.github.kimijb.ui.KimiTerminalPanel
import com.intellij.openapi.components.Service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project

@Service(Service.Level.PROJECT)
class KimiProjectService(private val project: Project) {

    private val LOG = Logger.getInstance(KimiProjectService::class.java)
    private var panel: KimiTerminalPanel? = null

    fun registerPanel(panel: KimiTerminalPanel) {
        LOG.info("Registering KimiTerminalPanel")
        this.panel = panel
    }

    fun getPanel(): KimiTerminalPanel? = panel

    fun insertContext(filePath: String?, lineNumber: Int?) {
        val text = ContextExtractor.formatContextText(filePath, lineNumber)
        LOG.info("Inserting context: formattedText='$text', filePath=$filePath, lineNumber=$lineNumber")
        val panel = panel
        if (panel == null) {
            LOG.warn("Cannot insert context: panel is null (Tool Window may not be opened yet)")
            return
        }
        if (text.isEmpty()) {
            LOG.warn("Formatted context is empty, focusing panel without insertion")
            panel.focusInput()
            return
        }
        panel.setInputText(panel.formatInsertedContext(text))
        panel.focusInput()
        LOG.info("Context inserted successfully")
    }
}
