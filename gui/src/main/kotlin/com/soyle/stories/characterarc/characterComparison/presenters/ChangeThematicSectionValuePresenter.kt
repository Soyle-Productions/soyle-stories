package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValue

internal class ChangeThematicSectionValuePresenter(
  private val view: CharacterComparisonView
) : ChangeThematicSectionValue.OutputPort {

	override fun receiveChangeThematicSectionValueResponse(response: ChangeThematicSectionValue.ResponseModel) {
		view.update {
			copy(
			  isInvalid = true
			)
		}

	}

	override fun receiveChangeThematicSectionValueFailure(failure: Exception) {}

}