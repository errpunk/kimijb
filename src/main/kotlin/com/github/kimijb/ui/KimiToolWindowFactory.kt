package com.github.kimijb.ui

import com.github.kimijb.service.KimiProjectService
import com.intellij.openapi.components.service
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory

class KimiToolWindowFactory : ToolWindowFactory {

    private val LOG = Logger.getInstance(KimiToolWindowFactory::class.java)

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        LOG.info("Creating Kimi Tool Window, project=${project.name}")

        val panel = KimiTerminalPanel(project)
        val component = panel.createComponent()
        LOG.info("KimiTerminalPanel created")

        project.service<KimiProjectService>().registerPanel(panel)
        LOG.info("Panel registered to KimiProjectService")
        toolWindow.setTitleActions(listOf(RefitLayoutAction(panel)))

        val content = ContentFactory.getInstance().createContent(component, "", false)
        toolWindow.contentManager.addContent(content)

        if (shouldStartTerminal(project)) {
            val workDir = project.basePath ?: System.getProperty("user.home")
            LOG.info("Scheduling kimi startup, workDir=$workDir")
            panel.startProcessWhenReady(workDir)
        } else {
            LOG.info("Skipping kimi startup for default welcome-screen project")
            panel.showWelcomeScreenEmptyState()
        }

        LOG.info("Kimi Tool Window created successfully")
    }

    internal fun shouldStartTerminal(project: Project): Boolean = !project.isDefault
}
