package com.soyle.stories.characterarc.usecaseControllers

import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeleteCharacterArc
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter
import java.util.*

class DeleteLocalCharacterArcController(
  themeId: String,
  private val deleteLocalCharacterArc: DeleteCharacterArc,
  private val deleteLocalCharacterArcOutputPort: DemoteMajorCharacter.OutputPort
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