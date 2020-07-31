package com.soyle.stories.theme.themeList

import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.gui.View
import com.soyle.stories.theme.addSymbolToTheme.SymbolAddedToThemeReceiver
import com.soyle.stories.theme.createTheme.CreatedThemeReceiver
import com.soyle.stories.theme.updateThemeMetaData.RenamedThemeReceiver
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
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.RemoveSymbolFromTheme
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.SymbolRemovedFromTheme
import com.soyle.stories.theme.usecases.renameSymbol.RenameSymbol
import com.soyle.stories.theme.usecases.renameSymbol.RenamedSymbol
import com.soyle.stories.theme.usecases.updateThemeMetaData.RenameTheme
import com.soyle.stories.theme.usecases.updateThemeMetaData.RenamedTheme
import java.util.*

class ThemeListPresenter(
    private val view: View.Nullable<ThemeListViewModel>
) : ListSymbolsByTheme.OutputPort,
    CreatedThemeReceiver,
    DeleteTheme.OutputPort,
    RenamedThemeReceiver,
    SymbolAddedToThemeReceiver,
    RemoveSymbolFromTheme.OutputPort,
    RenameSymbol.OutputPort {

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

    override suspend fun removedSymbolFromTheme(response: SymbolRemovedFromTheme) {
        val themeId = response.themeId.toString()
        val symbolId = response.symbolId.toString()
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

    override suspend fun symbolRenamed(response: RenamedSymbol) {
        val themeId = response.themeId.toString()
        val symbolId = response.symbolId.toString()
        view.updateOrInvalidated {
            copy(
                themes = themes.map {
                    if (it.themeId != themeId) it
                    else it.copy(
                        symbols = sortedSymbols(it.symbols.map {
                            if (it.symbolId != symbolId) it
                            else it.copy(
                                symbolName = response.newName
                            )
                        })
                    )
                }
            )
        }
    }

    override suspend fun characterArcsDeleted(response: List<DeletedCharacterArc>) {
        // do nothing
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