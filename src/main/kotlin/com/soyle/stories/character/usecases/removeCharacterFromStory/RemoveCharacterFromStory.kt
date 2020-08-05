package com.soyle.stories.character.usecases.removeCharacterFromStory

import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemovedCharacterFromTheme
import java.util.*

interface RemoveCharacterFromStory {
    suspend operator fun invoke(characterId: UUID, output: OutputPort)

    class ResponseModel(
        val removedCharacter: RemovedCharacter,
        val removedCharacterFromThemes: List<RemovedCharacterFromTheme>
    )

    interface OutputPort {
        suspend fun receiveRemoveCharacterFromStoryResponse(response: ResponseModel)
    }
}