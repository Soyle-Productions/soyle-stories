package com.soyle.stories.theme.createSymbolDialog

import com.soyle.stories.theme.addSymbolToTheme.AddSymbolToThemeController
import com.soyle.stories.theme.createTheme.CreateThemeController

class CreateSymbolDialogController(
    private val presenter: CreateSymbolDialogPresenter,
    private val addSymbolToThemeController: AddSymbolToThemeController
) : CreateSymbolDialogViewListener {

    override fun getValidState() {
        presenter.presentDialog()
    }

    override fun createSymbol(themeId: String, name: String) {
        addSymbolToThemeController.addSymbolToTheme(themeId, name) {
            presenter.presentError(it)
        }
    }

}