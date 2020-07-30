package com.soyle.stories.theme.usecases.updateThemeMetaData

import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class ChangeCentralConflictUseCase(
    private val themeRepository: ThemeRepository
) : ChangeCentralConflict {

    override suspend fun invoke(themeId: UUID, centralConflict: String, output: ChangeCentralConflict.OutputPort) {
        val theme = themeRepository.getThemeById(Theme.Id(themeId))
            ?: throw ThemeDoesNotExist(themeId)
        themeRepository.updateTheme(theme.withCentralConflict(centralConflict))

        output.centralConflictChanged(
            ChangeCentralConflict.ResponseModel(
                ThemeWithCentralConflictChanged(theme.id.uuid, centralConflict)
            )
        )
    }

}