package com.soyle.stories.desktop.view.scene.setting.list.item

import com.soyle.stories.common.components.ComponentsStyles.Companion.hasProblem
import com.soyle.stories.common.components.ComponentsStyles.Companion.loading
import com.soyle.stories.desktop.view.common.NodeTest
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.ListLocationsToUseInSceneControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.RemoveLocationFromSceneControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.ReplaceSettingInSceneControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.item.SceneSettingItemLocaleMock
import com.soyle.stories.desktop.view.scene.sceneSetting.item.`Scene Setting Item Access`.Companion.access
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.domain.scene.events.SceneSettingLocationRenamed
import com.soyle.stories.scene.inconsistencies.SceneInconsistenciesNotifier
import com.soyle.stories.scene.locationsInScene.SceneSettingLocationRenamedNotifier
import com.soyle.stories.scene.locationsInScene.replace.ReplaceSettingInSceneController
import com.soyle.stories.scene.setting.list.item.SceneSettingItemModel
import com.soyle.stories.scene.setting.list.item.SceneSettingItemView
import com.soyle.stories.usecase.location.listAllLocations.LocationItem
import com.soyle.stories.usecase.scene.inconsistencies.SceneInconsistencies
import com.soyle.stories.usecase.scene.inconsistencies.SceneSettingLocationInconsistencies
import com.soyle.stories.usecase.scene.location.listLocationsToUse.ListAvailableLocationsToUseInScene
import javafx.scene.layout.Pane
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.*
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
    private val sceneInconsistentNotifier = SceneInconsistenciesNotifier()

    private var removeLocationRequest: Pair<Scene.Id, Location.Id>? = null

    private var availableLocationsOutput: ListAvailableLocationsToUseInScene.OutputPort? = null
    private val listLocationsToUseInScene = ListLocationsToUseInSceneControllerDouble({ sceneId, output ->
        assertEquals(this.sceneId, sceneId)
        availableLocationsOutput = output
    })

    private var replaceLocationRequest: Triple<Scene.Id, Location.Id, Location.Id>? = null
    private val replaceLocationController = ReplaceSettingInSceneControllerDouble(
        onInvoke = { a, b, c -> replaceLocationRequest = Triple(a, b, c) }
    )

    override val view: SceneSettingItemView = SceneSettingItemView(
        SceneSettingItemModel(sceneId, locationId, initialLocationName, removed),
        locale,
        RemoveLocationFromSceneControllerDouble(
            onRemoveLocation = { sceneId, locationId -> removeLocationRequest = sceneId to locationId}
        ),
        listLocationsToUseInScene,
        replaceLocationController,
        sceneSettingRenamed,
        sceneInconsistentNotifier
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
    inner class `When scene inconsistency is received`
    {

        @Nested
        inner class `Given No Inconsistency`
        {

            @Test
            fun `should not show warning when scene does not have scene setting location inconsistency`() {
                sendInconsistency(sceneId, isSceneSetting = false)

                // then
                assertFalse(view.hasClass(hasProblem))
            }

            @Test
            fun `should not show warning when scene setting location has inconsistency for different scene`() {
                sendInconsistency(Scene.Id(), locationId)

                // then
                assertFalse(view.hasClass(hasProblem))
            }

            @Test
            fun `should not show warning when scene setting location has inconsistency for different location`() {
                sendInconsistency(sceneId, Location.Id())

                // then
                assertFalse(view.hasClass(hasProblem))
            }

            @Test
            fun `should not show warning when scene setting location has no inconsistency for same scene and location`() {
                sendInconsistency(sceneId, locationId, hasInconsistency = false)

                // then
                assertFalse(view.hasClass(hasProblem))
            }

            @Test
            fun `should show warning when scene setting location has inconsistency for same scene and location`() {
                sendInconsistency(sceneId, locationId, hasInconsistency = true)

                // then
                assertTrue(view.hasClass(hasProblem))
            }

        }

        @Nested
        inner class `Given Inconsistency` {

            init {
                sendInconsistency(sceneId, locationId)
            }

            @Test
            fun `removed tooltip should display text from locale`() {
                locale.locationHasBeenRemovedFromStory.set("This location was removed a while ago, dude.")
                assertEquals("This location was removed a while ago, dude.", view.tooltip!!.text)
            }

            @Test
            fun `should still show warning when scene does not have scene setting location inconsistency`() {
                sendInconsistency(sceneId, isSceneSetting = false)

                // then
                assertTrue(view.hasClass(hasProblem))
            }

            @Test
            fun `should still show warning when scene setting location has inconsistency for different scene`() {
                sendInconsistency(Scene.Id(), locationId)

                // then
                assertTrue(view.hasClass(hasProblem))
            }

            @Test
            fun `should still show warning when scene setting location has inconsistency for different location`() {
                sendInconsistency(sceneId, Location.Id())

                // then
                assertTrue(view.hasClass(hasProblem))
            }

            @Test
            fun `should still show warning when scene setting location has inconsistency for same scene and location`() {
                sendInconsistency(sceneId, locationId, hasInconsistency = true)

                // then
                assertTrue(view.hasClass(hasProblem))
            }

            @Test
            fun `should not show warning when scene setting location has no inconsistency for same scene and location`() {
                sendInconsistency(sceneId, locationId, hasInconsistency = false)

                // then
                assertFalse(view.hasClass(hasProblem))
            }

        }

        private fun sendInconsistency(sceneId: Scene.Id, isSceneSetting: Boolean, locationId: Location.Id, hasInconsistency: Boolean) {
            runBlocking {
                sceneInconsistentNotifier.receiveSceneInconsistencies(SceneInconsistencies(sceneId, setOfNotNull(
                    if (isSceneSetting) SceneInconsistencies.SceneInconsistency.SceneSettingInconsistency(
                        setOf(
                            SceneSettingLocationInconsistencies(sceneId, locationId, setOfNotNull(
                                if (hasInconsistency) SceneSettingLocationInconsistencies.SceneSettingLocationInconsistency.LocationRemovedFromStory
                                else null
                            )
                            )
                        )
                    )
                    else null
                )
                ))
            }
        }

        private fun sendInconsistency(sceneId: Scene.Id) =
            sendInconsistency(sceneId, false, Location.Id(), false)

        private fun sendInconsistency(sceneId: Scene.Id, isSceneSetting: Boolean) =
            sendInconsistency(sceneId, isSceneSetting, Location.Id(), false)

        private fun sendInconsistency(sceneId: Scene.Id, locationId: Location.Id, hasInconsistency: Boolean = true) =
            sendInconsistency(sceneId, true, locationId, hasInconsistency)

    }

    @Nested
    inner class `When Options Icon is Clicked`
    {

        init {
            interact { clickOn(view.access().deleteButton) }
        }

        @Test
        fun `should show options`() {
            view.access().removeOption!!
            view.access().replaceOption!!

            interact { locale.removeFromScene .set("Get rid of this") }
            assertEquals("Get rid of this", view.access().removeOption!!.text)

            interact { locale.replaceWith.set("Swap this out") }
            assertEquals("Swap this out", view.access().replaceOption!!.text)
        }

        @Test
        fun `should not generate remove location request`() {
            assertNull(removeLocationRequest)
        }

        @Nested
        inner class `When Remove Option is Selected` {

            init {
                interact { view.access().removeOption!!.fire() }
            }

            @Test
            fun `should remove location`() {
                assertEquals(
                    sceneId to locationId,
                    removeLocationRequest
                )
            }

        }

        @Nested
        inner class `When Replace Options are Shown` {

            init {
                interact { view.access().replaceOption!!.show() }
            }

            @Test
            fun `should be loading available replacement locations`() {
                assertNotNull(availableLocationsOutput)

                interact { locale.loading.set("Just wait a second") }

                val loadingItem = view.access().replaceOption!!.items.single()
                assertEquals("loading", loadingItem.id)
                assertEquals("Just wait a second", loadingItem.text)
                assertTrue(loadingItem.isDisable)
            }

            @Nested
            inner class `Given no Locations Available`
            {

                init {
                    val responseJob = GlobalScope.launch {
                        availableLocationsOutput!!.receiveAvailableLocationsToUseInScene(ListAvailableLocationsToUseInScene.ResponseModel(listOf()))
                    }
                    runBlocking { responseJob.join() }
                }

                @Test
                fun `should show special message`() {
                    interact { locale.allExistingLocationsInProjectHaveBeenUsed.set("Nothing available") }

                    assertTrue(view.access().replaceOption!!.items.none { it.id == "loading" })
                    val emptyMessageItem = view.access().replaceOption!!.items.single { it.id == "empty-message" }
                    assertEquals("Nothing available", emptyMessageItem.text)
                    assertTrue(emptyMessageItem.isDisable)
                }

                @Test
                fun `should load again given menu is closed and reopened`() {
                    availableLocationsOutput = null

                    interact { view.access().deleteButton.hide() }
                    interact { view.access().deleteButton.show() }
                    interact { view.access().replaceOption!!.show() }

                    val loadingItem = view.access().replaceOption!!.items.single()
                    assertEquals("loading", loadingItem.id)

                    assertNotNull(availableLocationsOutput)
                }

            }

            @Nested
            inner class `Given Some Locations are Available`
            {

                private val availableLocations = List(5) { LocationItem(Location.Id(), "Location $it") }

                init {
                    val responseJob = GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            availableLocationsOutput!!.receiveAvailableLocationsToUseInScene(
                                ListAvailableLocationsToUseInScene.ResponseModel(availableLocations)
                            )
                        }
                    }
                    runBlocking { responseJob.join() }
                }

                @Test
                fun `should list all available locations given some are available`() {
                    view.access {
                        val replaceOption = replaceOption!!
                        assertTrue(replaceOption.items.none { it.id == "loading" })
                        assertTrue(replaceOption.items.none { it.id == "empty-message" })
                        assertEquals(5, replaceOption.availableLocationItems.size)
                        assertEquals(
                            availableLocations.mapTo(LinkedHashSet(5)) { it.id.toString() },
                            replaceOption.availableLocationItems.mapTo(LinkedHashSet(5)) { it.id }
                        )
                        replaceOption.availableLocationItems.forEach { menuItem ->
                            assertEquals(
                                availableLocations.find { it.id.toString() == menuItem.id }!!.locationName,
                                menuItem.text
                            )
                        }
                    }
                }

                @Test
                fun `should generate replace scene setting request when available location is selected`() {
                    val selectedLocationItem = with (view.access()) {
                        replaceOption!!.availableLocationItems.random()
                    }
                    interact { selectedLocationItem.fire() }

                    with(replaceLocationRequest!!) {
                        assertEquals(sceneId, first)
                        assertEquals(locationId, second)
                        assertEquals(selectedLocationItem.id, third.toString())
                    }
                }

                @Test
                fun `should load again given menu is closed and reopened`() {
                    availableLocationsOutput = null

                    interact { view.access().deleteButton.hide() }
                    interact { view.access().deleteButton.show() }
                    interact { view.access().replaceOption!!.show() }

                    val loadingItem = view.access().replaceOption!!.items.single()
                    assertEquals("loading", loadingItem.id)

                    assertNotNull(availableLocationsOutput)
                }

            }

        }

    }

}