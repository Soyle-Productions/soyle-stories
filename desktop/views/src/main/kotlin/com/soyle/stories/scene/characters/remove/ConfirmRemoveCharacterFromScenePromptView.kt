package com.soyle.stories.scene.characters.remove

import com.soyle.stories.di.get
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptView
import com.soyle.stories.ramifications.confirmation.confirmationPrompt
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage
import javafx.stage.Window
import tornadofx.*

typealias ConfirmRemoveCharacterFromScenePromptView = ConfirmationPromptView<ConfirmRemoveCharacterFromScenePromptViewModel>

fun confirmRemoveCharacterFromScenePrompt(
    scope: Scope = FX.defaultScope,
    ownerWindow: Window? = null,
    viewModel: ConfirmRemoveCharacterFromScenePromptViewModel = ConfirmRemoveCharacterFromScenePromptViewModel()
): ConfirmRemoveCharacterFromScenePromptViewModel {
    val locale = scope.get<ConfirmRemoveCharacterFromScenePromptLocale>()
    return confirmationPrompt(scope, ownerWindow, viewModel) {
        confirmationText().bind(locale.remove())
        headerText().bind(locale.confirmRemoveCharacterFromSceneMessage(viewModel.characterName(), viewModel.sceneName()))
        content = VBox().apply {
            bindChildren(viewModel.items) {
                label(it.storyEventName) {
                    id = it.storyEventId.toString()
                }
            }
        }
        titleProperty.bind(locale.confirmRemoveCharacterFromSceneTitle())
    }
}