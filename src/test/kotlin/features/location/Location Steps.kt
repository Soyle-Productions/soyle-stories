package com.soyle.stories.desktop.config.features.location

import com.soyle.stories.desktop.config.drivers.location.LocationDriver
import com.soyle.stories.desktop.config.drivers.soylestories.getAnyOpenWorkbenchOrError
import com.soyle.stories.desktop.config.features.soyleStories
import io.cucumber.datatable.DataTable
import io.cucumber.java8.En

class `Location Steps` : En {

    init {
        givens()
        whens()
        thens()
    }

    private fun givens() {
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
    }

    private fun whens() {

    }

    private fun thens() {

    }

}