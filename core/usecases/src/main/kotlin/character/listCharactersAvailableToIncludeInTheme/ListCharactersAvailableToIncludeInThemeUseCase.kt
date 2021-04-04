package com.soyle.stories.usecase.character.listCharactersAvailableToIncludeInTheme

import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.character.CharacterRepository
import com.soyle.stories.usecase.character.arc.listAllCharacterArcs.CharacterItem
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class ListCharactersAvailableToIncludeInThemeUseCase(
    private val themeRepository: ThemeRepository,
    private val characterRepository: CharacterRepository
) : ListCharactersAvailableToIncludeInTheme {

    override suspend fun invoke(themeId: UUID, output: ListCharactersAvailableToIncludeInTheme.OutputPort) {
        val theme = getTheme(themeId)

        val availableCharacters = CharactersAvailableToIncludeInTheme(
            themeId,
            getCharactersNotInTheme(theme).map { CharacterItem(it) }
        )

        output.availableCharactersToIncludeInThemeListed(availableCharacters)
    }

    private suspend fun getTheme(themeId: UUID): Theme {
        val theme = themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)
        return theme
    }

    private suspend fun getCharactersNotInTheme(theme: Theme) =
        characterRepository.listCharactersInProject(theme.projectId)
            .filterNot { theme.containsCharacter(it.id) }

}