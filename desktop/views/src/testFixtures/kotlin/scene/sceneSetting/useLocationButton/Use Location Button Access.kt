package com.soyle.stories.desktop.view.scene.sceneSetting.useLocationButton

import com.soyle.stories.desktop.view.common.NodeAccess
import com.soyle.stories.domain.location.Location
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButton
import javafx.scene.control.MenuItem
import tornadofx.hasClass

class `Use Location Button Access`(button: UseLocationButton) : NodeAccess<UseLocationButton>(button) {

    companion object {

        fun UseLocationButton.access(op: `Use Location Button Access`.() -> Unit = {}): `Use Location Button Access` =
            `Use Location Button Access`(this).apply(op)
        fun UseLocationButton.drive(op: `Use Location Button Access`.() -> Unit) {
            val accessor = `Use Location Button Access`(this)
            accessor.interact {
                accessor.apply(op)
            }
        }
    }

    val loadingItem: MenuItem?
        get() = node.items.find { it.id == UseLocationButton.Styles.loading.name }

    val createLocationItem: MenuItem?
        get() = node.items.find { it.id == UseLocationButton.Styles.createLocation.name }

    val noAvailableLocationsItem: MenuItem?
        get() = node.items.find { it.id == UseLocationButton.Styles.noAvailableLocations.name }

    val availableLocationItems: List<MenuItem>
        get() = node.items.filter { it.hasClass(UseLocationButton.Styles.availableLocation) }

    fun availableLocationItem(locationId: Location.Id): MenuItem? =
        availableLocationItems.find { it.id == locationId.toString() }

}