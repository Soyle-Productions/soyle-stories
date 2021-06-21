package com.soyle.stories.desktop.view.scene.setting.list.useLocationButton

import com.soyle.stories.desktop.view.common.NodeTest
import com.soyle.stories.desktop.view.location.create.CreateLocationDialogFactory
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.LinkLocationToSceneControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.doubles.ListLocationsToUseInSceneControllerDouble
import com.soyle.stories.desktop.view.scene.sceneSetting.useLocationButton.UseLocationButtonLocaleMock
import com.soyle.stories.desktop.view.scene.sceneSetting.useLocationButton.`Use Location Button Access`.Companion.access
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.scene.setting.list.useLocationButton.UseLocationButton
import com.soyle.stories.usecase.location.createNewLocation.CreateNewLocation
import com.soyle.stories.usecase.location.listAllLocations.LocationItem
import com.soyle.stories.usecase.scene.location.listLocationsToUse.ListAvailableLocationsToUseInScene
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxAssert
import org.testfx.matcher.control.LabeledMatchers
import org.testfx.matcher.control.MenuItemMatchers
import java.util.*

class `Use Location Button Unit Test` : NodeTest<UseLocationButton>() {

    private val sceneId = Scene.Id()
    private val locale = UseLocationButtonLocaleMock()

    private var createNewLocationDialogRequest: ((CreateNewLocation.ResponseModel) -> Unit)? = null
    private val createLocationDialogFactory = CreateLocationDialogFactory(onInvoke = {
        createNewLocationDialogRequest = it
    })

    private var output: ListAvailableLocationsToUseInScene.OutputPort? = null
    private val listLocationsToUseInScene = ListLocationsToUseInSceneControllerDouble({ sceneId, output ->
        assertEquals(this.sceneId, sceneId)
        this.output = output
    })

    private var useLocationRequest: Location.Id? = null
    private val useLocation = LinkLocationToSceneControllerDouble(
        onLinkLocationToScene = { sceneId, locationId ->
            assertEquals(this.sceneId, sceneId)
            useLocationRequest = locationId
        }
    )

    private val button = UseLocationButton(
        sceneId,
        locale,
        createLocationDialogFactory,
        listLocationsToUseInScene,
        useLocation
    )
    override val view: UseLocationButton
        get() = button

    @Test
    fun `should display text from locale`() {
        locale.useLocation.set("Let's add a location")
        FxAssert.verifyThat(button, LabeledMatchers.hasText("Let's add a location"))
    }

    @Nested
    inner class `Given Button has been Selected` {

        init {
            interact { button.show() }
        }

        @Test
        fun `should be loading`() {
            locale.loading.set("Hold up, we're loading here")
            button.access {
                Assertions.assertEquals(button.items.single(), loadingItem)
                FxAssert.verifyThat(loadingItem, MenuItemMatchers.hasText("Hold up, we're loading here"))
                Assertions.assertTrue(loadingItem!!.isDisable)
            }

            assertNotNull(output)
        }

        @Nested
        inner class `Given No Available Locations`
        {

            init {
                runBlocking {
                    output!!.receiveAvailableLocationsToUseInScene(ListAvailableLocationsToUseInScene.ResponseModel(listOf()))
                }
            }

            @Test
            fun `should display create location item and no available locations message`() {
                Assertions.assertNull(button.access().loadingItem)

                locale.createLocation.set("Let's create a location, shall we?")
                FxAssert.verifyThat(
                    button.access().createLocationItem,
                    MenuItemMatchers.hasText("Let's create a location, shall we?")
                )

                locale.allExistingLocationsInProjectHaveBeenUsed.set("There's no more locations")
                button.access {
                    FxAssert.verifyThat(noAvailableLocationsItem, MenuItemMatchers.hasText("There's no more locations"))
                    Assertions.assertTrue(noAvailableLocationsItem!!.isDisable)
                }
            }

            @Nested
            inner class `Given Create Location Item Selected`
            {

                init {
                    interact { button.access().createLocationItem!!.fire() }
                }

                @Test
                fun `should open create location dialog`() {
                    assertNotNull(createNewLocationDialogRequest)
                }

                @Nested
                inner class `Given Location Dialog has created Location`
                {

                    private val newLocation = CreateNewLocation.ResponseModel(UUID.randomUUID(), "Some location")

                    init {
                        createNewLocationDialogRequest!!.invoke(newLocation)
                    }

                    @Test
                    fun `should use new location`() {
                        assertEquals(Location.Id(newLocation.locationId), useLocationRequest)
                    }

                }

            }

        }

        @Nested
        inner class `Given Available Locations`
        {

            private val locationItems = listOf(
                LocationItem(Location.Id(), "One Location"),
                LocationItem(Location.Id(), "Another Location"),
                LocationItem(Location.Id(), "And Another"),
                LocationItem(Location.Id(), "May the Fourth be With You")
            )

            init {
                runBlocking {
                    output!!.receiveAvailableLocationsToUseInScene(
                        ListAvailableLocationsToUseInScene.ResponseModel(
                            locationItems
                        )
                    )
                }
            }

            @Test
            fun `should display create location item and available locations`() {
                Assertions.assertNull(button.access().loadingItem)
                assertTrue(button.access().createLocationItem!!.isVisible)
                Assertions.assertNull(button.access().noAvailableLocationsItem)

                val availableLocationItems = button.access().availableLocationItems
                assertEquals(4, availableLocationItems.size)
                assertEquals(
                    locationItems.map { it.id.toString() }.toSet(),
                    availableLocationItems.map { it.id }.toSet()
                )
                availableLocationItems.forEach { menuItem ->
                    assertEquals(
                        locationItems.find { it.id.toString() == menuItem.id }!!.locationName,
                        menuItem.text
                    )
                }
            }

            @Nested
            inner class `When Shown Again`
            {

                init {
                    output = null
                    interact { button.show() }
                }

                @Test
                fun `should be loading again`() {
                    Assertions.assertEquals(button.items.single(), button.access().loadingItem)

                    assertNotNull(output)
                }

            }

            @Nested
            inner class `Given Location Item has been Selected`
            {

                private val locationToUse = locationItems.random()

                init {
                    interact { button.access().availableLocationItem(locationToUse.id)!!.fire() }
                }

                @Test
                fun `should generate use location request`() {
                    assertEquals(locationToUse.id, useLocationRequest)
                }

            }

        }

    }


}