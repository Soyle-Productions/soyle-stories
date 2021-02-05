package com.soyle.stories.desktop.view.location.deleteLocationDialog

import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialog
import javafx.scene.control.Button
import org.testfx.api.FxRobot

class DeleteLocationDialogDriver (private val dialog: DeleteLocationDialog) : FxRobot() {

    val confirmButton: Button
        get() = from(dialog.root).lookup(".button").queryAll<Button>().single { it.isDefaultButton }

}