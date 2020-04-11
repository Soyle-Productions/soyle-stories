package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.characterarc.LocalCharacterArcException
import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArc
import java.util.*

internal class DeleteLocalCharacterArcPresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : DeleteLocalCharacterArc.OutputPort {

	override fun receiveDeleteLocalCharacterArcResponse(response: DeleteLocalCharacterArc.ResponseModel) {
		if (response.themeId == themeId && !response.themeRemoved) {
			view.update {
				copy(
				  isInvalid = true
				)
			}
		}
	}

	override fun receiveDeleteLocalCharacterArcFailure(failure: LocalCharacterArcException) {}

}