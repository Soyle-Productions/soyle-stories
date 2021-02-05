package com.soyle.stories.theme.usecases.listSymbolsByTheme

import com.soyle.stories.entities.Project
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.SymbolItem
import com.soyle.stories.theme.usecases.ThemeItem
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