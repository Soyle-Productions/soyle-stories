package com.soyle.stories.characterarc.characterComparison.presenters

import com.soyle.stories.characterarc.characterComparison.CharacterComparisonView
import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromComparison
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemovedCharacterFromTheme
import java.util.*

internal class RemoveCharacterFromLocalComparisonPresenter(
  private val themeId: UUID,
  private val view: CharacterComparisonView
) : RemoveCharacterFromComparison.OutputPort {

	override suspend fun characterArcDeleted(response: DeletedCharacterArc) {
		// do nothing
	}

	override fun receiveRemoveCharacterFromComparisonResponse(response: RemovedCharacterFromTheme) {
		if (themeId != response.themeId) return
		view.update {
			copy(
				isInvalid = true
			)
		}
	}

}