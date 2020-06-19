package com.soyle.stories.theme.themeList

import com.soyle.stories.gui.View
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

class ThemeListPresenter(
    private val view: View.Nullable<ThemeListViewModel>
) : ListSymbolsByTheme.OutputPort, CreateTheme.OutputPort, DeleteTheme.OutputPort, RenameTheme.OutputPort {

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
                createThemeButtonLabel = "Create New Theme",
                deleteButtonLabel = "Delete"
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

    override fun themeDeleted(response: DeletedTheme) {
        val themeId = response.themeId.toString()
        view.updateOrInvalidated {
            copy(
                themes = (themes.filterNot {
                    it.themeId == themeId
                }).sortedBy { it.themeName.toLowerCase() }
            )
        }
    }

    override fun themeRenamed(response: RenamedTheme) {
        val themeId = response.themeId.toString()
        val newName = response.newName
        view.updateOrInvalidated {
            copy(
                themes = (themes.map {
                    if (it.themeId != themeId) it
                    else it.copy(
                        themeName = newName
                    )
                }).sortedBy { it.themeName.toLowerCase() }
            )
        }
    }

}