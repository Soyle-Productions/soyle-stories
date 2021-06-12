package com.soyle.stories.desktop.config.features.location

import com.soyle.stories.desktop.config.drivers.location.*
import com.soyle.stories.desktop.config.drivers.scene.SceneDriver
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.desktop.view.location.details.`Location Details View Assertions`.Companion.assertThis
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.scene.Scene
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

class `Location Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
        Given("I have created a location named {string}") { locationName: String ->
            LocationDriver(soyleStories.getAnyOpenWorkbenchOrError()).givenLocationWithName(locationName)
        }
        Given("a location named {string} has been created") { locationName: String ->
            LocationDriver(soyleStories.getAnyOpenWorkbenchOrError()).givenLocationWithName(locationName)
        }
        Given("the following locations have been created") { data: DataTable ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val locationDriver = LocationDriver(workbench)
            data.asList().forEach { locationName ->
                locationDriver.givenLocationWithName(locationName)
            }
        }
        Given("I have created the following locations") { data: DataTable ->
            val workbench = soyleStories.getAnyOpenWorkbenchOrError()
            val locationDriver = LocationDriver(workbench)
            data.asList().forEach { locationName ->
                locationDriver.givenLocationWithName(locationName)
            }
        }
        Given("I have renamed the {location} to {string}") { location: Location, newName: String ->
            if (location.name.value != newName) {
                LocationDriver(soyleStories.getAnyOpenWorkbenchOrError())
                    .givenLocationRenamedTo(location.id, newName)
            }
        }
        Given(
            "I have removed the {location} from the story"
        ) { location: Location ->
            LocationDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenLocationDeleted(location)
        }
        Given(
            "I am looking at the {location}'s details"
        ) { location: Location ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenLocationListToolHasBeenOpened()
                .givenLocationDetailsToolHasBeenOpenedFor(location)
        }
        Given("I have added the {scene} to the {location}") { scene: Scene, location: Location ->
            SceneDriver(soyleStories.getAnyOpenWorkbenchOrError())
                .givenLocationUsedInScene(scene, location)
        }
    }

    private fun whens() {
        When("I rename the {location} to {string}") { location: Location, newName: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenLocationListToolHasBeenOpened()
                .renameLocationTo(location.id, newName)
        }
        When("I remove the {location} from the story") { location: Location ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenLocationListToolHasBeenOpened()
                .givenDeleteLocationDialogHasBeenOpened(location.id)
                .confirmDelete()
        }
        When("I request a list of scenes to host in the {location}") { location: Location ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenLocationListToolHasBeenOpened()
                .givenLocationDetailsToolHasBeenOpenedFor(location)
                .requestScenesToHost()
        }
        When("I add the {scene} to the {location}") { scene: Scene, location: Location ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenLocationListToolHasBeenOpened()
                .givenLocationDetailsToolHasBeenOpenedFor(location)
                .addScene(scene)
        }
    }

    private fun thens() {
        Then("a location named {string} should have been created") { expectedName: String ->
            LocationDriver(soyleStories.getAnyOpenWorkbenchOrError()).getLocationByNameOrError(expectedName)
        }
        Then("the {location} should not host a scene named {string}") { location: Location, sceneName: String ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenLocationListToolHasBeenOpened()
                .givenLocationDetailsToolHasBeenOpenedFor(location)
                .assertThis {
                    doesNotHaveSceneNamed(sceneName)
                }
        }
        Then("the {scene} should take place at the {location}") { scene: Scene, location: Location ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenLocationListToolHasBeenOpened()
                .givenLocationDetailsToolHasBeenOpenedFor(location)
                .assertThis {
                    hasScene(scene.id, scene.name.value)
                }
        }
        Then("the {scene} should be listed to be hosted in the {location}") { scene: Scene, location: Location ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenLocationListToolHasBeenOpened()
                .givenLocationDetailsToolHasBeenOpenedFor(location)
                .assertThis {
                    hasAvailableSceneItem(scene.id, scene.name.value)
                }
        }
        Then("the {scene} should not be listed to be hosted in the {location}") { scene: Scene, location: Location ->
            soyleStories.getAnyOpenWorkbenchOrError()
                .givenLocationListToolHasBeenOpened()
                .givenLocationDetailsToolHasBeenOpenedFor(location)
                .assertThis {
                    doesNotHaveAvailableSceneItem(scene.id)
                }
        }
    }

}