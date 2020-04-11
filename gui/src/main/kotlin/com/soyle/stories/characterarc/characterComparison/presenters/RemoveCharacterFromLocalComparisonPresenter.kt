package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.theme.LocalThemeException
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromLocalComparison
import java.util.*

internal class RemoveCharacterFromLocalComparisonPresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : RemoveCharacterFromLocalComparison.OutputPort {

	override fun receiveRemoveCharacterFromLocalComparisonResponse(response: RemoveCharacterFromLocalComparison.ResponseModel) {
		if (themeId != response.themeId || response.themeRemoved) return
		view.update {
			copy(
			  isInvalid = true
			)
		}
	}

	override fun receiveRemoveCharacterFromLocalComparisonFailure(failure: LocalThemeException) {

	}
}