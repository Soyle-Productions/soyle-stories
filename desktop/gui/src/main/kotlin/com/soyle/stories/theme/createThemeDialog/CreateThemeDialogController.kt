package com.soyle.stories.theme.createThemeDialog

import com.soyle.stories.theme.createTheme.CreateThemeController

class CreateThemeDialogController(
    private val presenter: CreateThemeDialogPresenter,
    private val createThemeController: CreateThemeController
) : CreateThemeDialogViewListener {

    override fun getValidState() {
        presenter.presentDialog()
    }

    override fun createTheme(name: String) {
        createThemeController.createTheme(name) {
            presenter.presentError(it)
        }
    }

}