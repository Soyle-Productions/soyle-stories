package com.soyle.stories.characterarc.components

import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.domain.character.name.events.CharacterRenamed
import com.soyle.stories.gui.View
import java.util.*

class CharacterNamePresenter(
  characterId: String,
  private val view: View<String>
) : CharacterRenamedReceiver {

	private val characterId: UUID = UUID.fromString(characterId)

	override suspend fun receiveCharacterRenamed(characterRenamed: CharacterRenamed) {
		if (characterRenamed.characterId.uuid != characterId) return
		view.updateOrInvalidated { characterRenamed.name }
	}

}