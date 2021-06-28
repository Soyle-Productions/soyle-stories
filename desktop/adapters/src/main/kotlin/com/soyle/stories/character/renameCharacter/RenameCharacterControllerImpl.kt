package com.soyle.stories.character.renameCharacter

import com.soyle.stories.usecase.character.renameCharacter.RenameCharacter
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.common.ThreadTransformer
import java.util.*

class RenameCharacterControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val renameCharacter: RenameCharacter,
  private val renameCharacterOutputPort: RenameCharacter.OutputPort
) : RenameCharacterController {

	override fun renameCharacter(characterId: String, newName: NonBlankString) {
		val formattedCharacterId = UUID.fromString(characterId)
		threadTransformer.async {
			renameCharacter.invoke(
			  formattedCharacterId,
			  newName,
			  renameCharacterOutputPort
			)
		}
	}
}