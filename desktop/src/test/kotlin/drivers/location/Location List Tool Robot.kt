package com.soyle.stories.desktop.config.drivers.location

import com.soyle.stories.common.editingCell
import com.soyle.stories.desktop.config.drivers.robot
import com.soyle.stories.desktop.config.drivers.soylestories.findMenuItemById
import com.soyle.stories.desktop.view.location.locationList.drive
import com.soyle.stories.desktop.view.location.locationList.driver
import com.soyle.stories.domain.location.Location
import com.soyle.stories.location.locationList.LocationList
import com.soyle.stories.project.WorkBench
import javafx.event.ActionEvent
import javafx.scene.control.TextField
import tornadofx.FX

fun WorkBench.givenLocationListToolHasBeenOpened(): LocationList =
    getLocationListTool() ?: openLocationListTool().let { getCharacterListToolOrError() }

fun WorkBench.getCharacterListToolOrError(): LocationList =
    getLocationListTool() ?: throw NoSuchElementException("Theme List has not been opened")

fun WorkBench.getLocationListTool(): LocationList?
{
    return (FX.getComponents(scope)[LocationList::class] as? LocationList)?.takeIf { it.currentStage?.isShowing == true }
}

fun WorkBench.openLocationListTool()
{
    findMenuItemById("tools_locationlist")!!
        .apply { robot.interact { fire() } }
}


fun LocationList.renameLocationTo(locationId: Location.Id, newName: String)
{
    with (driver()) {
        val item = getLocationItemOrError(locationId)
        drive {
            tree.selectionModel.select(item)
            val renameOptionItem = locationItemContextMenu!!.getRenameOption()
            renameOptionItem.fire()
            (tree.editingCell!!.graphic as TextField).run {
                text = newName
                fireEvent(ActionEvent())
            }
        }
    }
}

fun LocationList.openLocationDetails(locationId: Location.Id) {
    drive {
        val item = getLocationItemOrError(locationId)
        tree.selectionModel.select(item)
        val openDetailsOption = locationItemContextMenu!!.openDetailsOption
        openDetailsOption.fire()
    }
}