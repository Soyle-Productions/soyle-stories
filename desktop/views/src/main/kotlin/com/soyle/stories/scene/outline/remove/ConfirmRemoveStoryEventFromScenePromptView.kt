package com.soyle.stories.scene.outline.remove

import com.soyle.stories.di.get
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptView
import com.soyle.stories.ramifications.confirmation.confirmationPrompt
import com.soyle.stories.storyevent.coverage.uncover.ConfirmUncoverStoryEventPrompt
import javafx.stage.Stage
import tornadofx.FX
import tornadofx.Scope

typealias ConfirmRemoveStoryEventFromScenePromptView = ConfirmationPromptView<ConfirmRemoveStoryEventFromScenePromptViewModel>


fun confirmRemoveStoryEventFromScenePrompt(
    scope: Scope = FX.defaultScope,
    ownerWindow: Stage? = null,
    viewModel: ConfirmRemoveStoryEventFromScenePromptViewModel = ConfirmRemoveStoryEventFromScenePromptViewModel()
): ConfirmUncoverStoryEventPrompt {
    val locale = scope.get<ConfirmRemoveStoryEventFromScenePromptLocale>()
    confirmationPrompt(scope, ownerWindow, viewModel) {
        confirmationText().bind(locale.remove())
        headerText().bind(
            locale.areYouSureYouWantToRemoveTheStoryEventFromTheScene(
                viewModel.storyEventName(),
                viewModel.sceneName()
            )
        )
        titleProperty.bind(locale.confirmRemoveStoryEventFromScene())
    }
    return viewModel
}