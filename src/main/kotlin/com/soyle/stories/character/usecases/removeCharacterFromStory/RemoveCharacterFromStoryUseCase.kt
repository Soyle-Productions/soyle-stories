package com.soyle.stories.character.usecases.removeCharacterFromStory

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.character.repositories.ThemeRepository
import com.soyle.stories.entities.Character
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
        output: RemoveCharacterFromStory.OutputPort
    ) {
        val character = characterRepository.getCharacterById(Character.Id(characterId))
            ?: throw CharacterDoesNotExist(characterId)

        val themesForRemoval = themeRepository.getThemesWithCharacterIncluded(Character.Id(characterId))
        val updatedThemes = themesForRemoval.map { it.withoutCharacter(character.id) }

        if (updatedThemes.isNotEmpty()) {
            themeRepository.updateThemes(updatedThemes)
        }

        characterRepository.deleteCharacterWithId(character.id)
        val characterArcs = characterArcRepository.listCharacterArcsForCharacter(character.id)
        if (characterArcs.isNotEmpty()) {
            characterArcRepository.removeCharacterArcs(*characterArcs.toTypedArray())
        }

        output.receiveRemoveCharacterFromStoryResponse(
            RemoveCharacterFromStory.ResponseModel(
                RemovedCharacter(characterId),
                updatedThemes.map { RemovedCharacterFromTheme(it.id.uuid, characterId) }
            )
        )
    }
}