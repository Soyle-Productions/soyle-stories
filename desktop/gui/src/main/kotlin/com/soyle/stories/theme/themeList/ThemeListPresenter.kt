package com.soyle.stories.theme.themeList

import com.soyle.stories.gui.View
import com.soyle.stories.theme.addSymbolToTheme.SymbolAddedToThemeReceiver
import com.soyle.stories.theme.changeThemeDetails.renameTheme.RenamedThemeReceiver
import com.soyle.stories.theme.createTheme.CreatedThemeReceiver
import com.soyle.stories.theme.deleteTheme.ThemeDeletedReceiver
import com.soyle.stories.theme.removeSymbolFromTheme.SymbolRemovedFromThemeReceiver
import com.soyle.stories.theme.renameSymbol.RenamedSymbolReceiver
import com.soyle.stories.theme.usecases.SymbolItem
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.theme.usecases.changeThemeDetails.RenamedTheme
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeletedTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.ListSymbolsByTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.SymbolsByTheme
import com.soyle.stories.theme.usecases.listSymbolsByTheme.symbols
import com.soyle.stories.theme.usecases.listSymbolsByTheme.theme
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.SymbolRemovedFromTheme
import com.soyle.stories.theme.usecases.renameSymbol.RenamedSymbol
import java.util.*

class ThemeListPresenter(
    private val view: View.Nullable<ThemeListViewModel>
) : ListSymbolsByTheme.OutputPort,
    CreatedThemeReceiver,
    ThemeDeletedReceiver,
    RenamedThemeReceiver,
    SymbolAddedToThemeReceiver,
    SymbolRemovedFromThemeReceiver,
    RenamedSymbolReceiver {

    override suspend fun symbolsListedByTheme(response: SymbolsByTheme) {
        val viewModel = ThemeListViewModel(
            emptyMessage = "No themes created yet.",
            createFirstThemeButtonLabel = "Create First Theme",
            themes = sortedThemes(response.themes.map {
                themeItem(it.theme.themeId, it.theme.themeName, it.symbols)
            }),
            createThemeButtonLabel = "Create New Theme",
            createSymbolButtonLabel = "Create Symbol",
            deleteButtonLabel = "Delete"
        )
        view.update { viewModel }
    }

    override suspend fun receiveCreatedTheme(createdTheme: CreatedTheme) {
        val newItem = themeItem(createdTheme.themeId, createdTheme.themeName)
        view.updateOrInvalidated {
            copy(
                themes = sortedThemes(themes + newItem)
            )
        }
    }

    override suspend fun receiveDeletedTheme(deletedTheme: DeletedTheme) {
        val themeId = deletedTheme.themeId.toString()
        view.updateOrInvalidated {
            copy(
                themes = themes.filterNot {
                    it.themeId == themeId
                }
            )
        }
    }

    override suspend fun receiveRenamedTheme(renamedTheme: RenamedTheme) {
        val themeId = renamedTheme.themeId.toString()
        val newName = renamedTheme.newName
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

    override suspend fun receiveSymbolAddedToTheme(symbolAddedToTheme: SymbolAddedToTheme) {
        val themeId = symbolAddedToTheme.themeId.toString()
        val newItem = symbolItem(symbolAddedToTheme.symbolId, symbolAddedToTheme.symbolName)
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

    override suspend fun receiveSymbolRemovedFromTheme(symbolRemovedFromTheme: SymbolRemovedFromTheme) {
        val themeId = symbolRemovedFromTheme.themeId.toString()
        val symbolId = symbolRemovedFromTheme.symbolId.toString()
        view.updateOrInvalidated {
            copy(
                themes = themes.map {
                    if (it.themeId != themeId) it
                    else it.copy(
                        symbols = it.symbols.filterNot { it.symbolId == symbolId }
                    )
                }
            )
        }
    }

    override suspend fun receiveRenamedSymbol(renamedSymbol: RenamedSymbol) {
        val themeId = renamedSymbol.themeId.toString()
        val symbolId = renamedSymbol.symbolId.toString()
        view.updateOrInvalidated {
            copy(
                themes = themes.map {
                    if (it.themeId != themeId) it
                    else it.copy(
                        symbols = sortedSymbols(it.symbols.map {
                            if (it.symbolId != symbolId) it
                            else it.copy(
                                symbolName = renamedSymbol.newName
                            )
                        })
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