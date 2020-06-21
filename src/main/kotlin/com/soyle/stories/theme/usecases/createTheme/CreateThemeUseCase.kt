package com.soyle.stories.theme.usecases.createTheme

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.Symbol
import com.soyle.stories.theme.ThemeNameCannotBeBlank
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.theme.usecases.validateSymbolName
import com.soyle.stories.theme.usecases.validateThemeName
import java.util.*

class CreateThemeUseCase(
    private val themeRepository: ThemeRepository
) : CreateTheme {

    override suspend fun invoke(request: CreateTheme.RequestModel, output: CreateTheme.OutputPort) {
        validateThemeName(request.themeName)
        val (theme, symbol) = createThemeAndMaybeSymbol(request)
        output.themeCreated(CreatedTheme(request.projectId, theme.id.uuid, request.themeName))
        if (symbol != null) {
            output.addedSymbolToTheme(SymbolAddedToTheme(theme.id.uuid, symbol.id.uuid, symbol.name))
        }
    }

    private suspend fun createThemeAndMaybeSymbol(request: CreateTheme.RequestModel): Pair<Theme, Symbol?>
    {
        val theme = Theme(Project.Id(request.projectId), request.themeName)
        val symbol = request.firstSymbolName?.let {
            validateSymbolName(request.firstSymbolName)
            Symbol(request.firstSymbolName)
        }
        if (symbol != null) themeRepository.addTheme(theme.withSymbol(symbol))
        else themeRepository.addTheme(theme)
        return theme to symbol
    }
}