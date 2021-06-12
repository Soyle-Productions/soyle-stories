package com.soyle.stories.desktop.view.location.details.hostSceneButton

import com.soyle.stories.desktop.view.location.details.LocaleMock
import com.soyle.stories.desktop.view.location.details.UserActionsMock
import com.soyle.stories.desktop.view.location.details.`Host Scene Button Access`.Companion.access
import com.soyle.stories.desktop.view.testconfig.verifyDesign
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.location.details.components.HostSceneButton
import com.soyle.stories.location.details.models.AvailableSceneToHostModel
import javafx.beans.property.ReadOnlyListWrapper
import javafx.beans.property.SimpleStringProperty
import javafx.beans.value.WritableStringValue
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxAssert
import org.testfx.api.FxAssert.verifyThat
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.matcher.control.MenuItemMatchers
import tornadofx.observableListOf

class `Host Scene Button Presentation Test` : FxRobot() {

    private val primaryStage = FxToolkit.registerPrimaryStage()

    private val availableScenesToHost = ReadOnlyListWrapper<AvailableSceneToHostModel>(null)
    private val locale = LocaleMock()
    private val view = HostSceneButton(availableScenesToHost.readOnlyProperty, UserActionsMock(), locale)

    @Test
    fun `loading item should display text from locale`() {
        locale.loading.set("Hold up, we're loading here")
        FxAssert.verifyThat(view) { it.items.single().text == "Hold up, we're loading here" }
    }

    @Test
    fun design() = verifyDesign(primaryStage, view)

    @Nested
    inner class `Given Available Scenes are Loaded`
    {

        private fun `should display create scene button`() {
            locale.createScene.set("Creating scenes creates drama!")
            assertEquals("Creating scenes creates drama!", view.access().createSceneItem!!.text)
        }

        init {
            availableScenesToHost.set(
                observableListOf(
                    AvailableSceneToHostModel(Scene.Id(), SimpleStringProperty("I'm available!")),
                    AvailableSceneToHostModel(Scene.Id(), SimpleStringProperty("Me too!")),
                    AvailableSceneToHostModel(Scene.Id(), SimpleStringProperty("I'm reluctant to be..."))
                )
            )
        }

        @Test
        fun `should display available scene items`() {
            FxAssert.verifyThat(view) { it.access().availableSceneItems.size == 3 }

            val availableScenesById = availableScenesToHost.associateBy { it.sceneId.toString() }
            assertEquals(
                availableScenesById.keys,
                view.access().availableSceneItems.map { it.id }.toSet()
            )
            view.access().availableSceneItems.forEach {
                verifyThat(it, MenuItemMatchers.hasText(availableScenesById[it.id]!!.name.value))
            }

            `should display create scene button`()
        }

        @Test
        fun `available scene items should always display current scene name`() {
            val modifiedSceneItem = availableScenesToHost.random()
            (modifiedSceneItem.name as WritableStringValue).set("I have a fancy new name")

            val displayedItem = view.access().getSceneItemById(modifiedSceneItem.sceneId)!!
            assertEquals("I have a fancy new name", displayedItem.text)
        }

        @Test
        fun design() = verifyDesign(primaryStage, view)

        @Nested
        inner class `Given No Available Scenes`
        {

            init {
                availableScenesToHost.set(observableListOf())
            }

            @Test
            fun `should display special message from locale`() {
                locale.allExistingScenesInProjectHaveBeenHosted.set("Special messages for special people")
                assertEquals("Special messages for special people", view.items.component3().text)

                `should display create scene button`()
            }

            @Test
            fun design() = verifyDesign(primaryStage, view)

        }

    }



}