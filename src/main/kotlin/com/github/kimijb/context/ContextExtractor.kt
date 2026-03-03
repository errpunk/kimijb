package com.github.kimijb.context

import com.intellij.openapi.editor.Editor

object ContextExtractor {

    fun extractFilePath(editor: Editor?): String? {
        return editor?.virtualFile?.path
    }

    fun extractLineNumber(editor: Editor?): Int? {
        editor ?: return null
        return editor.caretModel.logicalPosition.line + 1
    }

    fun formatContextText(filePath: String?, lineNumber: Int?): String {
        if (filePath == null) return ""
        return if (lineNumber != null) "$filePath:$lineNumber" else filePath
    }
}
