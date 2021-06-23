package com.soyle.stories.desktop.view.scene.setting.list

import com.soyle.stories.common.components.ComponentsStyles
import com.soyle.stories.common.components.buttons.ButtonStyles
import com.soyle.stories.desktop.view.common.NodeTest
import com.soyle.stories.desktop.view.common.hasPseudoClass
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.DetectInconsistenciesInSceneSettingsControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.ListLocationsInSceneControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.item.SceneSettingItemFactory
import com.soyle.stories.desktop.view.scene.sceneSetting.list.SceneSettingItemListLocaleMock
import com.soyle.stories.desktop.view.scene.sceneSetting.list.`Scene Setting Item List Access`.Companion.access
import com.soyle.stories.desktop.view.scene.sceneSetting.useLocationButton.UseLocationButtonFactory
import com.soyle.stories.desktop.view.scene.sceneSetting.useLocationButton.`Use Location Button Access`.Companion.access
import com.soyle.stories.desktop.view.testconfig.DesignTest
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.domain.scene.events.LocationRemovedFromScene
import com.soyle.stories.domain.scene.events.LocationUsedInScene
import com.soyle.stories.location.deleteLocation.DeletedLocationNotifier
import com.soyle.stories.scene.locationsInScene.detectInconsistencies.DetectInconsistenciesInSceneSettingsController
import com.soyle.stories.scene.locationsInScene.linkLocationToScene.LocationUsedInSceneNotifier
import com.soyle.stories.scene.locationsInScene.removeLocationFromScene.LocationRemovedFromSceneNotifier
import com.soyle.stories.scene.setting.list.SceneSettingItemList
import com.soyle.stories.scene.setting.list.SceneSettingItemListLocale
import com.soyle.stories.scene.setting.list.item.SceneSettingItemView
import com.soyle.stories.usecase.location.deleteLocation.DeletedLocation
import com.soyle.stories.usecase.location.listAllLocations.LocationItem
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInScene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxAssert
import org.testfx.api.FxAssert.verifyThat
import org.testfx.matcher.base.NodeMatchers
import org.testfx.matcher.base.ParentMatchers
import org.testfx.matcher.control.LabeledMatchers
import tornadofx.hasClass
import tornadofx.hasPseudoClass
import tornadofx.label
import tornadofx.text

class `Scene Setting Item List Unit Test` : NodeTest<SceneSettingItemList>() {

    private val sceneId = Scene.Id()

    private val locale = SceneSettingItemListLocaleMock()

    private var listLocationsInSceneOutput: ListLocationsUsedInScene.OutputPort? = null
    private val listLocationsInSceneController = ListLocationsInSceneControllerDouble(
        onListLocationsInScene = { sceneId, output ->
            assertEquals(this.sceneId, sceneId) { "Did not load the locations for the expected scene" }
            listLocationsInSceneOutput = output
        }
    )

    private var detectInconsistenciesRequest: Scene.Id? = null
    private val detectInconsistenciesInSceneSettingsController = DetectInconsistenciesInSceneSettingsControllerDouble(
        onInvoke = ::detectInconsistenciesRequest::set
    )

    private val sceneSettingRemovedNotifier = LocationRemovedFromSceneNotifier()
    private val sceneSettingAddedNotifier = LocationUsedInSceneNotifier()
    private val deletedLocationNotifier = DeletedLocationNotifier()

    override val view: SceneSettingItemList = SceneSettingItemList(
        sceneId,
        locale,
        listLocationsInSceneController,
        detectInconsistenciesInSceneSettingsController,
        sceneSettingRemovedNotifier,
        sceneSettingAddedNotifier,
        deletedLocationNotifier,
        SceneSettingItemFactory { assertEquals(sceneId, it.sceneId) },
        UseLocationButtonFactory { assertEquals(sceneId, it) }
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
    fun `should be loading`() {
        assertTrue(view.hasClass(ComponentsStyles.loading))
        assertFalse(view.hasClass(ComponentsStyles.invitation))
        assertEquals(view.access().loadingIndicator!!, view.children.single { it.isVisible })

        assertNotNull(listLocationsInSceneOutput)
    }

    @Nested
    inner class `Given Scene Setting Loading Failed` {

        init {
            listLocationsInSceneController.job.completeExceptionally(Exception())
        }

        @Test
        fun `should display error message`() {
            interact {
                locale.failedToLoadUsedLocations.set("Didn't load 'em")
                locale.retry.set("Do it again")
            }
            with (view.access().errorMessage!!) {
                assertTrue(isVisible)
                assertEquals("Didn't load 'em", text)
            }
            with (view.access().retryButton!!) {
                assertTrue(isVisible)
                assertEquals("Do it again", text)
            }
        }

        @Nested
        inner class `When Retry Button is Selected`
        {

            init {
                listLocationsInSceneOutput = null
                interact { view.access().retryButton!!.fire() }
                interact { }
            }

            @Test
            fun `should load again`() {
                assertNotNull(listLocationsInSceneOutput) { "Did not generate list locations request" }
                assertTrue(view.hasClass(ComponentsStyles.loading))
                assertEquals(view.access().loadingIndicator!!, view.children.single { it.isVisible })
            }

        }

    }

    @Nested
    inner class `Given Scene Settings Loaded`
    {

        init {
            runBlocking {
                listLocationsInSceneOutput!!.receiveLocationsUsedInScene(
                    ListLocationsUsedInScene.ResponseModel(emptyList()))
            }
            interact{}
        }

        @Test
        fun `should show invitation`() {
            verifyThat(view) { !it.hasClass(ComponentsStyles.loading) }
            assert(view.access().loadingIndicator?.isVisible != true)

            verifyThat(view) { it.hasClass(ComponentsStyles.invitation) }

            view.access {
                verifyThat(inviteImage, NodeMatchers.isVisible())
                verifyThat(inviteTitle, NodeMatchers.isVisible())
                verifyThat(inviteMessage, NodeMatchers.isVisible())
                verifyThat(useLocationButton, NodeMatchers.isVisible())
                verifyThat(useLocationButton!!) { it.hasClass(ButtonStyles.inviteButton) }
            }
        }

        @Test
        fun `should show invite messages from locale`() {
            interact {
                locale.useLocationsAsSceneSetting.set("Locations need be be used")
                locale.noLocationUsedInSceneMessage.set {
                    text("You haven't used anything")
                    label(" and you should feel bad")
                }
            }

            verifyThat(view.access().inviteTitle, LabeledMatchers.hasText("Locations need be be used"))

            verifyThat(
                view.access().inviteMessage,
                ParentMatchers.hasChildren(2)
            ) { it.append(view.access().inviteMessage?.children) }
        }

        @Nested
        inner class `Given Locations have been Used`
        {
            private val locationItems = listOf(
                LocationItem(Location.Id(), "Could take place here"),
                LocationItem(Location.Id(), "Or Here"),
                LocationItem(Location.Id(), "And here too"),
            )

            init {
                runBlocking {
                    listLocationsInSceneOutput!!.receiveLocationsUsedInScene(
                        ListLocationsUsedInScene.ResponseModel(locationItems))
                }
                interact{}
            }

            @Test
            fun `should show scene settings`() {
                assertEquals(3, view.access().sceneSettingItems.size)
                assertEquals(
                    locationItems.map { it.id.toString() }.toSet(),
                    view.access().sceneSettingItems.map { it.id }.toSet()
                )
                view.access().sceneSettingItems.forEach { sceneSettingItem ->
                    val backingLocationItem = locationItems.find { it.id.toString() == sceneSettingItem.id }!!
                    assertEquals(backingLocationItem.locationName, sceneSettingItem.text)
                }
            }

            @Test
            fun `should detect inconsistencies`() {
                assertEquals(sceneId, detectInconsistenciesRequest)
            }

            @Nested
            inner class `When Scene Setting is Removed`
            {

                private val locationToRemove = locationItems.random()

                @Test
                fun `same location removed from different scene should have no affect`() {
                    locationRemoved(Scene.Id(), locationToRemove.id)
                    assertEquals(3, view.access().sceneSettingItems.size) { "should not have removed anything" }
                }

                @Test
                fun `different location removed from the scene should have no affect`() {
                    locationRemoved(sceneId, Location.Id())
                    assertEquals(3, view.access().sceneSettingItems.size) { "should not have removed anything" }
                }

                @Test
                fun `same location removed from the scene should remove the scene setting item`() {
                    locationRemoved(sceneId, locationToRemove.id)
                    assertEquals(2, view.access().sceneSettingItems.size)
                    assertEquals(
                        locationItems.minus(locationToRemove).map { it.id.toString() }.toSet(),
                        view.access().sceneSettingItems.map { it.id }.toSet()
                    )
                }

                @Test
                fun `should detect inconsistencies again`() {
                    detectInconsistenciesRequest = null

                    locationRemoved(sceneId, locationToRemove.id)

                    assertEquals(sceneId, detectInconsistenciesRequest)
                }

                private fun locationRemoved(sceneId: Scene.Id, locationId: Location.Id) {
                    runBlocking {
                        sceneSettingRemovedNotifier.receiveLocationRemovedFromScene(
                            LocationRemovedFromScene(sceneId, SceneSettingLocation(locationId, ""))
                        )
                    }
                    interact{}
                }

            }

            @Nested
            inner class `When Scene Setting is Added`
            {

                @Test
                fun `location added to different scene should have no affect`() {
                    sceneSettingAdded(Scene.Id(), Location.Id(), "")
                    assertEquals(3, view.access().sceneSettingItems.size) { "should not have added anything" }
                }

                @Test
                fun `location added to the scene should add a scene setting item`() {
                    val locationId = Location.Id()
                    val locationName = "Brand new location!"
                    sceneSettingAdded(sceneId, locationId, locationName)
                    assertEquals(4, view.access().sceneSettingItems.size) { "should have added the location" }
                    view.access().sceneSettingItems.find { it.id == locationId.toString() }!!.let {
                        assertEquals(locationName, it.text)
                    }
                }

                @Test
                fun `should detect inconsistencies again`() {
                    detectInconsistenciesRequest = null

                    sceneSettingAdded(sceneId, Location.Id(), "Some Location Name")

                    assertEquals(sceneId, detectInconsistenciesRequest)
                }

                private fun sceneSettingAdded(sceneId: Scene.Id, locationId: Location.Id, name: String) {
                    runBlocking {
                        sceneSettingAddedNotifier.receiveLocationUsedInScene(
                            LocationUsedInScene(sceneId, SceneSettingLocation(locationId, name))
                        )
                    }
                    interact{}
                }

            }

            @Nested
            inner class `When Location is Removed from Story`
            {

                @Test
                fun `should not detect inconsistencies given removed location is not scene setting`() {
                    detectInconsistenciesRequest = null

                    sendDeletedLocation(Location.Id())

                    assertNull(detectInconsistenciesRequest)
                }

                @Test
                fun `should detect inconsistencies again given removed location is scene setting`() {
                    detectInconsistenciesRequest = null

                    sendDeletedLocation(locationItems.random().id)

                    assertEquals(sceneId, detectInconsistenciesRequest)
                }

                private fun sendDeletedLocation(locationId: Location.Id)
                {
                    runBlocking {
                        deletedLocationNotifier.receiveDeletedLocation(DeletedLocation(locationId))
                    }
                }

            }

        }

        @Nested
        inner class `When Scene Setting is Added`
        {

            @Test
            fun `location added to different scene should have no affect`() {
                sceneSettingAdded(Scene.Id(), Location.Id(), "")
                assertEquals(0, view.access().sceneSettingItems.size) { "should not have added anything" }
            }

            @Test
            fun `location added to the scene should add a scene setting item`() {
                val locationId = Location.Id()
                val locationName = "Brand new location!"
                sceneSettingAdded(sceneId, locationId, locationName)
                view.access().sceneSettingItems.single().let {
                    assertEquals(locationId.toString(), it.id)
                    assertEquals(locationName, it.text)
                }
            }

            @Test
            fun `should detect inconsistencies again`() {
                detectInconsistenciesRequest = null

                sceneSettingAdded(sceneId, Location.Id(), "Some Location Name")

                assertEquals(sceneId, detectInconsistenciesRequest)
            }

            private fun sceneSettingAdded(sceneId: Scene.Id, locationId: Location.Id, name: String) {
                runBlocking {
                    sceneSettingAddedNotifier.receiveLocationUsedInScene(
                        LocationUsedInScene(sceneId, SceneSettingLocation(locationId, name))
                    )
                }
                interact{}
            }

        }

    }

}