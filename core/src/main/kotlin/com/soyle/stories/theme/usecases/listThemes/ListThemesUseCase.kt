package com.soyle.stories.theme.usecases.listThemes

import com.soyle.stories.entities.Project
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.ThemeItem
import java.util.*

class ListThemesUseCase(
    private val themeRepository: ThemeRepository
) : ListThemes {

    override suspend fun invoke(projectId: UUID, output: ListThemes.OutputPort) {
        themeRepository.listThemesInProject(Project.Id(projectId))
            .map { ThemeItem(it.id.uuid, it.name) }
            .let(::ThemeList)
            .let { output.themesListed(it) }
    }

}