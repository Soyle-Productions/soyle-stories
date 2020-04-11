package com.soyle.stories.characterarc.usecaseControllers

import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromLocalComparison
import java.util.*

class RemoveCharacterFromLocalComparisonController(
  private val themeId: String,
  private val removeCharacterFromLocalComparison: RemoveCharacterFromLocalComparison,
  private val removeCharacterFromLocalComparisonOutputPort: RemoveCharacterFromLocalComparison.OutputPort
) {

	suspend fun removeCharacterFromComparison(characterId: String) {
		removeCharacterFromLocalComparison.invoke(
		  UUID.fromString(themeId),
		  UUID.fromString(characterId),
		  removeCharacterFromLocalComparisonOutputPort
		)
	}

}