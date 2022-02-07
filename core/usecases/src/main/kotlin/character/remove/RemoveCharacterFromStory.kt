package com.soyle.stories.usecase.character.remove

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.events.CharacterRemovedFromStory
import com.soyle.stories.usecase.theme.removeCharacterFromComparison.RemovedCharacterFromTheme
import java.util.*

interface RemoveCharacterFromStory {
    suspend operator fun invoke(characterId: Character.Id, output: OutputPort)

    class ResponseModel(
        val characterRemoved: CharacterRemovedFromStory,
    )

    fun interface OutputPort {
        suspend fun characterRemovedFromProject(response: ResponseModel)
    }
}