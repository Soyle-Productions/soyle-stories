package com.soyle.stories.usecase.theme.includeCharacterInComparison

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class IncludeCharacterInComparisonUseCase(
    private val characterRepository: CharacterRepository,
    private val themeRepository: ThemeRepository
) : IncludeCharacterInComparison {

    override suspend fun invoke(characterId: UUID, themeId: UUID, output: IncludeCharacterInComparison.OutputPort) {
        val response = try {
            includeCharacterInComparison(characterId, themeId)
        } catch (c: Exception) {
            return output.receiveIncludeCharacterInComparisonFailure(c)
        }
        output.receiveIncludeCharacterInComparisonResponse(response)
    }

    private suspend fun includeCharacterInComparison(
        characterId: UUID,
        themeId: UUID
    ): CharacterIncludedInTheme {
        val character = characterRepository.getCharacterOrError(characterId)
        val theme = getThemeById(themeId)

        val themeWithCharacter = theme.withCharacterIncluded(character.id, character.displayName.value, character.media)
        themeRepository.updateTheme(themeWithCharacter)

        return CharacterIncludedInTheme(
            themeId,
            "",
            characterId,
            character.displayName.value,
            false
        )
    }

    private suspend fun getThemeById(themeId: UUID) =
        themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)

}