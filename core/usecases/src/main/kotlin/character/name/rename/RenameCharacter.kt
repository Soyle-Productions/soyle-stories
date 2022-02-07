package com.soyle.stories.usecase.character.name.rename

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.name.events.CharacterRenamed
import com.soyle.stories.domain.prose.events.MentionTextReplaced
import com.soyle.stories.domain.validation.NonBlankString

interface RenameCharacter {

    class RequestModel(
        val characterId: Character.Id,
        val name: NonBlankString,
        val replacement: NonBlankString
    )

    suspend operator fun invoke(request: RequestModel, output: OutputPort): Result<Nothing?>

    interface OutputPort {
        suspend fun characterRenamed(characterRenamed: CharacterRenamed)
        suspend fun mentionTextReplaced(mentionTextReplaced: MentionTextReplaced)
    }

}