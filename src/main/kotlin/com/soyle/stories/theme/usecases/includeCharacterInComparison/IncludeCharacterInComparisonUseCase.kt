package com.soyle.stories.theme.usecases.includeCharacterInComparison

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.CharacterException
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.ThemeException
import com.soyle.stories.theme.repositories.CharacterArcRepository
import com.soyle.stories.theme.repositories.CharacterRepository
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class IncludeCharacterInComparisonUseCase(
    private val characterRepository: CharacterRepository,
    private val themeRepository: ThemeRepository
) : IncludeCharacterInComparison {

    override suspend fun invoke(characterId: UUID, themeId: UUID, output: IncludeCharacterInComparison.OutputPort) {
        val response = try {
            includeCharacterInComparison(characterId, themeId)
        } catch (c: CharacterException) {
            return output.receiveIncludeCharacterInComparisonFailure(c)
        } catch (c: com.soyle.stories.characterarc.CharacterArcException) {
            return output.receiveIncludeCharacterInComparisonFailure(c)
        } catch (t: ThemeException) {
            return output.receiveIncludeCharacterInComparisonFailure(t)
        }
        output.receiveIncludeCharacterInComparisonResponse(response)
    }

    private suspend fun includeCharacterInComparison(
        characterId: UUID,
        themeId: UUID
    ): CharacterIncludedInTheme {
        val character = getCharacterById(characterId)
        val theme = getThemeById(themeId)

        val themeWithCharacter = theme.withCharacterIncluded(character.id, character.name, character.media)
        themeRepository.updateTheme(themeWithCharacter)

        return CharacterIncludedInTheme(
            themeId,
            "",
            characterId,
            character.name,
            false
        )
    }

    private suspend fun getCharacterById(characterId: UUID) =
        characterRepository.getCharacterById(Character.Id(characterId))
            ?: throw CharacterDoesNotExist(characterId)

    private suspend fun getThemeById(themeId: UUID) =
        themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)

}