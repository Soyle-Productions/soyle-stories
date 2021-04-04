package com.soyle.stories.scene.sceneCharacters.characterEditor

import com.soyle.stories.characterarc.createArcSectionDialog.CreateArcSectionDialogView.Companion.createArcSectionDialog
import com.soyle.stories.characterarc.planCharacterArcDialog.planCharacterArcDialog
import com.soyle.stories.common.async
import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.common.exists
import com.soyle.stories.common.existsWhen
import com.soyle.stories.common.onLoseFocus
import com.soyle.stories.di.resolve
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.scene.sceneCharacters.AvailableArcSectionViewModel
import com.soyle.stories.scene.sceneCharacters.SceneCharactersState
import com.soyle.stories.scene.sceneCharacters.SceneCharactersViewListener
import javafx.animation.Interpolator
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.event.ActionEvent
import javafx.scene.control.*
import javafx.scene.layout.Pane
import javafx.scene.layout.Region.USE_COMPUTED_SIZE
import javafx.scene.layout.VBox
import javafx.scene.shape.Rectangle
import javafx.scene.text.FontWeight
import javafx.util.Duration
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import tornadofx.*
import java.lang.ref.WeakReference

class SelectedSceneCharacterEditorLogic(private val view: SelectedSceneCharacterEditor, private val state: CharacterEditorModel) {

    private val viewListener = view.resolve<SceneCharactersViewListener>()
    private val characterIsBeingEdited = state.itemProperty().isNotNull

    internal fun characterEditor(editor: Pane) {
        when (characterIsBeingEdited.value) {
            false -> {
                if (editor.parent == null) editor.exists = false
                else {
                    editor.fade(Duration.millis(300.0), 0.0) {
                        setOnFinished { editor.exists = false }
                    }
                }
            }
            true -> {
                editor.opacity = 1.0
                editor.exists = true
                editor.translateYProperty().animate(0, Duration.millis(300.0), Interpolator.EASE_IN)
            }
        }
        val editorRef = WeakReference(editor)
        characterIsBeingEdited.onChangeOnce {
            editorRef.get()?.let(::characterEditor)
        }
    }

    internal fun editorBody(body: Pane) {
        if (body.clip !is Rectangle) {
            body.clip = Rectangle().apply {
                widthProperty().bind(body.widthProperty())
                heightProperty().bind(body.heightProperty())
            }
        }
        when (characterIsBeingEdited.value) {
            false -> {
                if (body.parent == null) {
                    body.minHeight = 0.0
                    body.maxHeight = 0.0
                }
            }
            true -> {
                if (body.parent != null) {
                    body.minHeight = 0.0
                    body.maxHeight = 0.0
                    body.maxHeightProperty().animate(body.scene.height, Duration.millis(300.0), Interpolator.EASE_IN) {
                        setOnFinished {
                            body.minHeight = USE_COMPUTED_SIZE
                            body.maxHeight = USE_COMPUTED_SIZE
                        }
                    }
                } else {
                    body.minHeight = USE_COMPUTED_SIZE
                    body.maxHeight = USE_COMPUTED_SIZE
                }
            }
        }
        val bodyRef = WeakReference(body)
        characterIsBeingEdited.onChangeOnce {
            bodyRef.get()?.let(::editorBody)
        }
    }

    internal fun editorFooter(footer: Pane) {
        footer.existsWhen(characterIsBeingEdited)
    }


    internal fun closeEditor(e: ActionEvent) {
        state.resolve<SceneCharactersState>().characterBeingEdited.set(null)
    }

    internal fun incitingCharacterToggle(button: ButtonBase) {
        button.action {

        }
    }

    internal fun opponentCharacterToggle(button: ButtonBase) {
        button.action {

        }
    }

    internal fun desire(input: TextInputControl)
    {
        input.onLoseFocus {
            val characterId = state.item?.id ?: return@onLoseFocus
            val newDesire = input.text
            if (newDesire != state.desire.value) {
                // TODO("viewListener.setDesire(characterId, newDesire)")
            }
        }
    }

    internal fun motivation(input: TextInputControl)
    {
        input.onLoseFocus {
            val character = state.item ?: return@onLoseFocus
            val newMotivation = input.text
            if (newMotivation != character.motivation) {
                viewListener.setMotivation(character.id, newMotivation)
            }
        }
    }

    internal fun resetToPreviousMotivationButton(button: ButtonBase)
    {
        button.action {
            val character = state.item ?: return@action
            viewListener.resetMotivation(character.id)
        }
    }

    internal fun previousMotivationLink(link: Hyperlink) {
        link.action {
            state.previousMotivation.value?.let {
                state.resolve<SceneCharactersState>().selectScene(it.sourceSceneId, it.sourceSceneName)
            }
        }
    }

    internal fun coverArcSectionButton(menuButton: MenuButton) {
        menuButton.setOnShowing {
            val characterBeingEdited = state.item ?: return@setOnShowing
            state.resolve<SceneCharactersState>().characterBeingEdited.set(
                characterBeingEdited.copy(availableCharacterArcSections = null)
            )
            viewListener.getAvailableCharacterArcSections(characterBeingEdited.id)
        }
    }

    internal fun createCharacterArcOption(menuItem: MenuItem)
    {
        menuItem.action {
            val characterId = state.item?.id ?: return@action
            planCharacterArcDialog(view.scope, characterId.uuid.toString(), view.currentStage)
        }
    }

    internal fun createArcSectionOption(themeId: Theme.Id) = fun (menuItem: MenuItem)
    {
        menuItem.action {
            val characterId = state.item?.id ?: return@action
            createArcSectionDialog(view.scope, characterId, themeId) { arcSectionId, _ ->
                viewListener.coverCharacterArcSectionInScene(
                    characterId, listOf(arcSectionId.uuid.toString()), emptyList()
                )
            }
        }
    }

    internal fun availableArcSectionOption(menuItem: MenuItem)
    {
        menuItem.action {
            val characterId = state.item?.id ?: return@action
            val section = menuItem.userData as? AvailableArcSectionViewModel ?: return@action
            if (section.isCovered) {
                viewListener.coverCharacterArcSectionInScene(
                    characterId, emptyList(), listOf(section.arcSectionId.uuid.toString())
                )
            } else {
                viewListener.coverCharacterArcSectionInScene(
                    characterId, listOf(section.arcSectionId.uuid.toString()), emptyList()
                )
            }
        }
    }

    internal fun coveredArcSection(section: Chip) {
        section.onDelete {
            val characterId = state.item?.id ?: return@onDelete
            viewListener.coverCharacterArcSectionInScene(
                characterId, emptyList(), listOf(section.id)
            )
        }
    }

    internal fun removeCharacterButton(button: ButtonBase)
    {
        button.action {
            val characterId = state.item?.id ?: return@action
            viewListener.removeCharacter(characterId)
        }
    }

}