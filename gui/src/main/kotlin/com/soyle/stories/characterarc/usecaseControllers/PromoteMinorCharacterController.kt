package com.soyle.stories.characterarc.usecaseControllers

import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter
import java.util.*

class PromoteMinorCharacterController(
  private val promoteMinorCharacter: PromoteMinorCharacter,
  private val promoteMinorCharacterOutputPort: PromoteMinorCharacter.OutputPort
) {

	suspend fun promoteCharacter(themeId: String, characterId: String) {
		val preparedThemeId = UUID.fromString(themeId)
		val preparedCharacterId = UUID.fromString(characterId)
		PromoteMinorCharacter.RequestModel(
			preparedThemeId,
			preparedCharacterId
		)
		  .let { promoteMinorCharacter.invoke(it, promoteMinorCharacterOutputPort) }
	}
}