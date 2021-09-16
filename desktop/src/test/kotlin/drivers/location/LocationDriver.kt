package com.soyle.stories.desktop.config.drivers.location

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.SingleLine
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.domain.validation.countLines
import com.soyle.stories.location.controllers.CreateNewLocationController
import com.soyle.stories.location.controllers.RenameLocationController
import com.soyle.stories.location.deleteLocation.DeleteLocationController
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import com.soyle.stories.usecase.location.LocationRepository
import kotlinx.coroutines.runBlocking

class LocationDriver private constructor(private val projectScope: ProjectScope) {

    private val locationRepository
        get() = projectScope.get<LocationRepository>()

    fun getLocationByNameOrError(locationName: String): Location =
        getLocationByName(locationName) ?: error("No location with name $locationName")

    fun getLocationByName(locationName: String): Location? {
        val projectId = Project.Id(projectScope.projectId)
        val locations = runBlocking { locationRepository.getAllLocationsInProject(projectId) }
        val location = locations.find { it.name.value == locationName }
        location?.let {
            //previouslyNamedScenes[sceneName] = previouslyNamedScenes.getValue(sceneName).apply { add(scene.id) }
        }
        return location
    }

    fun createLocationWithName(locationName: String) {
        runBlocking {
            projectScope.get<CreateNewLocationController>().createNewLocation(SingleNonBlankLine.create(countLines(locationName) as SingleLine)!!, "").await()
        }
    }

    fun givenLocationWithName(locationName: String): Location {
        return getLocationByName(locationName) ?: run {
            createLocationWithName(locationName)
            getLocationByNameOrError(locationName)
        }
    }

    fun givenLocationRenamedTo(locationId: Location.Id, newName: String) {
        val controller = projectScope.get<RenameLocationController>()
        runBlocking {
            controller.renameLocation(locationId, SingleNonBlankLine.create(countLines(newName) as SingleLine)!!)
        }
    }

    fun givenLocationDeleted(location: Location)
    {
        val controller = projectScope.get<DeleteLocationController>()
        runBlocking {
            controller.deleteLocation(location.id)
        }
    }

    companion object {
        init {
            scoped<ProjectScope> { provide { LocationDriver(this) } }
        }

        operator fun invoke(workBench: WorkBench): LocationDriver = invoke(workBench.scope)
        operator fun invoke(projectScope: ProjectScope): LocationDriver = projectScope.get()
    }
}