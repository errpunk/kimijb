package com.github.kimijb.action

import com.github.kimijb.service.KimiProjectService
import com.github.kimijb.ui.KimiTerminalPanel
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.Presentation
import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class KimiInsertContextActionTest {

    private lateinit var action: KimiInsertContextAction
    private val mockProject = mockk<Project>(relaxed = true)
    private val mockEditor = mockk<Editor>(relaxed = true)
    private val mockCaretModel = mockk<CaretModel>(relaxed = true)
    private val mockVirtualFile = mockk<VirtualFile>(relaxed = true)
    private val mockService = mockk<KimiProjectService>(relaxed = true)
    private val mockPanel = mockk<KimiTerminalPanel>(relaxed = true)

    @BeforeEach
    fun setup() {
        action = KimiInsertContextAction()
        every { mockEditor.caretModel } returns mockCaretModel
        every { mockCaretModel.logicalPosition } returns LogicalPosition(2, 0)
        every { mockEditor.virtualFile } returns mockVirtualFile
        every { mockVirtualFile.path } returns "/project/main.go"
    }

    @Test
    fun `update disables action when no editor is available`() {
        val presentation = Presentation()
        val event = mockk<AnActionEvent> {
            every { getData(CommonDataKeys.EDITOR) } returns null
            every { getPresentation() } returns presentation
        }
        action.update(event)
        assertFalse(presentation.isEnabled)
    }

    @Test
    fun `update enables action when editor is open`() {
        val presentation = Presentation()
        val event = mockk<AnActionEvent> {
            every { getData(CommonDataKeys.EDITOR) } returns mockEditor
            every { getPresentation() } returns presentation
        }
        action.update(event)
        assertTrue(presentation.isEnabled)
    }

    @Test
    fun `actionPerformed inserts context via service when editor open`() {
        every { mockService.getPanel() } returns mockPanel
        every { mockProject.getService(KimiProjectService::class.java) } returns mockService

        val event = mockk<AnActionEvent> {
            every { project } returns mockProject
            every { getData(CommonDataKeys.EDITOR) } returns mockEditor
        }
        action.actionPerformed(event)

        verify { mockService.insertContext("/project/main.go", 3) }
    }

    @Test
    fun `actionPerformed does nothing when no editor`() {
        every { mockProject.getService(KimiProjectService::class.java) } returns mockService

        val event = mockk<AnActionEvent> {
            every { project } returns mockProject
            every { getData(CommonDataKeys.EDITOR) } returns null
        }
        action.actionPerformed(event)

        verify(exactly = 0) { mockService.insertContext(any(), any()) }
    }
}
