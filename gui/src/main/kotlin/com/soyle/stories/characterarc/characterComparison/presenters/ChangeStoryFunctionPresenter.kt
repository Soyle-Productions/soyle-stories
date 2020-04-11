package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.theme.usecases.changeStoryFunction.ChangeStoryFunction
import java.util.*

internal class ChangeStoryFunctionPresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : ChangeStoryFunction.OutputPort {


	override fun receiveChangeStoryFunctionResponse(response: ChangeStoryFunction.ResponseModel) {
		if (themeId != response.themeId) return
		view.update {
			copy(
			  isInvalid = true
			)
		}
	}

	override fun receiveChangeStoryFunctionFailure(failure: Exception) {}
}