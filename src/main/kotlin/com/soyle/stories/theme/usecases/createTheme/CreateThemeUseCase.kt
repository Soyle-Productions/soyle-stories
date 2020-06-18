package com.soyle.stories.theme.usecases.createTheme

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.ThemeNameCannotBeBlank
import com.soyle.stories.theme.repositories.ThemeRepository
import java.util.*

class CreateThemeUseCase(
    private val themeRepository: ThemeRepository
) : CreateTheme {

    override suspend fun invoke(projectId: UUID, name: String, output: CreateTheme.OutputPort) {
        if (name.isBlank()) throw ThemeNameCannotBeBlank
        val theme = Theme(Project.Id(projectId), name)
        themeRepository.addTheme(theme)
        output.themeCreated(CreatedTheme(projectId, theme.id.uuid, name))
    }
}