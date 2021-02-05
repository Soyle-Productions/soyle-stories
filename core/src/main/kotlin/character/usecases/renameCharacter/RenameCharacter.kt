package com.soyle.stories.character.usecases.renameCharacter

import com.soyle.stories.common.NonBlankString
import com.soyle.stories.entities.CharacterRenamed
import com.soyle.stories.prose.MentionTextReplaced
import java.util.*

interface RenameCharacter {

    suspend operator fun invoke(characterId: UUID, name: NonBlankString, output: OutputPort)

    class ResponseModel(
        val characterRenamed: CharacterRenamed,
        val affectedThemeIds: List<UUID>,
        val mentionTextReplaced: List<MentionTextReplaced>
    )

    interface OutputPort {
        suspend fun receiveRenameCharacterResponse(response: ResponseModel)
    }

}