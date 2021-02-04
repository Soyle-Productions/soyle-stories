package com.soyle.stories.location.usecases.createNewLocation

import com.soyle.stories.common.SingleNonBlankLine
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.Project
import com.soyle.stories.location.LocationException
import com.soyle.stories.location.repositories.LocationRepository
import java.util.*

class CreateNewLocationUseCase(
  projectId: UUID,
  private val locationRepository: LocationRepository
) : CreateNewLocation {

	private val projectId: Project.Id = Project.Id(projectId)

	override suspend fun invoke(name: SingleNonBlankLine, description: String?, output: CreateNewLocation.OutputPort) {
		val response = try {
			createNewLocation(name, description)
		} catch (l: LocationException) {
			return output.receiveCreateNewLocationFailure(l)
		}
		output.receiveCreateNewLocationResponse(response)
	}

	private suspend fun createNewLocation(name: SingleNonBlankLine, description: String?): CreateNewLocation.ResponseModel {
		val location = createLocation(name, description)
		addNewLocation(location)
		return CreateNewLocation.ResponseModel(location.id.uuid, name.value)
	}

	private fun createLocation(name: SingleNonBlankLine, description: String?): Location {
		val id = Location.Id()
		return Location(id, projectId, name, description ?: "")
	}

	private suspend fun addNewLocation(location: Location) {
		locationRepository.addNewLocation(location)
	}
}