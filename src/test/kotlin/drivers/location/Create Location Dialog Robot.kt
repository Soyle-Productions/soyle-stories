package com.soyle.stories.desktop.config.drivers.location

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import javafx.event.ActionEvent
import javafx.scene.control.TextField

fun getCreateLocationDialogOrError() =
    getCreateLocationDialog() ?: error("Create Location Dialog is not open")

fun getCreateLocationDialog(): CreateLocationDialog? = robot.getOpenDialog()

fun CreateLocationDialog.createLocationWithName(locationName: String)
{
    val nameInput = robot.from(this.root).lookup(".text-field").query<TextField>()
    robot.interact {
        nameInput.text = locationName
        nameInput.fireEvent(ActionEvent())
    }
}