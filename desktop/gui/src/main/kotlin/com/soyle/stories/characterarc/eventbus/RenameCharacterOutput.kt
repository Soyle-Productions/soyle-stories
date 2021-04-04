package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.usecase.character.renameCharacter.RenameCharacter
import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedReceiver
import com.soyle.stories.scene.charactersInScene.RenamedCharacterInSceneReceiver

class RenameCharacterOutput(
	private val characterRenamedReceiver: CharacterRenamedReceiver,
	private val renamedCharacterInSceneReceiver: RenamedCharacterInSceneReceiver,
	private val mentionTextReplacedReceiver: MentionTextReplacedReceiver
) : RenameCharacter.OutputPort {

	override suspend fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
		characterRenamedReceiver.receiveCharacterRenamed(response.characterRenamed)
		response.renamedCharacterInScenes.forEach {
			renamedCharacterInSceneReceiver.receiveRenamedCharacterInScene(it)
		}
		response.mentionTextReplaced.forEach {
			mentionTextReplacedReceiver.receiveMentionTextReplaced(it)
		}
	}
}