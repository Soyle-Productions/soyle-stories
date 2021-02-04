package com.soyle.stories.theme.deleteValueWebDialog

import com.soyle.stories.gui.View
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.RemoveSymbolFromTheme
import com.soyle.stories.theme.usecases.removeSymbolFromTheme.SymbolRemovedFromTheme
import com.soyle.stories.theme.usecases.removeValueWebFromTheme.RemoveValueWebFromTheme
import com.soyle.stories.theme.usecases.removeValueWebFromTheme.ValueWebRemovedFromTheme
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences

class DeleteValueWebDialogPresenter(
    private val valueWebId: String,
    private val valueWebName: String,
    private val view: View.Nullable<DeleteValueWebDialogViewModel>
) : GetDialogPreferences.OutputPort, RemoveValueWebFromTheme.OutputPort {

    override fun gotDialogPreferences(response: DialogPreference) {
        view.update {
            DeleteValueWebDialogViewModel(
                title = "Confirm Delete Value Web",
                message = "Are you sure you want to delete $valueWebName?",
                doNotShowLabel = "Do not show this dialog again",
                deleteButtonLabel = "Delete",
                cancelButtonLabel = "Cancel",
                doDefaultAction = ! response.shouldShow,
                errorMessage = null
            )
        }
    }

    override suspend fun removedValueWebFromTheme(response: ValueWebRemovedFromTheme) {
        if (response.valueWebId.toString() != valueWebId) return
        view.updateOrInvalidated {
            this
        }
    }

    override fun failedToGetDialogPreferences(failure: Exception) {
        view.update {
            DeleteValueWebDialogViewModel(
                title = "Confirm Delete Value Web",
                message = "Are you sure you want to delete $valueWebName?",
                doNotShowLabel = "Do not show this dialog again",
                deleteButtonLabel = "Delete",
                cancelButtonLabel = "Cancel",
                doDefaultAction = false,
                errorMessage = failure.localizedMessage?.takeUnless { it.isBlank() } ?: "Failed to retrieve dialog preferences: ${failure::class.simpleName ?: failure::class}"
            )
        }
    }

}