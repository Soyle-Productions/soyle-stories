package com.soyle.stories.theme.usecases.deleteTheme

import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class DeleteThemeUseCase(
    private val themeRepository: ThemeRepository
) : DeleteTheme {

    override suspend fun invoke(themeId: UUID, output: DeleteTheme.OutputPort) {
        val theme = themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)

        themeRepository.deleteTheme(theme)

        output.themeDeleted(DeletedTheme(themeId))
    }
}