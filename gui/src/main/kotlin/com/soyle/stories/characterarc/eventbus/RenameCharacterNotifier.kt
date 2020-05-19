package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.common.Notifier

class RenameCharacterNotifier : RenameCharacter.OutputPort, Notifier<RenameCharacter.OutputPort>() {
	override fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
		notifyAll { it.receiveRenameCharacterResponse(response) }
	}

	override fun receiveRenameCharacterFailure(failure: CharacterException) {
		notifyAll { it.receiveRenameCharacterFailure(failure) }
	}
}