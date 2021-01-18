package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.character.usecases.renameCharacter.RenameCharacter
import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedReceiver

class RenameCharacterOutput(
	private val characterRenamedReceiver: CharacterRenamedReceiver,
	private val mentionTextReplacedReceiver: MentionTextReplacedReceiver
) : RenameCharacter.OutputPort{

	override suspend fun receiveRenameCharacterResponse(response: RenameCharacter.ResponseModel) {
		characterRenamedReceiver.receiveCharacterRenamed(response.characterRenamed)
		response.mentionTextReplaced.forEach {
			mentionTextReplacedReceiver.receiveMentionTextReplaced(it)
		}
	}
}