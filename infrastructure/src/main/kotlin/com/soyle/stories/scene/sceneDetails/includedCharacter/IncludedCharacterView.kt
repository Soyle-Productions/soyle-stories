package com.soyle.stories.scene.sceneDetails.includedCharacter

import com.soyle.stories.common.components.*
import com.soyle.stories.common.components.menuChipGroup.MenuChipGroup
import com.soyle.stories.common.components.menuChipGroup.menu
import com.soyle.stories.common.components.menuChipGroup.menuchipgroup
import com.soyle.stories.common.existsWhen
import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.di.get
import javafx.beans.property.SimpleIntegerProperty
import javafx.collections.MapChangeListener
import javafx.collections.ObservableList
import javafx.collections.ObservableMap
import javafx.scene.Parent
import javafx.scene.control.CheckBox
import javafx.scene.control.Label
import javafx.scene.control.MenuItem
import javafx.scene.layout.Priority
import org.controlsfx.control.PopOver
import tornadofx.*
import tornadofx.controlsfx.popover
import java.lang.ref.WeakReference

class IncludedCharacterView : View() {

    private val state = scope.get<IncludedCharacterInSceneState>()
    private val viewListener = scope.get<IncludedCharacterInSceneViewListener>()

    override val root: Parent = card {
        addClass("included-character")

        // build children
        cardHeader {
            characterName()
            removeCharacterButton().apply { hgrow = Priority.NEVER }
        }
        cardBody(isFirstChild = false) {
            positionOnArcSelectionField()
            characterMotivationField()
        }
    }

    init {
        root.idProperty().bind(state.characterId)
    }

    private fun Parent.characterName() = fieldLabel(state.characterName).apply {
        addClass("character-name")
    }

    private fun Parent.removeCharacterButton() = button(state.removeCharacterLabel) {
        action(::removeCharacter)
    }

    private fun Parent.positionOnArcSelectionField() {
        labeledSection(state.positionOnCharacterArcLabel) {
            PositionOnArcSelection(this, state, viewListener)
        }
    }

    private fun Parent.characterMotivationField() =
        labeledSection(state.motivationFieldLabel) {
            addClass("motivation")
            motivationTextField()
            hbox {
                resetButton()
                lastChangedButton()
            }
        }

    private fun Parent.motivationTextField() =
        textfield(state.motivation) {
            onLoseFocus { updateMotivationIfDifferent(text) }
        }

    private fun Parent.resetButton() = hyperlink(state.resetMotivationLabel) {
        addClass("reset-button")
        existsWhen { state.motivationCanBeReset }
        action { resetMotivation() }
    }

    private fun Parent.lastChangedButton() = hyperlink(state.motivationLastChangedLabel) {
        addClass("previously-set-tip")
        existsWhen { state.previousMotivation.isNotNull }
        val popover = popover(
            arrowLocation = PopOver.ArrowLocation.TOP_CENTER
        ) {
            vbox {
                previousMotivationSourceButton(onNavigateToSource = { this@popover.hide() })
                previousMotivationValue()
                setOnMouseExited { this@popover.hide() }
            }
        }
        action { popover.show(this) }
    }

    private fun Parent.previousMotivationSourceButton(onNavigateToSource: () -> Unit = {}) =
        hyperlink(state.previousMotivationSource) {
            action {
                if (navigateToMotivationSourceScene()) {
                    onNavigateToSource()
                }
            }
        }

    private fun Parent.previousMotivationValue() =
        text(state.previousMotivationValue) {
            addClass("motivation")
        }


    private fun removeCharacter() {
        viewListener.removeCharacter()
    }

    /**
     * @return true if navigation was successful
     */
    private fun navigateToMotivationSourceScene(): Boolean {
        state.previousMotivationSource.value?.let {
            viewListener.openSceneDetails(it)
        } ?: return false
        return true
    }

    private fun resetMotivation()
    {
        if (! state.motivationCanBeReset.get()) return
        viewListener.resetMotivation()
    }

    private fun updateMotivationIfDifferent(text: String)
    {
        val motivationValue = state.motivation.value ?: return
        if (text != motivationValue) {
            viewListener.setMotivation(text)
        }
    }

}