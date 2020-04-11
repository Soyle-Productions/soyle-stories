package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.characterarc.characterComparison.MoralProblemSubToolViewModel
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCentralMoralQuestion.ChangeCentralMoralQuestion
import java.util.*

internal class ChangeCentralMoralQuestionPresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : ChangeCentralMoralQuestion.OutputPort {

	override fun receiveChangeCentralMoralQuestionResponse(response: ChangeCentralMoralQuestion.ResponseModel) {
		if (themeId != response.themeId) return
		view.update {
			val moralProblemSubTool = subTools.filterIsInstance<MoralProblemSubToolViewModel>().singleOrNull()
			  ?: return@update copy(isInvalid = true)

			copy(
			  subTools = subTools.map { if (it == moralProblemSubTool) moralProblemSubTool.copy(centralMoralQuestion = response.newQuestion) else it }
			)
		}
	}

	override fun receiveChangeCentralMoralQuestionFailure(failure: ThemeException) {}
}