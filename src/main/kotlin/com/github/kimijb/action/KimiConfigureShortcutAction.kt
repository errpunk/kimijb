package com.github.kimijb.action

import com.intellij.openapi.actionSystem.ActionUpdateThread
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.DumbAwareAction

class KimiConfigureShortcutAction(
    private val shortcutSettingsNavigator: ShortcutSettingsNavigator = ShortcutSettingsNavigator()
) : DumbAwareAction() {

    private val log = Logger.getInstance(KimiConfigureShortcutAction::class.java)

    override fun actionPerformed(e: AnActionEvent) {
        log.info("Opening Keymap settings for action ${KimiActionIds.INSERT_CONTEXT}")
        shortcutSettingsNavigator.open(e.project, KimiActionIds.INSERT_CONTEXT)
    }

    override fun getActionUpdateThread(): ActionUpdateThread = ActionUpdateThread.BGT
}
