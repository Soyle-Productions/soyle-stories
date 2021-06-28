package com.soyle.stories.usecase.theme.listAvailablePerspectiveCharacters

import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class ListAvailablePerspectiveCharactersUseCase(
    private val themeRepository: ThemeRepository
) : ListAvailablePerspectiveCharacters {

    override suspend fun invoke(themeId: UUID, output: ListAvailablePerspectiveCharacters.OutputPort) {
        val theme = getTheme(themeId)

        output.receiveAvailablePerspectiveCharacters(AvailablePerspectiveCharacters(
            themeId,
            theme.characters.map { AvailablePerspectiveCharacter(it.id.uuid, it.name, it is MajorCharacter) }
        ))
    }

    private suspend fun getTheme(themeId: UUID) = (themeRepository.getThemeById(Theme.Id(themeId))
        ?: throw ThemeDoesNotExist(themeId))

}