package com.soyle.stories.desktop.config.features.scene

import com.soyle.stories.desktop.config.drivers.location.LocationDriver
import com.soyle.stories.desktop.config.drivers.scene.*
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.scene.sceneSetting.SceneSettingAssertions
import com.soyle.stories.desktop.view.scene.sceneSetting.SceneSettingAssertions.Companion.assertThat
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingLocation
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En
import org.junit.jupiter.api.Assertions.*

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
        Given("I have used the following locations as settings for the {scene}") { scene: Scene,  locationData: DataTable ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val locationDriver = LocationDriver(workbench)
            val sceneDriver = SceneDriver(workbench)
            locationData.asList().forEach { locationName ->
                val location = locationDriver.getLocationByNameOrError(locationName)
                sceneDriver.givenLocationUsedInScene(scene, location)
            }
        }
        Given("I am mapping the {scene}'s setting locations") { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
        }
        Given("I have stopped using the {string} setting in the {scene}") { settingName: String, scene: Scene ->
            val sceneSetting = scene.settings.find { it.locationName == settingName } ?: return@Given

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenSettingRemoved(sceneSetting)
        }
        Given(
            "I have replaced the {string} setting in the {scene} with the {location}"
        ) { settingName: String, scene: Scene, replacement: Location ->
            val sceneSetting = scene.settings.find { it.locationName == settingName } ?: return@Given

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenSettingReplacedWith(sceneSetting, replacement)
        }
        Given(
            "I wanted to replace the {string} setting in the {scene}"
        ) { settingName: String, scene: Scene ->
            val sceneSetting = scene.settings.find { it.locationName == settingName } ?: return@Given

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenReplacementOptionsLoadedFor(sceneSetting)
        }
    }

    private fun whens() {
        When("I map the {scene}'s setting locations") { scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .focusOn(scene)
        }
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
        When("I stop using {string} as a setting for the {scene}") { settingName: String, scene: Scene ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
                .removeLocation(settingName)
        }
        When(
            "I want to replace the {string} setting in the {scene}"
        ) { settingName: String, scene: Scene ->
            val sceneSetting: SceneSettingLocation = scene.settings.find { it.locationName == settingName }!!
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
                .loadReplacementOptionsFor(sceneSetting)
        }
        When(
            "I replace the {string} setting in the {scene} with the {location}"
        ) { settingName: String, scene: Scene, replacement: Location ->
            val sceneSetting = scene.settings.find { it.locationName == settingName }!!

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
                .replaceSettingWith(sceneSetting, replacement)
        }
        When(
            "I select the {location} to replace the {string} setting in the {scene}"
        ) { replacement: Location, settingName: String, scene: Scene  ->
            val sceneSetting = scene.settings.find { it.locationName == settingName }!!

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenReplacementOptionsLoadedFor(sceneSetting)
                .replaceSettingWith(sceneSetting, replacement)
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
        Then(
            "the {scene}'s {string} setting should indicate that it was removed"
        ) { scene: Scene, settingName: String ->
            val sceneSetting = scene.settings.find { it.locationName == settingName }!!

            val sceneSettingView = soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
            SceneSettingAssertions.assertThat(sceneSettingView) {
                hasLocationNamed(settingName)
                locationIndicatesIssue(settingName)
            }
        }
        Then(
            "the following locations should be listed to replace the {string} setting in the {scene}"
        ) { settingName: String, scene: Scene, locationData: DataTable ->
            val sceneSetting = scene.settings.find { it.locationName == settingName }!!
            val locationNames = locationData.asList().toList()

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenReplacementOptionsLoadedFor(sceneSetting)
                .assertThat {
                    locationNames.forEach { locationName ->
                        sceneSettingItemHasReplacementOption(sceneSetting.id, locationName)
                    }
                }
        }
        Then(
            "the {location} should be listed to replace the {string} setting in the {scene}"
        ) { replacement: Location, settingName: String, scene: Scene ->
            val sceneSetting = scene.settings.find { it.locationName == settingName }!!

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenReplacementOptionsLoadedFor(sceneSetting)
                .assertThat {
                    sceneSettingItemHasReplacementOption(sceneSetting.id, replacement.name.value)
                }
        }
        Then(
            "there should be no available locations to replace the {string} setting in the {scene}"
        ) { settingName: String, scene: Scene ->
            val sceneSetting = scene.settings.find { it.locationName == settingName }!!

            soyleStories.getAnyOpenWorkbenchOrError()
                .givenSceneSettingToolHasBeenOpened()
                .givenFocusedOn(scene)
                .givenReplacementOptionsLoadedFor(sceneSetting)
                .assertThat {
                    sceneSettingItemHasNoReplacementOptions(sceneSetting.id)
                }
        }
    }

}