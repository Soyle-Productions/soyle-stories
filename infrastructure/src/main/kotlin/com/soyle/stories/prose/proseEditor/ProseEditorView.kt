package com.soyle.stories.prose.proseEditor

import com.soyle.stories.di.resolve
import javafx.scene.Parent
import tornadofx.Fragment
import tornadofx.textarea

class ProseEditorView : Fragment() {

    private val viewListener = resolve<ProseEditorViewListener>()
    private val state = resolve<ProseEditorState>()

    override val root: Parent = textarea(state.content) {
        isWrapText = true

    }

    init {
        viewListener.getValidState()
    }

}