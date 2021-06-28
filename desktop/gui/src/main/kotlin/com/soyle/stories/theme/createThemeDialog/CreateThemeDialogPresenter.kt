package com.soyle.stories.theme.createThemeDialog

import com.soyle.stories.gui.View
import com.soyle.stories.theme.createTheme.CreatedThemeReceiver
import com.soyle.stories.usecase.theme.createTheme.CreatedTheme

class CreateThemeDialogPresenter(
    private val view: View.Nullable<CreateThemeDialogViewModel>
) : CreatedThemeReceiver {

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

    override suspend fun receiveCreatedTheme(createdTheme: CreatedTheme) {
        view.update {
            CreateThemeDialogViewModel("", "", null, created = true)
        }
    }

}