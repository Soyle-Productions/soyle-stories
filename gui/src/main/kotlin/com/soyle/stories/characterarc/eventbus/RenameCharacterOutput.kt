package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.renameCharacter.RenamedCharacterReceiver
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.common.Notifier

class RenameCharacterOutput(
	private val renamedCharacterReceiver: RenamedCharacterReceiver
) : RenameCharacter.OutputPort{
	override suspend fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
		renamedCharacterReceiver.receiveRenamedCharacter(response)
	}

	override fun receiveRenameCharacterFailure(failure: CharacterException) {
		throw failure
	}
}