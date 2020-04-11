package com.soyle.stories.characterarc.usecaseControllers

import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter
import java.util.*

class PromoteMinorCharacterController(
  themeId: String,
  private val promoteMinorCharacter: PromoteMinorCharacter,
  private val promoteMinorCharacterOutputPort: PromoteMinorCharacter.OutputPort
) {

	private val themeId: UUID = UUID.fromString(themeId)

	suspend fun promoteCharacter(characterId: String) {
		PromoteMinorCharacter.RequestModel(
		  themeId,
		  UUID.fromString(characterId)
		)
		  .let { promoteMinorCharacter.invoke(it, promoteMinorCharacterOutputPort) }
	}
}