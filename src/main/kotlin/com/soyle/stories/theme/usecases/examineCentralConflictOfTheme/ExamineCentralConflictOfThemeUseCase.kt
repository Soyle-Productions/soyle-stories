package com.soyle.stories.theme.usecases.examineCentralConflictOfTheme

import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class ExamineCentralConflictOfThemeUseCase(
    private val themeRepository: ThemeRepository
) : ExamineCentralConflictOfTheme {

    override suspend fun invoke(themeId: UUID, outputPort: ExamineCentralConflictOfTheme.OutputPort) {
        val theme = themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)

        outputPort.centralConflictExamined(ExaminedCentralConflict(themeId, theme.centralConflict))
    }
}