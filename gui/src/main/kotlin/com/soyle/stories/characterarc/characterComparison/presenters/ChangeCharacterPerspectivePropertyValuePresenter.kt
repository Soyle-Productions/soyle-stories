package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue
import java.util.*

class ChangeCharacterPerspectivePropertyValuePresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : ChangeCharacterPerspectivePropertyValue.OutputPort {

	override fun receiveChangeCharacterPerspectivePropertyValueResponse(response: ChangeCharacterPerspectivePropertyValue.ResponseModel) {
		if (themeId != response.themeId) return
		view.update {
			copy(
			  isInvalid = true
			)
		}
	}

	override fun receiveChangeCharacterPerspectivePropertyValueFailure(failure: ThemeException) {
	}
}