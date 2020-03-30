package com.soyle.stories.character.usecases.removeCharacterFromLocalStory

import com.soyle.stories.character.CharacterException
import java.util.*

interface RemoveCharacterFromLocalStory {

    suspend operator fun invoke(characterId: UUID, outputPort: OutputPort)

    class ResponseModel(val characterId: UUID, val removedThemes: List<UUID>, val updatedThemes: List<UUID>, val removedTools: List<UUID>)

    interface OutputPort {
        fun receiveRemoveCharacterFromLocalStoryResponse(response: ResponseModel)
        fun receiveRemoveCharacterFromLocalStoryFailure(failure: CharacterException)
    }
}