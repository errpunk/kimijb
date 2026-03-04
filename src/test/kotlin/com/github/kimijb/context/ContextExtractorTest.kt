package com.github.kimijb.context

import com.intellij.openapi.editor.CaretModel
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.LogicalPosition
import com.intellij.openapi.vfs.VirtualFile
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ContextExtractorTest {

    private val mockEditor = mockk<Editor>()
    private val mockCaretModel = mockk<CaretModel>()
    private val mockVirtualFile = mockk<VirtualFile>()

    @Test
    fun `extractFilePath returns null when editor is null`() {
        assertNull(ContextExtractor.extractFilePath(null))
    }

    @Test
    fun `extractFilePath returns absolute path from virtual file`() {
        every { mockEditor.virtualFile } returns mockVirtualFile
        every { mockVirtualFile.path } returns "/a/b.go"
        assertEquals("/a/b.go", ContextExtractor.extractFilePath(mockEditor))
    }

    @Test
    fun `extractFilePath returns null when virtual file is null`() {
        every { mockEditor.virtualFile } returns null
        assertNull(ContextExtractor.extractFilePath(mockEditor))
    }

    @Test
    fun `extractLineNumber returns null when editor is null`() {
        assertNull(ContextExtractor.extractLineNumber(null))
    }

    @Test
    fun `extractLineNumber returns 1-based line for line index 0`() {
        every { mockEditor.caretModel } returns mockCaretModel
        every { mockCaretModel.logicalPosition } returns LogicalPosition(0, 0)
        assertEquals(1, ContextExtractor.extractLineNumber(mockEditor))
    }

    @Test
    fun `extractLineNumber returns 1-based line for line index 4`() {
        every { mockEditor.caretModel } returns mockCaretModel
        every { mockCaretModel.logicalPosition } returns LogicalPosition(4, 0)
        assertEquals(5, ContextExtractor.extractLineNumber(mockEditor))
    }

    @Test
    fun `formatContextText with both values formats as filepath colon line`() {
        assertEquals("@a/b.go:10", ContextExtractor.formatContextText("a/b.go", 10))
    }

    @Test
    fun `formatContextText with null filePath returns empty string`() {
        assertEquals("", ContextExtractor.formatContextText(null, 10))
    }

    @Test
    fun `formatContextText with null lineNumber returns filepath only`() {
        assertEquals("@a/b.go", ContextExtractor.formatContextText("a/b.go", null))
    }

    @Test
    fun `formatContextText with both null returns empty string`() {
        assertEquals("", ContextExtractor.formatContextText(null, null))
    }

    @Test
    fun `formatContextText with path containing spaces`() {
        assertEquals("@/a/my file.go:3", ContextExtractor.formatContextText("/a/my file.go", 3))
    }
}
