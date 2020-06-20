package com.soyle.stories.theme.themeList

import com.soyle.stories.gui.View
import com.soyle.stories.theme.usecases.SymbolItem
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
) : ListSymbolsByTheme.OutputPort, CreateTheme.OutputPort, DeleteTheme.OutputPort, RenameTheme.OutputPort {

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

    private fun sortedThemes(themes: List<ThemeListItemViewModel>) = themes.sortedBy { it.themeName.toLowerCase() }

    private fun themeItem(themeId: UUID, themeName: String, symbols: List<SymbolItem> = listOf()) = ThemeListItemViewModel(
        themeId.toString(),
        themeName,
        symbols.map {
            SymbolListItemViewModel(it.symbolId.toString(), it.symbolName)
        }.sortedBy { it.symbolName.toLowerCase() }
    )

}