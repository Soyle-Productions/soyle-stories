package com.soyle.stories.character.delete

import com.soyle.stories.character.removeCharacterFromStory.ConfirmationPrompt
import com.soyle.stories.di.get
import com.soyle.stories.di.resolve
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptView
import com.soyle.stories.ramifications.confirmation.confirmationPrompt
import javafx.scene.Parent
import javafx.scene.control.Alert
import javafx.scene.control.ButtonBar
import javafx.scene.control.ButtonType
import javafx.stage.Stage
import tornadofx.*

typealias ConfirmDeleteCharacterPromptView = ConfirmationPromptView<ConfirmDeleteCharacterPromptViewModel>

fun confirmDeleteCharacterPrompt(
    scope: Scope = FX.defaultScope,
    ownerWindow: Stage? = null,
    viewModel: ConfirmDeleteCharacterPromptViewModel = ConfirmDeleteCharacterPromptViewModel()
): ConfirmationPrompt {
    val locale = scope.get<ConfirmDeleteCharacterPromptLocale>()
    confirmationPrompt(scope, ownerWindow, viewModel) {
        confirmationText().bind(locale.remove())
        headerText().bind(locale.confirmDeleteCharacterMessage(viewModel.characterName()))
        titleProperty.bind(locale.confirmDeleteCharacterTitle())
    }
    return viewModel
}