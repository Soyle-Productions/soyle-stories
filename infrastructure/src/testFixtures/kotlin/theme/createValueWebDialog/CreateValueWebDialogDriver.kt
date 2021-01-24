package com.soyle.stories.desktop.view.theme.createValueWebDialog

import com.soyle.stories.theme.createValueWebDialog.CreateValueWebDialog
import javafx.scene.control.TextInputControl
import org.testfx.api.FxRobot

class CreateValueWebDialogDriver(private val dialog: CreateValueWebDialog) : FxRobot() {

    fun getNameInput(): TextInputControl
    {
        return from(dialog.root).lookup(".text-field").query()
    }

}