package com.soyle.stories.desktop.view.location.locationList

import com.soyle.stories.domain.location.Location
import com.soyle.stories.location.items.LocationItemViewModel
import com.soyle.stories.location.locationList.LocationList
import javafx.scene.control.Button
import javafx.scene.control.ContextMenu
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView
import org.testfx.api.FxRobot

class LocationListDriver(private val locationList: LocationList) : FxRobot() {

    val tree: TreeView<Any?>
        get() = from(locationList.root).lookup(".tree-view").query<TreeView<Any?>>()

    val locationItemContextMenu: ContextMenu?
        get() = tree.contextMenu

    val deleteButton: Button
        get() = from(locationList.root).lookup("#actionBar_deleteLocation").queryButton()

    fun ContextMenu.getRenameOption() = items.find { it.text == "Rename" }!!

    val ContextMenu.openDetailsOption
        get() = items.find { it.id == "open" }!!

    fun getLocationItemOrError(locationId: Location.Id): TreeItem<Any?> =
        getLocationItem(locationId) ?: error("No item in location list with id $locationId")

    fun getLocationItem(locationId: Location.Id): TreeItem<Any?>? {
        return tree.root.children.asSequence().mapNotNull {
            val value = it.value as? LocationItemViewModel
            if (value?.id == locationId) it as TreeItem<Any?>
            else null
        }.firstOrNull()
    }

}

fun LocationList.driver() = LocationListDriver(this)
inline fun LocationList.drive(crossinline road: LocationListDriver.() -> Unit) {
    val driver = LocationListDriver(this)
    driver.interact { driver.road() }
}