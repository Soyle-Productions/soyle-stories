/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 10:34 PM
 */
package com.soyle.stories.character.usecases.removeCharacterFromStory

import arrow.core.Either
import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.repositories.CharacterRepository
import com.soyle.stories.character.repositories.ThemeRepository
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import java.util.*

class RemoveCharacterFromStoryUseCase(
    private val characterRepository: CharacterRepository,
    private val themeRepository: ThemeRepository,
    private val characterArcSectionRepository: CharacterArcSectionRepository
) : RemoveCharacterFromStory {

    override suspend fun invoke(
        characterId: UUID,
        output: RemoveCharacterFromStory.OutputPort
    ) {
        val character = characterRepository.getCharacterById(Character.Id(characterId))
            ?: return output.receiveRemoveCharacterFromStoryFailure(
                CharacterDoesNotExist(
                    characterId
                )
            )

        val themesForRemoval = themeRepository.getThemesWithCharacterIncluded(Character.Id(characterId))
        val maybeThemesWithCharacterRemoved = themesForRemoval.map {
            it.removeCharacter(character.id)
        }
        val themesWithNoCharactersRemaining: List<Theme>
        val themesWithRemainingCharacters: List<Theme>

        if (maybeThemesWithCharacterRemoved.all { it.isRight() }) {
            val themesWithCharacterRemoved = maybeThemesWithCharacterRemoved.map { (it as Either.Right).b }
            themesWithRemainingCharacters =
                themesWithCharacterRemoved.filter { it.hasCharacters() }
            themesWithNoCharactersRemaining =
                themesWithCharacterRemoved.filterNot { it.hasCharacters() }
            themeRepository.updateThemes(themesWithRemainingCharacters)
            themeRepository.deleteThemes(themesWithNoCharactersRemaining)
        } else {
            themesWithNoCharactersRemaining = emptyList()
            themesWithRemainingCharacters = emptyList()
        }

        characterRepository.deleteCharacterWithId(character.id)
        val arcSections = characterArcSectionRepository.getCharacterArcSectionsForCharacter(character.id)
        characterArcSectionRepository.removeArcSections(arcSections)

        output.receiveRemoveCharacterFromStoryResponse(
            RemoveCharacterFromStory.ResponseModel(
                characterId,
                themesWithNoCharactersRemaining.map { it.id.uuid },
                themesWithRemainingCharacters.map { it.id.uuid })
        )
    }
}