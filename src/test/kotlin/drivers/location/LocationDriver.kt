package com.soyle.stories.desktop.config.drivers.location

import com.soyle.stories.di.get
import com.soyle.stories.di.scoped
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.location.controllers.CreateNewLocationController
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.project.WorkBench
import kotlinx.coroutines.runBlocking

class LocationDriver private constructor(private val projectScope: ProjectScope) {

    private val locationRepository
        get() = projectScope.get<LocationRepository>()

    fun getLocationByNameOrError(locationName: String): Location =
        getLocationByName(locationName) ?: error("No location with name $locationName")

    fun getLocationByName(locationName: String): Location?
    {
        val projectId = Project.Id(projectScope.projectId)
        val locations = runBlocking { locationRepository.getAllLocationsInProject(projectId) }
        val location = locations.find { it.name == locationName }
        location?.let {
            //previouslyNamedScenes[sceneName] = previouslyNamedScenes.getValue(sceneName).apply { add(scene.id) }
        }
        return location
    }

    fun createLocationWithName(locationName: String)
    {
        runBlocking {
            projectScope.get<CreateNewLocationController>().createNewLocation(locationName, "")
        }
    }

    fun givenLocationWithName(locationName: String): Location
    {
        return getLocationByName(locationName) ?: run {
            createLocationWithName(locationName)
            getLocationByNameOrError(locationName)
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