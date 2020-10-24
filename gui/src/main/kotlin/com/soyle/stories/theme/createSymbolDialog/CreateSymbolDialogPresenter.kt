package com.soyle.stories.theme.createSymbolDialog

import com.soyle.stories.characterarc.usecases.deleteCharacterArc.DeletedCharacterArc
import com.soyle.stories.gui.View
import com.soyle.stories.theme.ThemeNameCannotBeBlank
import com.soyle.stories.theme.addSymbolToTheme.SymbolAddedToThemeReceiver
import com.soyle.stories.theme.createTheme.CreatedThemeReceiver
import com.soyle.stories.theme.changeThemeDetails.RenamedThemeReceiver
import com.soyle.stories.theme.usecases.SymbolNameCannotBeBlank
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeleteTheme
import com.soyle.stories.theme.usecases.deleteTheme.DeletedTheme
import com.soyle.stories.theme.usecases.listThemes.ListThemes
import com.soyle.stories.theme.usecases.listThemes.ThemeList
import com.soyle.stories.theme.usecases.changeThemeDetails.RenamedTheme

class CreateSymbolDialogPresenter(
    private val view: View.Nullable<CreateSymbolDialogViewModel>
) : ListThemes.OutputPort, DeleteTheme.OutputPort, RenamedThemeReceiver, CreatedThemeReceiver, SymbolAddedToThemeReceiver {

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

    override fun themeDeleted(response: DeletedTheme) {
        val themeId = response.themeId.toString()
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
                errorMessage = when (t) {
                    is ThemeNameCannotBeBlank -> "Theme name cannot be blank"
                    is SymbolNameCannotBeBlank -> "Symbol name cannot be blank"
                    else -> t.localizedMessage?.takeUnless { it.isBlank() }
                        ?: "Something went wrong: ${t::class.simpleName}"
                },
                errorCause = when (t) {
                    is ThemeNameCannotBeBlank -> "ThemeName"
                    is SymbolNameCannotBeBlank -> "SymbolName"
                    else -> null
                }
            )
        }
    }

    override suspend fun receiveSymbolAddedToTheme(symbolAddedToTheme: SymbolAddedToTheme) {
        view.update {
            CreateSymbolDialogViewModel("", "", null, null, emptyList(), createdId = symbolAddedToTheme.symbolId.toString())
        }
    }

    override suspend fun characterArcsDeleted(response: List<DeletedCharacterArc>) {
        // do nothing
    }

}