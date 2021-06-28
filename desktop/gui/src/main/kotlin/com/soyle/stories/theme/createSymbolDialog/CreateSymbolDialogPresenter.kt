package com.soyle.stories.theme.createSymbolDialog

import com.soyle.stories.gui.View
import com.soyle.stories.theme.addSymbolToTheme.SymbolAddedToThemeReceiver
import com.soyle.stories.theme.changeThemeDetails.renameTheme.RenamedThemeReceiver
import com.soyle.stories.theme.createTheme.CreatedThemeReceiver
import com.soyle.stories.theme.deleteTheme.ThemeDeletedReceiver
import com.soyle.stories.usecase.theme.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.usecase.theme.changeThemeDetails.RenamedTheme
import com.soyle.stories.usecase.theme.createTheme.CreatedTheme
import com.soyle.stories.usecase.theme.deleteTheme.DeletedTheme
import com.soyle.stories.usecase.theme.listThemes.ListThemes
import com.soyle.stories.usecase.theme.listThemes.ThemeList

class CreateSymbolDialogPresenter(
    private val view: View.Nullable<CreateSymbolDialogViewModel>
) : ListThemes.OutputPort, ThemeDeletedReceiver, RenamedThemeReceiver, CreatedThemeReceiver, SymbolAddedToThemeReceiver {

    override suspend fun themesListed(response: ThemeList) {
        view.update {
            CreateSymbolDialogViewModel(
                title = "Create New Symbol",
                nameFieldLabel = "Name",
                errorMessage = null,
                errorCause = null,
                themes = response.themes.map {
                    ThemeItemViewModel(it.themeId.toString(), it.themeName)
                }.sortedBy { it.themeName },
                createdId = null
            )
        }
    }

    override suspend fun receiveCreatedTheme(createdTheme: CreatedTheme) {
        val newItem = ThemeItemViewModel(createdTheme.themeId.toString(), createdTheme.themeName)
        view.updateOrInvalidated {
            copy(
                themes = (themes + newItem).sortedBy { it.themeName }
            )
        }
    }

    override suspend fun receiveDeletedTheme(deletedTheme: DeletedTheme) {
        val themeId = deletedTheme.themeId.toString()
        view.updateOrInvalidated {
            copy(
                themes = themes.filterNot { it.themeId == themeId }
            )
        }
    }

    override suspend fun receiveRenamedTheme(renamedTheme: RenamedTheme) {
        val themeId = renamedTheme.themeId.toString()
        view.updateOrInvalidated {
            copy(
                themes = themes.map {
                    if (it.themeId != themeId) it
                    else ThemeItemViewModel(it.themeId, renamedTheme.newName)
                }.sortedBy { it.themeName }
            )
        }
    }

    internal fun presentError(t: Throwable) {
        view.updateOrInvalidated {
            copy(
                errorMessage = t.localizedMessage?.takeUnless { it.isBlank() }
                    ?: "Something went wrong: ${t::class.simpleName}",
                errorCause = null
            )
        }
    }

    override suspend fun receiveSymbolAddedToTheme(symbolAddedToTheme: SymbolAddedToTheme) {
        view.update {
            CreateSymbolDialogViewModel("", "", null, null, emptyList(), createdId = symbolAddedToTheme.symbolId.toString())
        }
    }

}