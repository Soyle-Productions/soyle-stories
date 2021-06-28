package com.soyle.stories.usecase.location.listAllLocations

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.usecase.location.LocationRepository
import java.util.*

class ListAllLocationsUseCase(
  projectId: UUID,
  private val locationRepository: LocationRepository
) : ListAllLocations {

	private val projectId: Project.Id = Project.Id(projectId)

	override suspend fun invoke(output: ListAllLocations.OutputPort) {
		val response = getResponseModel()
		output.receiveListAllLocationsResponse(response)
	}

	private suspend fun getResponseModel(): ListAllLocations.ResponseModel {
		val locations = getAllLocations()
		val locationItems = locations.map(::convertLocationToLocationItem)
		return ListAllLocations.ResponseModel(locationItems)
	}

	private suspend fun getAllLocations() =
	  locationRepository.getAllLocationsInProject(projectId)

	private fun convertLocationToLocationItem(location: Location): LocationItem =
	  LocationItem(location.id, location.name.value)
}