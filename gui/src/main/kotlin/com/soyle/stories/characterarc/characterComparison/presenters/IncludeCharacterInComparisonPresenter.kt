package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison
import java.util.*

internal class IncludeCharacterInComparisonPresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : IncludeCharacterInComparison.OutputPort {

	override fun receiveIncludeCharacterInComparisonResponse(response: CharacterIncludedInTheme) {
		if (response.themeId != themeId) return
		view.update {
			copy(
				isInvalid = true
			)
		}
	}

	override fun receiveIncludeCharacterInComparisonFailure(failure: Exception) {}
}