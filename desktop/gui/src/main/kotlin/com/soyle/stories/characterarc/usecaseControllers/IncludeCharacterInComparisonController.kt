package com.soyle.stories.characterarc.usecaseControllers

import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison
import java.util.*

class IncludeCharacterInComparisonController(
  themeId: String,
  private val includeCharacterInComparison: IncludeCharacterInComparison,
  private val includeCharacterInComparisonOutputPort: IncludeCharacterInComparison.OutputPort
) {

	private val themeId: UUID = UUID.fromString(themeId)

	suspend fun addCharacterToComparison(characterId: String) {
		includeCharacterInComparison.invoke(
		  UUID.fromString(characterId),
		  themeId,
		  includeCharacterInComparisonOutputPort
		)
	}

}