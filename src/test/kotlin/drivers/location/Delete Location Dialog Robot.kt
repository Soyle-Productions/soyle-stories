package com.soyle.stories.desktop.config.drivers.location

import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.view.location.deleteLocationDialog.DeleteLocationDialogDriver
import com.soyle.stories.desktop.view.location.locationList.drive
import com.soyle.stories.desktop.view.location.locationList.driver
import com.soyle.stories.entities.Location
import com.soyle.stories.location.deleteLocationDialog.DeleteLocationDialog
import com.soyle.stories.location.locationList.LocationList
import tornadofx.uiComponent

fun LocationList.givenDeleteLocationDialogHasBeenOpened(locationId: Location.Id): DeleteLocationDialog =
    getDeleteLocationDialog() ?: openDeleteLocationDialog(locationId).let { getDeleteLocationDialogOrError() }

fun getDeleteLocationDialogOrError(): DeleteLocationDialog =
    getDeleteLocationDialog() ?: error("Delete Location Dialog is not open")

fun getDeleteLocationDialog(): DeleteLocationDialog? =
    robot.listWindows().asSequence()
        .mapNotNull { it.scene.root.uiComponent<DeleteLocationDialog>() }
        .firstOrNull { it.currentStage?.isShowing == true }

fun LocationList.openDeleteLocationDialog(locationId: Location.Id)
{
    with (driver()) {
        val item = getLocationItemOrError(locationId)
        drive {
            tree.selectionModel.select(item)
            deleteButton.fire()
        }
    }
}

fun DeleteLocationDialog.confirmDelete()
{
    val driver = DeleteLocationDialogDriver(this)
    driver.interact {
        driver.confirmButton.fire()
    }
}