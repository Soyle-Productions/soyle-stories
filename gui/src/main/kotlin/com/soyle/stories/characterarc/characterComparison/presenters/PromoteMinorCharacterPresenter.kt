package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter
import java.util.*

internal class PromoteMinorCharacterPresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : PromoteMinorCharacter.OutputPort {

	override fun receivePromoteMinorCharacterFailure(failure: ThemeException) {
		if (failure.themeId == themeId) {
			view.update { this }
			throw failure
		}
	}

	override fun receivePromoteMinorCharacterResponse(response: PromoteMinorCharacter.ResponseModel) {
		view.update {
			copy(
			  isInvalid = true
			)
		}
	}

}