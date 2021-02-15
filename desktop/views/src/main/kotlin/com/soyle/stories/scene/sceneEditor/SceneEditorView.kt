package com.soyle.stories.scene.sceneEditor

import com.soyle.stories.common.existsWhen
import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.prose.proseEditor.ProseEditorScope
import com.soyle.stories.prose.proseEditor.ProseEditorView
import javafx.beans.property.SimpleBooleanProperty
import javafx.geometry.Orientation
import javafx.geometry.Pos
import javafx.scene.Parent
import javafx.scene.layout.Priority
import tornadofx.*

class SceneEditorView : View() {

    override val scope: SceneEditorScope = super.scope as SceneEditorScope
    private val viewListener = resolve<SceneEditorViewListener>()
    val state = resolve<SceneEditorState>()

    private val proseEditor = ProseEditorScope(
        scope.projectScope,
        scope.type.proseId,
        viewListener::loadMentionSuggestionsForScene,
        viewListener::useProseMentionInScene,
        viewListener::loadMentionReplacements
    ).get<ProseEditorView>()

    private val conflictInput = textfield {
        state.conflict.onChange { text = it }
        onLoseFocus { viewListener.changeConflict(text) }
    }
    private val resolutionInput = textfield {
        state.resolution.onChange { text = it }
        onLoseFocus { viewListener.changeResolution(text) }
    }

    private val showConflictAndResolution = SimpleBooleanProperty(true)
    private fun toggleShowConflictAndResolution() {
        showConflictAndResolution.value = ! showConflictAndResolution.value
    }

    override val root: Parent = vbox {
        isFillWidth = true
        form {
            existsWhen(showConflictAndResolution)
            fieldset(labelPosition = Orientation.HORIZONTAL) {
                field("Conflict") {
                    id = "conflict-field"
                    add(conflictInput)
                }
                field("Resolution") {
                    id = "resolution-field"
                    add(resolutionInput)
                }
            }
        }
        hbox(alignment = Pos.BASELINE_RIGHT) {
            val conflictAndResolutionToggleText = showConflictAndResolution.stringBinding {
                if (it == true) "Hide Scene Conflict and Resolution"
                else "Show Scene Conflict and Resolution"
            }
            button(conflictAndResolutionToggleText) {
                action { toggleShowConflictAndResolution() }
            }
        }
        hbox {
            vgrow = Priority.ALWAYS
            isFillHeight = true
            add(proseEditor)
            proseEditor.root.hgrow = Priority.ALWAYS
        }
    }

    private fun getValidStateIfInvalid(invalid: Boolean) {
        if (invalid) viewListener.getValidState()
    }

    init {
        state.invalidatedProperty().onChange { getValidStateIfInvalid(it) }
        getValidStateIfInvalid(state.invalidated)
    }

}