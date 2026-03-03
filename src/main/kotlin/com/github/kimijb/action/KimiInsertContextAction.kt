package com.github.kimijb.action

import com.github.kimijb.context.ContextExtractor
import com.github.kimijb.service.KimiProjectService
import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAwareAction

class KimiInsertContextAction : DumbAwareAction() {

    private val LOG = Logger.getInstance(KimiInsertContextAction::class.java)

    override fun actionPerformed(e: AnActionEvent) {
        val project = e.project ?: run {
            LOG.warn("Option+K triggered but no project available")
            return
        }
        val editor = e.getData(CommonDataKeys.EDITOR) ?: run {
            LOG.warn("Option+K triggered but no editor available")
            return
        }

        val filePath = ContextExtractor.extractFilePath(editor)
        val lineNumber = ContextExtractor.extractLineNumber(editor)

        LOG.info("Option+K triggered: filePath=$filePath, lineNumber=$lineNumber")
        project.service<KimiProjectService>().insertContext(filePath, lineNumber)
    }

    override fun update(e: AnActionEvent) {
        val editor = e.getData(CommonDataKeys.EDITOR)
        e.presentation.isEnabled = editor != null
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
