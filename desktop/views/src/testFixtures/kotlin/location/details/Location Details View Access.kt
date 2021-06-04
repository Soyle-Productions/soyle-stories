package com.soyle.stories.desktop.view.location.details

import com.soyle.stories.desktop.view.location.details.`Location Details View Access`.Companion.drive
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.locationDetails.LocationDetails
import javafx.collections.ObservableList
import javafx.scene.Node
import javafx.scene.control.*
import org.testfx.api.FxRobot

class `Location Details View Access`(private val view: LocationDetails) : FxRobot() {

    companion object {
        fun LocationDetails.access() = `Location Details View Access`(this)
        fun <T> LocationDetails.drive(op: `Location Details View Access`.() -> T): T
        {
            var result: T? = null
            val access = `Location Details View Access`(this)
            access.interact { result = access.op() }
            return result as T
        }
    }

    val addSceneButton: ButtonBase
        get() = from(view.root).lookup("#add-scene").queryAll<ButtonBase?>().find { it.isVisible }!!

    val availableScenesToHost: ObservableList<MenuItem>?
        get() = (addSceneButton as MenuButton).run {
            items.takeIf { isShowing }
        }

    fun ObservableList<MenuItem>.getSceneItem(sceneId: Scene.Id): MenuItem? =
        find { it.id == sceneId.toString() }
    fun getHostedSceneByName(sceneName: String): Labeled? =
        from(view.root)
            .lookup(".hosted-scene-item")
            .queryAll<Labeled>()
            .find { it.text == sceneName }
    fun getHostedScene(sceneId: Scene.Id): Labeled? =
        from(view.root)
            .lookup(".hosted-scene-item")
            .queryAll<Labeled>()
            .find { it.id == sceneId.toString() }
}
