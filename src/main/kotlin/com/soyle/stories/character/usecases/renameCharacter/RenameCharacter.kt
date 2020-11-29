package com.soyle.stories.character.usecases.renameCharacter

import com.soyle.stories.character.CharacterException
import com.soyle.stories.common.NonBlankString
import java.util.*

interface RenameCharacter {

	suspend operator fun invoke(characterId: UUID, name: NonBlankString, output: OutputPort)

	class ResponseModel(val characterId: UUID, val newName: String, val affectedThemeIds: List<UUID>)

	interface OutputPort {
		fun receiveRenameCharacterFailure(failure: CharacterException)
		suspend fun receiveRenameCharacterResponse(response: ResponseModel)
	}

}