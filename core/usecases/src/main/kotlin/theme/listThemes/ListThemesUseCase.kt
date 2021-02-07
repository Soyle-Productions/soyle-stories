package com.soyle.stories.usecase.theme.listThemes

import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.theme.ThemeItem
import com.soyle.stories.usecase.theme.ThemeRepository
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