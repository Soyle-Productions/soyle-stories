package com.soyle.stories.usecase.character.renameCharacter

import com.soyle.stories.domain.character.CharacterRenamed
import com.soyle.stories.domain.prose.MentionTextReplaced
import com.soyle.stories.domain.scene.events.RenamedCharacterInScene
import com.soyle.stories.domain.validation.NonBlankString
import java.util.*

interface RenameCharacter {

    suspend operator fun invoke(characterId: UUID, name: NonBlankString, output: OutputPort)

    class ResponseModel(
        val characterRenamed: CharacterRenamed,
        val affectedThemeIds: List<UUID>,
        val renamedCharacterInScenes: List<RenamedCharacterInScene>,
        val mentionTextReplaced: List<MentionTextReplaced>
    )

    interface OutputPort {
        suspend fun receiveRenameCharacterResponse(response: ResponseModel)
    }

}