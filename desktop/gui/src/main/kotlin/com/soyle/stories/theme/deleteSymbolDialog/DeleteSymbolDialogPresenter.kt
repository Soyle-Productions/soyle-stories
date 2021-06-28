package com.soyle.stories.theme.deleteSymbolDialog

import com.soyle.stories.gui.View
import com.soyle.stories.theme.removeSymbolFromTheme.SymbolRemovedFromThemeReceiver
import com.soyle.stories.usecase.theme.removeSymbolFromTheme.SymbolRemovedFromTheme
import com.soyle.stories.writer.usecases.DialogPreference
import com.soyle.stories.writer.usecases.getDialogPreferences.GetDialogPreferences

class DeleteSymbolDialogPresenter(
    private val symbolId: String,
    private val symbolName: String,
    private val view: View.Nullable<DeleteSymbolDialogViewModel>
) : GetDialogPreferences.OutputPort, SymbolRemovedFromThemeReceiver {

    override fun gotDialogPreferences(response: DialogPreference) {
        view.update {
            DeleteSymbolDialogViewModel(
                title = "Confirm Delete Symbol",
                message = "Are you sure you want to delete $symbolName?",
                doNotShowLabel = "Do not show this dialog again",
                deleteButtonLabel = "Delete",
                cancelButtonLabel = "Cancel",
                doDefaultAction = ! response.shouldShow,
                errorMessage = null
            )
        }
    }

    override suspend fun receiveSymbolRemovedFromTheme(symbolRemovedFromTheme: SymbolRemovedFromTheme) {
        if (symbolRemovedFromTheme.symbolId.toString() != symbolId) return
        view.updateOrInvalidated {
            this
        }
    }

    override fun failedToGetDialogPreferences(failure: Exception) {
        view.update {
            DeleteSymbolDialogViewModel(
                title = "Confirm Delete Symbol",
                message = "Are you sure you want to delete $symbolName?",
                doNotShowLabel = "Do not show this dialog again",
                deleteButtonLabel = "Delete",
                cancelButtonLabel = "Cancel",
                doDefaultAction = false,
                errorMessage = failure.localizedMessage?.takeUnless { it.isBlank() } ?: "Failed to retrieve dialog preferences: ${failure::class.simpleName ?: failure::class}"
            )
        }
    }

}