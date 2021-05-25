package com.soyle.stories.character.nameVariant.rename

import com.soyle.stories.prose.mentionTextReplaced.MentionTextReplacedReceiver
import com.soyle.stories.usecase.character.nameVariant.rename.RenameCharacterNameVariant

class RenameCharacterNameVariantOutput(
    private val characterNameVariantRenamedReceiver: CharacterNameVariantRenamedReceiver,
    private val mentionTextReplacedReceiver: MentionTextReplacedReceiver
) : RenameCharacterNameVariant.OutputPort {

    override suspend fun characterArcNameVariantRenamed(response: RenameCharacterNameVariant.ResponseModel) {
        characterNameVariantRenamedReceiver.receiveCharacterNameVariantRenamed(response.characterNameVariantRenamed)
        response.mentionTextReplaced.forEach { mentionTextReplacedReceiver.receiveMentionTextReplaced(it) }
    }
}