package com.soyle.stories.characterarc.components

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.renameCharacter.RenamedCharacterReceiver
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.gui.View
import java.util.*

class CharacterNamePresenter(
  characterId: String,
  private val view: View<String>
) : RenamedCharacterReceiver {

	private val characterId: UUID = UUID.fromString(characterId)

	override suspend fun receiveRenamedCharacter(renamedCharacter: RenameCharacter.ResponseModel) {
		if (renamedCharacter.characterId != characterId) return
		view.update { renamedCharacter.newName }
	}

}