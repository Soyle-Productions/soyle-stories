package com.soyle.stories.usecase.theme.createTheme

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.Symbol
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.theme.ThemeRepository
import com.soyle.stories.usecase.theme.addSymbolToTheme.SymbolAddedToTheme

class CreateThemeUseCase(
    private val themeRepository: ThemeRepository
) : CreateTheme {

    override suspend fun invoke(request: CreateTheme.RequestModel, output: CreateTheme.OutputPort) {
        val (theme, symbol) = createThemeAndMaybeSymbol(request)
        output.themeCreated(CreatedTheme(request.projectId, theme.id.uuid, request.themeName.value))
        if (symbol != null) {
            output.addedSymbolToTheme(SymbolAddedToTheme(theme.id.uuid, symbol.id.uuid, symbol.name))
        }
    }

    private suspend fun createThemeAndMaybeSymbol(request: CreateTheme.RequestModel): Pair<Theme, Symbol?>
    {
        val theme = Theme(Project.Id(request.projectId), request.themeName.value)
        val symbol = request.firstSymbolName?.let {
            Symbol(request.firstSymbolName.value)
        }
        if (symbol != null) themeRepository.addTheme(theme.withSymbol(symbol))
        else themeRepository.addTheme(theme)
        return theme to symbol
    }
}