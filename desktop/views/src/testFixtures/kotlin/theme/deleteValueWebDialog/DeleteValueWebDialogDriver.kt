package com.soyle.stories.desktop.view.theme.deleteValueWebDialog

import com.soyle.stories.theme.deleteValueWebDialog.DeleteValueWebDialog
import javafx.scene.control.Button
import org.testfx.api.FxRobot

class DeleteValueWebDialogDriver(private val dialog: DeleteValueWebDialog) : FxRobot() {

    fun getDeleteButton(): Button
    {
        return from(dialog.root).lookup(".button").queryAll<Button>().find { it.isDefaultButton }!!
    }

}