package com.soyle.stories.desktop.view.scene.setting.list.item

import com.soyle.stories.common.components.ComponentsStyles.Companion.hasProblem
import com.soyle.stories.desktop.view.common.NodeTest
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.RemoveLocationFromSceneControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.item.SceneSettingItemLocaleMock
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.domain.scene.events.SceneSettingLocationRenamed
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedNotifier
import com.soyle.stories.scene.setting.list.item.SceneSettingItemModel
import com.soyle.stories.scene.setting.list.item.SceneSettingItemView
import javafx.scene.layout.Pane
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import tornadofx.booleanProperty
import tornadofx.hasClass

class `Scene Setting Item Unit Test` : NodeTest<SceneSettingItemView>() {

    private val sceneId = Scene.Id()
    private val locationId = Location.Id()
    private val initialLocationName = "Kansas"
    private val removed = booleanProperty(false)
    private val locale = SceneSettingItemLocaleMock()
    private val sceneSettingRenamed = SceneSettingLocationRenamedNotifier()

    private var removeLocationRequest: Pair<Scene.Id, Location.Id>? = null

    override val view: SceneSettingItemView = SceneSettingItemView(
        SceneSettingItemModel(sceneId, locationId, initialLocationName, removed),
        locale,
        RemoveLocationFromSceneControllerDouble(
            onRemoveLocation = { sceneId, locationId -> removeLocationRequest = sceneId to locationId}
        ),
        sceneSettingRenamed
    )

    init {
        // ensure test updates will result in wrong thread exceptions if not properly handled
        interact {
            val stage =  Stage(StageStyle.DECORATED).apply { initOwner(primaryStage) }
            stage.scene = javafx.scene.Scene(Pane(view))
            stage.show()
        }
    }

    @Test
    fun `should display name from model`() {
        assertEquals(initialLocationName, view.text)
    }

    @Nested
    inner class `When Scene Setting is Renamed` {

        @Test
        fun `different scene id should not produce update`() {
            val event = SceneSettingLocationRenamed(
                Scene.Id(),
                SceneSettingLocation(locationId, "Oz")
            )
            runBlocking {
                sceneSettingRenamed.receiveSceneSettingLocationRenamed(event)
            }
            assertNotEquals(view.text, "Oz")
        }

        @Test
        fun `different location id should not produce update`() {
            val event = SceneSettingLocationRenamed(
                sceneId,
                SceneSettingLocation(Location.Id(), "Oz")
            )
            runBlocking {
                sceneSettingRenamed.receiveSceneSettingLocationRenamed(event)
            }
            assertNotEquals(view.text, "Oz")
        }

        @Test
        fun `only last event produced should provide new name`() {
            val event = SceneSettingLocationRenamed(
                sceneId,
                SceneSettingLocation(locationId, "Oz")
            )
            runBlocking {
                sceneSettingRenamed.receiveSceneSettingLocaitonsRenamed(List(5) {
                    SceneSettingLocationRenamed(
                        sceneId,
                        SceneSettingLocation(locationId, "New Name $it")
                    )
                } + event)
            }
            assertEquals(view.text, "Oz")
        }

    }

    @Nested
    inner class `Given Scene Setting backing Location has been Removed` {

        init {
            removed.set(true)
        }

        @Test
        fun `should show warning state`() {
            assertTrue(view.hasClass(hasProblem))
        }

        @Test
        fun `removed tooltip should display text from locale`() {
            locale.locationHasBeenRemovedFromStory.set("This location was removed a while ago, dude.")
            assertEquals("This location was removed a while ago, dude.", view.tooltip!!.text)
        }

    }

    @Nested
    inner class `When Delete Icon is Clicked` {

        init {
            interact { clickOn(view.deleteGraphic) }
        }

        @Test
        fun `should remove location`() {
            assertEquals(
                sceneId to locationId,
                removeLocationRequest
            )
        }

    }

}