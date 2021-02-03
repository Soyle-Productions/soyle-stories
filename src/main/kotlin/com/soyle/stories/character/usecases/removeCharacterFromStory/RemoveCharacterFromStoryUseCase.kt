package com.soyle.stories.character.usecases.removeCharacterFromStory

import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.character.repositories.ThemeRepository
import com.soyle.stories.character.repositories.getCharacterOrError
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemovedCharacterFromTheme
import java.util.*

class RemoveCharacterFromStoryUseCase(
    private val characterRepository: CharacterRepository,
    private val themeRepository: ThemeRepository,
    private val characterArcRepository: CharacterArcRepository
) : RemoveCharacterFromStory {

    override suspend fun invoke(
        characterId: UUID,
        confirmed: Boolean,
        output: RemoveCharacterFromStory.OutputPort
    ) {
        val character = characterRepository.getCharacterOrError(characterId)
        if (!confirmed) confirmDeleteCharacter(character, output)
        else deleteCharacter(character, output)
    }

    private suspend fun confirmDeleteCharacter(character: Character, output: RemoveCharacterFromStory.OutputPort)
    {
        output.confirmDeleteCharacter(
            RemoveCharacterFromStory.ConfirmationRequest(
                character.id,
                character.name.value
            )
        )
    }

    private suspend fun deleteCharacter(character: Character, output: RemoveCharacterFromStory.OutputPort)
    {
        val updatedThemes = removeCharacterFromThemes(character)
        removeCharactersArcs(character)
        characterRepository.deleteCharacterWithId(character.id)

        output.receiveRemoveCharacterFromStoryResponse(
            RemoveCharacterFromStory.ResponseModel(
                RemovedCharacter(character.id.uuid),
                updatedThemes.map { RemovedCharacterFromTheme(it.id.uuid, character.id.uuid) }
            )
        )
    }

    private suspend fun removeCharactersArcs(character: Character) {
        val characterArcs = characterArcRepository.listCharacterArcsForCharacter(character.id)
        if (characterArcs.isNotEmpty()) {
            characterArcRepository.removeCharacterArcs(*characterArcs.toTypedArray())
        }
    }

    private suspend fun removeCharacterFromThemes(character: Character): List<Theme> {
        val themesForRemoval = themeRepository.getThemesWithCharacterIncluded(character.id)
        val updatedThemes = themesForRemoval.map { it.withoutCharacter(character.id) }

        if (updatedThemes.isNotEmpty()) {
            themeRepository.updateThemes(updatedThemes)
        }
        return updatedThemes
    }
}