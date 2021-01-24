package com.soyle.stories.desktop.view.theme.createSymbolDialog

import com.soyle.stories.theme.createSymbolDialog.CreateSymbolDialog
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import org.testfx.api.FxRobot

class CreateSymbolDialogDriver(private val dialog: CreateSymbolDialog) : FxRobot() {

    fun getNameInput(): TextInputControl
    {
        return from(dialog.root).lookup("#name-input").query<TextField>()
    }

}