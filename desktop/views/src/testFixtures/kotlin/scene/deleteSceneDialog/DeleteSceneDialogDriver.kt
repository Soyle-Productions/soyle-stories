package com.soyle.stories.desktop.view.scene.deleteSceneDialog

import com.soyle.stories.scene.delete.DeleteScenePromptView
import javafx.scene.control.Button
import org.testfx.api.FxRobot

class DeleteSceneDialogDriver (private val dialog: DeleteScenePromptView) : FxRobot() {

    val confirmButton: Button
        get() = from(dialog.root).lookup(".button").queryAll<Button>().single { it.isDefaultButton }

}