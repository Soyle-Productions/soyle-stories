package com.soyle.stories.desktop.view.scene.deleteSceneDialog

import com.soyle.stories.scene.deleteSceneDialog.DeleteSceneDialog
import javafx.scene.control.Button
import javafx.scene.control.DialogPane
import org.testfx.api.FxRobot

class DeleteSceneDialogDriver (private val dialog: DeleteSceneDialog) : FxRobot() {

    val confirmButton: Button
        get() = from(dialog.root).lookup(".button").queryAll<Button>().single { it.isDefaultButton }

}