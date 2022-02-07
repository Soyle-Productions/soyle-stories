package com.soyle.stories.storyevent.character.remove

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
import javafx.stage.Window
import tornadofx.*

typealias RemoveCharacterFromStoryEventPromptView = ConfirmationPromptView<RemoveCharacterFromStoryEventPromptViewModel>

fun removeCharacterFromStoryEventPrompt(
    scope: Scope = FX.defaultScope,
    ownerWindow: Window? = null,
    viewModel: RemoveCharacterFromStoryEventPromptViewModel = RemoveCharacterFromStoryEventPromptViewModel()
): ConfirmRemoveCharacterFromStoryEventPrompt {
    val locale = scope.get<RemoveCharacterFromStoryEventPromptLocale>()
    confirmationPrompt(scope, ownerWindow, viewModel) {
        confirmationText().bind(locale.remove())
        headerText().bind(locale.areYouSureYouWantToRemoveTheCharacterFromTheStoryEvent(viewModel.characterName(), viewModel.storyEventName()))
        titleProperty.bind(locale.confirmRemoveCharacterFromStoryEvent())
    }
    return viewModel
}