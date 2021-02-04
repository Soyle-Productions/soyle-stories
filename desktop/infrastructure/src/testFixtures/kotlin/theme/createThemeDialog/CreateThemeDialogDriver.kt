package com.soyle.stories.desktop.view.theme.createThemeDialog

import com.soyle.stories.theme.createThemeDialog.CreateThemeDialog
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import org.testfx.api.FxRobot

class CreateThemeDialogDriver(private val dialog: CreateThemeDialog) : FxRobot() {

    fun getNameInput(): TextInputControl
    {
        return from(dialog.root).lookup(".text-field").query<TextField>()
    }

}