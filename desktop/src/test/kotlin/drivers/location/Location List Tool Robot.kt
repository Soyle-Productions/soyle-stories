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
import tornadofx.select


fun WorkBench.givenLocationListToolHasBeenOpened(): LocationList {
    if (currentStage?.isShowing != true) IllegalStateException("Workbench is not yet showing")
    return getLocationListTool() ?: run {
        openLocationListTool()
        getLocationListToolOrError()
    }
}

fun WorkBench.getLocationListToolOrError(): LocationList =
    getLocationListTool() ?: throw NoSuchElementException("Location List has not been opened")

fun WorkBench.getLocationListTool(): LocationList? {
    val components = FX.getComponents(scope)
    val instance = components[LocationList::class]
    val view = instance as? LocationList
    return view?.takeIf { it.currentStage?.isShowing == true }
}

fun WorkBench.openLocationListTool() {
    val view = FX.getComponents(scope)[LocationList::class] as? LocationList
    if (view != null) {
        robot.interact {
            view.owningTab?.select()
        }
    } else {
        findMenuItemById("tools_locationlist")!!
            .apply { robot.interact { fire() } }
    }
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