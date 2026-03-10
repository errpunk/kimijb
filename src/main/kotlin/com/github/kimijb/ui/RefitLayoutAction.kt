package com.github.kimijb.ui

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAwareAction

class RefitLayoutAction(
    private val panel: KimiTerminalPanel
) : DumbAwareAction(
    "Refit Layout",
    "Restart kimi with --continue to fit the current panel width",
    KimiIcons.REFIT_LAYOUT
) {

    private val log = Logger.getInstance(RefitLayoutAction::class.java)

    override fun actionPerformed(e: AnActionEvent) {
        log.info("Refitting kimi layout with --continue")
        panel.refitLayout()
    }

    override fun update(e: AnActionEvent) {
        e.presentation.isEnabled = panel.canRefitLayout()
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
