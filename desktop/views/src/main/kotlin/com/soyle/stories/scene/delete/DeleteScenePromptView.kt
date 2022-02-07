package com.soyle.stories.scene.delete

import com.soyle.stories.di.get
import com.soyle.stories.project.WorkBench
import com.soyle.stories.ramifications.confirmation.ConfirmationPromptView
import com.soyle.stories.ramifications.confirmation.confirmationPrompt
import tornadofx.*


typealias DeleteScenePromptView = ConfirmationPromptView<DeleteScenePromptViewModel>

fun deleteScenePrompt(
	scope: Scope = FX.defaultScope,
	viewModel: DeleteScenePromptViewModel = DeleteScenePromptViewModel()
): DeleteScenePromptViewModel {

	val locale = scope.get<DeleteSceneConfirmationPromptLocale>()

	return confirmationPrompt(scope, scope.get<WorkBench>().currentStage, viewModel) {
		confirmationText().bind(locale.remove)
		headerText().bind(locale.message(viewModel.name()))
		titleProperty.bind(locale.title)
	}
}