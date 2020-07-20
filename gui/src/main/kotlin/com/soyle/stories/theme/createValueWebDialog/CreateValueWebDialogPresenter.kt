package com.soyle.stories.theme.createValueWebDialog

import com.soyle.stories.gui.View
import com.soyle.stories.theme.createThemeDialog.CreateThemeDialogViewModel
import com.soyle.stories.theme.usecases.addValueWebToTheme.AddValueWebToTheme
import com.soyle.stories.theme.usecases.addValueWebToTheme.ValueWebAddedToTheme

class CreateValueWebDialogPresenter(
    private val view: View.Nullable<CreateValueWebDialogViewModel>
) : AddValueWebToTheme.OutputPort {

    internal fun presentDialog()
    {
        view.update {
            CreateValueWebDialogViewModel(
                title = "Create New Value Web",
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

    override suspend fun addedValueWebToTheme(response: AddValueWebToTheme.ResponseModel) {
        view.update {
            CreateValueWebDialogViewModel("", "", null, created = true)
        }
    }

}