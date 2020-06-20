package com.soyle.stories.theme.themeList

import com.soyle.stories.gui.View
import com.soyle.stories.theme.usecases.SymbolItem
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeleteTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeletedTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.ListSymbolsByTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.SymbolsByTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.symbols
import com.soyle.stories.theme.usecases.listSymbolsByTheme.theme
import com.soyle.stories.theme.usecases.renameTheme.RenameTheme
import com.soyle.stories.theme.usecases.renameTheme.RenamedTheme
import java.util.*

class ThemeListPresenter(
    private val view: View.Nullable<ThemeListViewModel>
) : ListSymbolsByTheme.OutputPort,
    CreateTheme.OutputPort,
    DeleteTheme.OutputPort,
    RenameTheme.OutputPort,
    AddSymbolToTheme.OutputPort {

    override suspend fun symbolsListedByTheme(response: SymbolsByTheme) {
        val viewModel = ThemeListViewModel(
            emptyMessage = "No themes created yet.",
            createFirstThemeButtonLabel = "Create First Theme",
            themes = sortedThemes(response.themes.map {
                themeItem(it.theme.themeId, it.theme.themeName, it.symbols)
            }),
            createThemeButtonLabel = "Create New Theme",
            deleteButtonLabel = "Delete"
        )
        view.update { viewModel }
    }

    override suspend fun themeCreated(response: CreatedTheme) {
        val newItem = themeItem(response.themeId, response.themeName)
        view.updateOrInvalidated {
            copy(
                themes = sortedThemes(themes + newItem)
            )
        }
    }

    override fun themeDeleted(response: DeletedTheme) {
        val themeId = response.themeId.toString()
        view.updateOrInvalidated {
            copy(
                themes = themes.filterNot {
                    it.themeId == themeId
                }
            )
        }
    }

    override fun themeRenamed(response: RenamedTheme) {
        val themeId = response.themeId.toString()
        val newName = response.newName
        view.updateOrInvalidated {
            copy(
                themes = sortedThemes(themes.map {
                    if (it.themeId != themeId) it
                    else it.copy(
                        themeName = newName
                    )
                })
            )
        }
    }

    override suspend fun addedSymbolToTheme(response: SymbolAddedToTheme) {
        val themeId = response.themeId.toString()
        val newItem = symbolItem(response.symbolId, response.symbolName)
        view.updateOrInvalidated {
            copy(
                themes = themes.map {
                    if (it.themeId != themeId) it
                    else it.copy(
                        symbols = sortedSymbols(it.symbols + newItem)
                    )
                }
            )
        }
    }

    private fun sortedThemes(themes: List<ThemeListItemViewModel>) = themes.sortedBy { it.themeName.toLowerCase() }

    private fun themeItem(themeId: UUID, themeName: String, symbols: List<SymbolItem> = listOf()) = ThemeListItemViewModel(
        themeId.toString(),
        themeName,
        sortedSymbols(symbols.map {
            symbolItem(it.symbolId, it.symbolName)
        })
    )

    private fun sortedSymbols(symbols: List<SymbolListItemViewModel>) = symbols.sortedBy { it.symbolName.toLowerCase() }

    private fun symbolItem(symbolId: UUID, symbolName: String) =
        SymbolListItemViewModel(symbolId.toString(), symbolName)

}