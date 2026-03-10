package com.github.kimijb.action

import com.intellij.openapi.keymap.impl.ui.KeymapPanel
import com.intellij.openapi.options.ShowSettingsUtil
import com.intellij.openapi.project.Project

open class ShortcutSettingsNavigator {

    open fun open(project: Project?, actionId: String) {
        ShowSettingsUtil.getInstance().showSettingsDialog(project, KeymapPanel::class.java) {
            it.selectAction(actionId)
        }
    }
}
