package com.soyle.stories.theme.usecases.updateThemeMetaData

import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.validateThemeName
import java.util.*

class RenameThemeUseCase(
    private val themeRepository: ThemeRepository
) : RenameTheme {

    override suspend fun invoke(themeId: UUID, name: String, output: RenameTheme.OutputPort) {
        val theme = getTheme(themeId)
        validateThemeName(name)
        themeRepository.updateTheme(theme.withName(name))
        output.themeRenamed(RenamedTheme(themeId, theme.name, name))

    }

    private suspend fun getTheme(themeId: UUID) = (themeRepository.getThemeById(Theme.Id(themeId))
        ?: throw ThemeDoesNotExist(themeId))

}