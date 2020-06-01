package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparison
import java.util.*

internal class RemoveCharacterFromLocalComparisonPresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : RemoveCharacterFromComparison.OutputPort {

	override fun receiveRemoveCharacterFromComparisonResponse(response: RemoveCharacterFromComparison.ResponseModel) {
		if (themeId != response.themeId) return
		view.update {
			copy(
			  isInvalid = true
			)
		}
	}

	override fun receiveRemoveCharacterFromComparisonFailure(failure: ThemeException) {}
}