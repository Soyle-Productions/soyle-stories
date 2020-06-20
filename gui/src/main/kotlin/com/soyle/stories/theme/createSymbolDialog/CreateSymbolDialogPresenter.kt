package com.soyle.stories.theme.createSymbolDialog

import com.soyle.stories.gui.View
import com.soyle.stories.theme.usecases.addSymbolToTheme.AddSymbolToTheme
import com.soyle.stories.theme.usecases.addSymbolToTheme.SymbolAddedToTheme

class CreateSymbolDialogPresenter(
    private val view: View.Nullable<CreateSymbolDialogViewModel>
) : AddSymbolToTheme.OutputPort {

    internal fun presentDialog() {
        view.update {
            CreateSymbolDialogViewModel(
                title = "Create New Symbol",
                nameFieldLabel = "Name",
                errorMessage = null,
                created = false
            )
        }
    }

    internal fun presentError(t: Throwable) {
        view.updateOrInvalidated {
            copy(
                errorMessage = t.localizedMessage?.takeUnless { it.isBlank() }
                    ?: "Something went wrong: ${t::class.simpleName}"
            )
        }
    }

    override suspend fun addedSymbolToTheme(response: SymbolAddedToTheme) {
        view.update {
            CreateSymbolDialogViewModel("", "", null, created = true)
        }
    }

}