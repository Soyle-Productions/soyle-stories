package com.soyle.stories.usecase.character.removeCharacterFromStory

import com.soyle.stories.domain.character.Character
import com.soyle.stories.usecase.theme.removeCharacterFromComparison.RemovedCharacterFromTheme
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
        suspend fun confirmDeleteCharacter(request: ConfirmationRequest)
        suspend fun receiveRemoveCharacterFromStoryResponse(response: ResponseModel)
    }
}