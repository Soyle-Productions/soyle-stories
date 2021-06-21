package com.soyle.stories.desktop.config.drivers.location

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.project.workbench.getOpenDialog
import com.soyle.stories.location.createLocationDialog.CreateLocationDialog
import javafx.event.ActionEvent
import javafx.scene.control.TextField

fun getCreateLocationDialogOrError() =
    getCreateLocationDialog() ?: error("Create Location Dialog is not open")

fun getCreateLocationDialog(): CreateLocationDialog.View? = robot
    .listWindows().asSequence()
    .mapNotNull { it.scene.root as? CreateLocationDialog.View }
    .firstOrNull { it.scene.window?.isShowing == true }

fun CreateLocationDialog.View.createLocationWithName(locationName: String) {
    val nameInput = robot.from(this).lookup(".text-field").query<TextField>()
    robot.interact {
        nameInput.text = locationName
        nameInput.fireEvent(ActionEvent())
    }
}