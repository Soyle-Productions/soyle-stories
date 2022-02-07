package com.soyle.stories.storyevent.remove

import com.soyle.stories.common.components.ComponentsStyles.Companion.filled
import com.soyle.stories.common.components.ComponentsStyles.Companion.outlined
import com.soyle.stories.common.components.ComponentsStyles.Companion.primary
import com.soyle.stories.common.components.ComponentsStyles.Companion.secondary
import com.soyle.stories.di.get
import com.soyle.stories.project.WorkBench
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptView
import com.soyle.stories.ramifications.confirmation.confirmationPrompt
import com.soyle.stories.storyevent.character.remove.RemoveCharacterFromStoryEventPromptLocale
import javafx.scene.Parent
import javafx.scene.control.*
import tornadofx.*
import javax.swing.text.Style

typealias RemoveStoryEventConfirmationPromptView = ConfirmationPromptView<RemoveStoryEventConfirmationPromptViewModel>

fun removeStoryEventConfirmationPrompt(
    scope: Scope = FX.defaultScope,
    viewModel: RemoveStoryEventConfirmationPromptViewModel = RemoveStoryEventConfirmationPromptViewModel()
): RemoveStoryEventConfirmationPrompt {

    val locale = scope.get<RemoveStoryEventConfirmationPromptLocale>()

    confirmationPrompt(scope, scope.get<WorkBench>().currentStage, viewModel) {
        confirmationText().bind(locale.remove())
        headerText().bind(locale.areYouSureYouWantToRemoveTheseStoryEventsFromTheProject(viewModel.items()))
        titleProperty.bind(locale.confirmRemoveStoryEventFromProject())
    }

    return viewModel
}