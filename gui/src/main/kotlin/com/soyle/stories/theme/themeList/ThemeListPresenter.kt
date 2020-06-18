package com.soyle.stories.theme.themeList

import com.soyle.stories.gui.View
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.ListSymbolsByTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.SymbolsByTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.symbols
import com.soyle.stories.theme.usecases.listSymbolsByTheme.theme

class ThemeListPresenter(
    private val view: View.Nullable<ThemeListViewModel>
) : ListSymbolsByTheme.OutputPort, CreateTheme.OutputPort {

    override suspend fun symbolsListedByTheme(response: SymbolsByTheme) {
        view.update {
            ThemeListViewModel(
                emptyMessage = "No themes created yet.",
                createFirstThemeButtonLabel = "Create First Theme",
                themes = response.themes.map {
                    ThemeListItemViewModel(
                        it.theme.themeId.toString(),
                        it.theme.themeName,
                        it.symbols.map {
                            SymbolListItemViewModel(it.symbolId.toString(), it.symbolName)
                        }.sortedBy { it.symbolName.toLowerCase() })
                }.sortedBy { it.themeName.toLowerCase() },
                createThemeButtonLabel = "Create New Theme"
            )
        }
    }

    override suspend fun themeCreated(response: CreatedTheme) {
        view.updateOrInvalidated {
            copy(
                themes = (themes + ThemeListItemViewModel(
                    response.themeId.toString(),
                    response.themeName,
                    listOf()
                )).sortedBy { it.themeName.toLowerCase() }
            )
        }
    }

}