package com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters

import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.CharacterItem
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class ListAvailablePerspectiveCharactersUseCase(
    private val themeRepository: ThemeRepository
) : ListAvailablePerspectiveCharacters {

    override suspend fun invoke(themeId: UUID, output: ListAvailablePerspectiveCharacters.OutputPort) {
        val theme = getTheme(themeId)

        output.receiveAvailablePerspectiveCharacters(AvailablePerspectiveCharacters(
            themeId,
            theme.characters.filterIsInstance<MajorCharacter>().map { CharacterItem(it) }
        ))
    }

    private suspend fun getTheme(themeId: UUID) = (themeRepository.getThemeById(Theme.Id(themeId))
        ?: throw ThemeDoesNotExist(themeId))

}