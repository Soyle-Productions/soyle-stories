package com.soyle.stories.characterarc.deleteCharacterDialog

import com.soyle.stories.gui.View

class DeleteCharacterDialogPresenter(
    private val view: View.Nullable<DeleteCharacterDialogViewModel>
) {

    init {
        view.update {
            DeleteCharacterDialogViewModel(
                title = "Confirm",
                header = "",
                message = "Are you sure you want to delete this character?",
                deleteButtonLabel = "Delete",
                cancelButtonLabel = "Cancel",
                doDefaultAction = false
            )
        }
    }

    internal fun displayForCharacter(name: String)
    {
        view.updateOrInvalidated {
            copy(
                header = "Delete $name?"
            )
        }
    }

}