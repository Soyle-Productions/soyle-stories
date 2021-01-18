package com.soyle.stories.desktop.view.scene.createSceneDialog

import com.soyle.stories.scene.createSceneDialog.CreateSceneDialog
import javafx.scene.control.TextField
import javafx.scene.control.TextInputControl
import org.testfx.api.FxRobot

class CreateSceneDialogDriver(private val dialog: CreateSceneDialog) : FxRobot() {

    fun getNameInput(): TextInputControl
    {
        return from(dialog.root).lookup(".text-field").query<TextField>()
    }

}