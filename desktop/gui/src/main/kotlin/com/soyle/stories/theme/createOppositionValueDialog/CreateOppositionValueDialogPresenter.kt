package com.soyle.stories.theme.createOppositionValueDialog

import com.soyle.stories.gui.View
import com.soyle.stories.usecase.theme.addOppositionToValueWeb.AddOppositionToValueWeb

class CreateOppositionValueDialogPresenter(
    private val view: View.Nullable<CreateOppositionValueDialogViewModel>
) : AddOppositionToValueWeb.OutputPort {

    internal fun presentDialog()
    {
        view.update {
            CreateOppositionValueDialogViewModel(
                title = "Create New Opposition Value",
                nameFieldLabel = "Name",
                errorMessage = null,
                created =  false
            )
        }
    }

    override suspend fun addedOppositionToValueWeb(response: AddOppositionToValueWeb.ResponseModel) {
        view.update {
            CreateOppositionValueDialogViewModel("", "", null, created = true)
        }
    }

}