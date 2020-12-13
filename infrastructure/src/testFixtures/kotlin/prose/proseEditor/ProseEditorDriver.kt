package com.soyle.stories.desktop.view.prose.proseEditor

import com.soyle.stories.prose.proseEditor.ProseEditorView
import javafx.scene.control.TextInputControl

class ProseEditorDriver(private val proseEditor: ProseEditorView) {

    fun getContent(): String
    {
        return (proseEditor.root as TextInputControl).text
    }

}