package com.soyle.stories.usecase.character.remove

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterUpdate
import com.soyle.stories.domain.character.events.CharacterRemovedFromStory
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.removeCharacterFromComparison.RemovedCharacterFromTheme
import java.util.*

class RemoveCharacterFromStoryUseCase(
    private val characters: CharacterRepository
) : RemoveCharacterFromStory {

    override suspend fun invoke(characterId: Character.Id, output: RemoveCharacterFromStory.OutputPort) {
        val character = characters.getCharacterOrError(characterId.uuid)
        val update = character.removedFromStory()
        if (update is CharacterUpdate.Updated) {
            characters.updateCharacter(update.character)
            output.characterRemovedFromProject(RemoveCharacterFromStory.ResponseModel(update.event))
        }
    }

}