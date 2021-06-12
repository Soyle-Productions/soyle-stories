package com.soyle.stories.desktop.config.features.scene

import com.soyle.stories.desktop.config.drivers.scene.*
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.sceneSetting.SceneSettingAssertions
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue

class `Scene Setting Location Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("I have used the {location} as a setting for the {scene}") { location: Location, scene: Scene ->
            SceneDriver(soyleStories.getAnyOpenWorkbenchOrError()).givenLocationUsedInScene(scene, location)
        }
        Given("I am mapping the {scene}'s setting locations") { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
        }
    }

    private fun whens() {
        When("I use the {location} as a setting for the {scene}") { location: Location, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenAvailableLocationsLoaded()
                .selectAvailableLocation(location)
        }
        When("I stop using the {location} as a setting for the {scene}") { location: Location, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
                .removeLocation(location)
        }
    }

    private fun thens() {
        Then("the {location} should be a setting for the {scene}") { location: Location, scene: Scene ->
            assertTrue(location.id in scene)

            val sceneSettingView = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneSettingAssertions.assertThat(sceneSettingView) {
                hasLocation(location)
            }
        }
        Then("the {location} should not be a setting for the {scene}") { location: Location, scene: Scene ->
            assertFalse(location.id in scene)

            val sceneSettingView = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneSettingAssertions.assertThat(sceneSettingView) {
                doesNotHaveLocation(location)
            }
        }
        Then("the {scene} should not have a setting named {string}") { scene: Scene, settingName: String ->
            assertFalse(scene.settings.any { it.locationName == settingName })

            val sceneSettingView = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneSettingAssertions.assertThat(sceneSettingView) {
                doesNotHaveLocationNamed(settingName)
            }
        }
        Then("the {scene} should still have a setting named {string}") { scene: Scene, settingName: String ->
            assertTrue(scene.settings.any { it.locationName == settingName })

            val sceneSettingView = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneSettingAssertions.assertThat(sceneSettingView) {
                hasLocationNamed(settingName)
            }
        }
    }

}