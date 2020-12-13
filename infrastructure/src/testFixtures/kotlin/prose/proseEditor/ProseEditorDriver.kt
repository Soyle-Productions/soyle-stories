package com.soyle.stories.desktop.view.prose.proseEditor

import com.soyle.stories.common.EntityId
import com.soyle.stories.di.get
import com.soyle.stories.prose.proseEditor.ProseEditorState
import com.soyle.stories.prose.proseEditor.ProseEditorView

class ProseEditorDriver(private val proseEditor: ProseEditorView) {

    fun getContent(): String
    {
        return (proseEditor.textArea).content.text
    }

    fun getMentionAt(start: Int, end: Int): EntityId<*>? = proseEditor.textArea.getStyleOfChar(start).firstOrNull()?.let { idText ->
        proseEditor.scope.get<ProseEditorState>().mentions.find {
            it.entityId.id.toString() == idText
        }?.entityId
    }

}