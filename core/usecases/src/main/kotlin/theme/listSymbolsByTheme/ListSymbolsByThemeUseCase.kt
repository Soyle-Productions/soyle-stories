package com.soyle.stories.usecase.theme.listSymbolsByTheme

import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.theme.SymbolItem
import com.soyle.stories.usecase.theme.ThemeItem
import com.soyle.stories.usecase.theme.ThemeRepository
import java.util.*

class ListSymbolsByThemeUseCase(
    private val themeRepository: ThemeRepository
) : ListSymbolsByTheme {

    override suspend fun invoke(projectId: UUID, output: ListSymbolsByTheme.OutputPort) {
        themeRepository.listThemesInProject(Project.Id(projectId))
            .map { ThemeItem(it.id.uuid, it.name) to it.symbols.map { SymbolItem(it.id.uuid, it.name) } }
            .let(::SymbolsByTheme)
            .let { output.symbolsListedByTheme(it) }
    }

}