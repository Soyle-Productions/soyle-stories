package com.soyle.stories.characterarc.components

import com.soyle.stories.character.CharacterException
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.gui.View
import java.util.*

class CharacterNamePresenter(
  characterId: String,
  private val view: View<String>
) : RenameCharacter.OutputPort {

	private val characterId: UUID = UUID.fromString(characterId)

	override fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
		if (response.characterId != characterId) return
		view.update { response.newName }
	}
	override fun receiveRenameCharacterFailure(failure: CharacterException) {}

}