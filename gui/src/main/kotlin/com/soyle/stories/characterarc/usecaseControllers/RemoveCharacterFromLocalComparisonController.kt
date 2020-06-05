package com.soyle.stories.characterarc.usecaseControllers

import com.soyle.stories.character.usecases.removeCharacterFromStory.RemoveCharacterFromStory
import java.util.*

class RemoveCharacterFromLocalComparisonController(
  private val removeCharacterFromLocalComparison: RemoveCharacterFromStory,
  private val removeCharacterFromLocalComparisonOutputPort: RemoveCharacterFromStory.OutputPort
) {

	suspend fun removeCharacterFromComparison(characterId: String) {
		removeCharacterFromLocalComparison.invoke(
		  UUID.fromString(characterId),
		  removeCharacterFromLocalComparisonOutputPort
		)
	}

}