package com.soyle.stories.character.usecases.renameCharacter

import com.soyle.stories.character.CharacterException
import java.util.*

interface RenameCharacter {

	suspend operator fun invoke(characterId: UUID, name: String, output: OutputPort)

	class ResponseModel(val characterId: UUID, val newName: String, val affectedThemeIds: List<UUID>)

	interface OutputPort {
		fun receiveRenameCharacterFailure(failure: CharacterException)
		fun receiveRenameCharacterResponse(response: ResponseModel)
	}

}