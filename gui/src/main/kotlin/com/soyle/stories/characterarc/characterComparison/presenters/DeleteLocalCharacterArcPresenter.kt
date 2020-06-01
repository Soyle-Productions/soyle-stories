package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter
import java.util.*

internal class DeleteLocalCharacterArcPresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : DemoteMajorCharacter.OutputPort {

	override fun receiveDemoteMajorCharacterResponse(response: DemoteMajorCharacter.ResponseModel) {
		if (response.themeId == themeId && !response.themeRemoved) {
			view.update {
				copy(
				  isInvalid = true
				)
			}
		}
	}

	override fun receiveDemoteMajorCharacterFailure(failure: Exception) {}

}