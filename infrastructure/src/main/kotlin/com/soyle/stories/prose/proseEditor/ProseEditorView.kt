package com.soyle.stories.prose.proseEditor

import com.soyle.stories.di.resolve
import com.soyle.stories.entities.ProseMention
import javafx.collections.ObservableList
import javafx.scene.Parent
import org.fxmisc.richtext.StyleClassedTextArea
import tornadofx.Fragment
import tornadofx.addClass
import tornadofx.onChange

class ProseEditorView : Fragment() {

    private val viewListener = resolve<ProseEditorViewListener>()
    private val state = resolve<ProseEditorState>()

    val textArea = StyleClassedTextArea()

    override val root: Parent = textArea.apply {
        addClass("prose-editor")
        isWrapText = true
    }

    init {
        state.content.onChange {
            textArea.replace(0, textArea.text.length, it, "")
        }
        state.mentions.onChange { list: ObservableList<ProseMention<*>>? ->
            if (list == null) return@onChange
            textArea.clearStyle(0, textArea.content.length)
            list.forEach {
                textArea.setStyle(it.position.index, it.position.index + it.position.length, listOf(it.entityId.id.toString()))
            }
        }
        viewListener.getValidState()
    }

}