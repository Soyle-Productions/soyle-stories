package com.soyle.stories.theme.createThemeDialog

import com.soyle.stories.gui.View
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme
import com.soyle.stories.theme.usecases.createTheme.CreateTheme
import com.soyle.stories.theme.usecases.createTheme.CreatedTheme

class CreateThemeDialogPresenter(
    private val view: View.Nullable<CreateThemeDialogViewModel>
) : CreateTheme.OutputPort {

    internal fun presentDialog()
    {
        view.update {
            CreateThemeDialogViewModel(
                title = "Create New Theme",
                nameFieldLabel = "Name",
                errorMessage = null,
                created =  false
            )
        }
    }

    internal fun presentError(t: Throwable)
    {
        view.updateOrInvalidated {
            copy(
                errorMessage = t.localizedMessage?.takeUnless { it.isBlank() } ?: "Something went wrong: ${t::class.simpleName}"
            )
        }
    }

    override suspend fun themeCreated(response: CreatedTheme) {
        view.update {
            CreateThemeDialogViewModel("", "", null, created = true)
        }
    }

    override suspend fun addedSymbolToTheme(response: SymbolAddedToTheme) {}

}