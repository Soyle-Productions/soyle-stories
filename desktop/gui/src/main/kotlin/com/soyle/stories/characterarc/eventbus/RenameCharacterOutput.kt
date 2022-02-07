package com.soyle.stories.characterarc.eventbus

import com.soyle.stories.character.renameCharacter.CharacterRenamedReceiver
import com.soyle.stories.domain.character.name.events.CharacterRenamed
import com.soyle.stories.domain.prose.events.MentionTextReplaced
import com.soyle.stories.usecase.character.name.rename.RenameCharacter
import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedReceiver

class RenameCharacterOutput(
	private val characterRenamedReceiver: CharacterRenamedReceiver,
	private val mentionTextReplacedReceiver: MentionTextReplacedReceiver
) : RenameCharacter.OutputPort {

	override suspend fun characterRenamed(characterRenamed: CharacterRenamed) {
		characterRenamedReceiver.receiveCharacterRenamed(characterRenamed)
	}

	override suspend fun mentionTextReplaced(mentionTextReplaced: MentionTextReplaced) {
		mentionTextReplacedReceiver.receiveMentionTextReplaced(mentionTextReplaced)
	}
}