package com.soyle.stories.scene.sceneEditor

import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.prose.proseEditor.ProseEditorView
import com.soyle.stories.scene.SceneTargeted
import com.soyle.stories.scene.items.SceneItemViewModel
import javafx.scene.Parent
import javafx.scene.layout.Priority
import tornadofx.*

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
            viewListener::useProseMentionInScene,
            viewListener::loadMentionReplacements
        ).get<ProseEditorView>()
        add(proseEditor)
        proseEditor.root.hgrow = Priority.ALWAYS
    }

    init {
        root.focusedProperty().onChange {
            if (it) FX.eventbus.fire(
                SceneTargeted(
                    SceneItemViewModel(
                        scope.sceneId.uuid.toString(),
                        scope.type.proseId,
                        "Dummy Scene Name",
                        0,
                        false
                    )
                )
            )
        }
    }

}