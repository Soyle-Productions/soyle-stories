package com.soyle.stories.desktop.config.features.location

import com.soyle.stories.desktop.config.drivers.location.*
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import com.soyle.stories.entities.Location
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
    }

    private fun thens() {
        Then("a location named {string} should have been created") { expectedName: String ->
            LocationDriver(soyleStories.getAnyOpenWorkbenchOrError()).getLocationByNameOrError(expectedName)
        }
    }

}