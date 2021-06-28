package com.soyle.stories.desktop.view.scene.sceneSetting

import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.di.get
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.sceneSetting.LocationSetter
import com.soyle.stories.scene.sceneSetting.SceneSettingState
import com.soyle.stories.scene.sceneSetting.SceneSettingView
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.ButtonBase
import javafx.scene.control.Labeled
import javafx.scene.control.MenuButton
import javafx.scene.control.MenuItem
import org.testfx.api.FxRobot
import tornadofx.uiComponent

class `Scene Setting Driver`(private val view: SceneSettingView) : FxRobot() {

    fun isFocusedOn(scene: Scene): Boolean {
        return view.scope.get<SceneSettingState>().targetScene.value?.id == scene.id.uuid.toString()
    }

    private val locationSetter: LocationSetter?
        get() = from(view.root).lookup(".${SceneSettingView.Styles.locationSetter.name}").queryAll<Node>()
            .firstOrNull()
            ?.uiComponent()

    val useLocationButton: MenuButton?
        get() = locationSetter?.let { from(it.root).lookup(".use-location-button").queryAll<MenuButton>().firstOrNull() }

    fun getAvailableLocationItem(locationId: Location.Id): MenuItem?
    {
        return useLocationButton?.items?.find { it.id == locationId.toString() }
    }

    private val locationList: Parent?
        get() = locationSetter?.let { from(it.root).lookup(".location-list").queryAll<Parent>().firstOrNull() }

    fun getLocationItem(locationId: Location.Id): Chip?
    {
        val locationNode = locationList?.let { from(it).lookup("#${locationId}").queryAll<Chip>().firstOrNull() }
        return locationNode
    }

    fun getLocationItemByName(locationName: String): Labeled? {
        return locationList?.let { from(it).lookup(locationName).queryAll<Labeled>().firstOrNull() }
    }
}

fun SceneSettingView.driver() = `Scene Setting Driver`(this)
fun SceneSettingView.drive(block: `Scene Setting Driver`.() -> Unit) {
    val driver = driver()
    driver.interact {
        driver.block()
    }
}