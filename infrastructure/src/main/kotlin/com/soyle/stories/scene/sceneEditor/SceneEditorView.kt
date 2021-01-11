package com.soyle.stories.scene.sceneEditor

import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.prose.proseEditor.ProseEditorView
import javafx.scene.Parent
import javafx.scene.layout.Priority
import tornadofx.View
import tornadofx.hbox
import tornadofx.hgrow

class SceneEditorView : View() {

    override val scope: SceneEditorScope = super.scope as SceneEditorScope
    private val viewListener = resolve<SceneEditorViewListener>()
    val state = resolve<SceneEditorState>()

    override val root: Parent = hbox {
        isFillHeight = true
        val proseEditor = ProseEditorScope(
            scope.projectScope,
            scope.type.proseId,
            viewListener::loadMentionSuggestionsForScene,
            viewListener::useProseMentionInScene
        ).get<ProseEditorView>()
        add(proseEditor)
        proseEditor.root.hgrow = Priority.ALWAYS
    }

}