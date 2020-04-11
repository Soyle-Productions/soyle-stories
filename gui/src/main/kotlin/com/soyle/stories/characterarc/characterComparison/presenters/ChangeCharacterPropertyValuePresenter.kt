package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue
import java.util.*

internal class ChangeCharacterPropertyValuePresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : ChangeCharacterPropertyValue.OutputPort {

	override fun receiveChangeCharacterPropertyValueResponse(response: ChangeCharacterPropertyValue.ResponseModel) {
		if (themeId != response.themeId) return
		view.update {
			copy(
			  isInvalid = true
			)
		}
	}

	override fun receiveChangeCharacterPropertyValueFailure(failure: ThemeException) {
	}
}