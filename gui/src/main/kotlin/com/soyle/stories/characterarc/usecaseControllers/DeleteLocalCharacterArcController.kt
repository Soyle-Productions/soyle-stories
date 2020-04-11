package com.soyle.stories.characterarc.usecaseControllers

import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArc
import java.util.*

class DeleteLocalCharacterArcController(
  themeId: String,
  private val deleteLocalCharacterArc: DeleteLocalCharacterArc,
  private val deleteLocalCharacterArcOutputPort: DeleteLocalCharacterArc.OutputPort
) {

	private val themeId: UUID = UUID.fromString(themeId)

	suspend fun demoteCharacter(characterId: String) {
		deleteLocalCharacterArc.invoke(
		  themeId,
		  UUID.fromString(characterId),
		  deleteLocalCharacterArcOutputPort
		)
	}

}