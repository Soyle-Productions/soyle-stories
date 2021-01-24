package com.soyle.stories.character.usecases.removeCharacterFromStory

import com.soyle.stories.entities.Character
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemovedCharacterFromTheme
import java.util.*

interface RemoveCharacterFromStory {
    suspend operator fun invoke(characterId: UUID, confirmed: Boolean, output: OutputPort)

    class ConfirmationRequest(
        val characterId: Character.Id,
        val characterName: String
    )

    class ResponseModel(
        val removedCharacter: RemovedCharacter,
        val removedCharacterFromThemes: List<RemovedCharacterFromTheme>
    )

    interface OutputPort {
        suspend fun confirmDeleteCharacter(request: RemoveCharacterFromStory.ConfirmationRequest)
        suspend fun receiveRemoveCharacterFromStoryResponse(response: ResponseModel)
    }
}