package com.soyle.stories.usecase.location.createNewLocation

import com.soyle.stories.domain.location.HostedScene
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.domain.validation.entitySetOf
import com.soyle.stories.usecase.location.LocationRepository
import java.util.*

class CreateNewLocationUseCase(
  projectId: UUID,
  private val locationRepository: LocationRepository
) : CreateNewLocation {

	private val projectId: Project.Id = Project.Id(projectId)

	override suspend fun invoke(name: SingleNonBlankLine, description: String?, output: CreateNewLocation.OutputPort) {
		val response = try {
			createNewLocation(name, description)
		} catch (l: Exception) {
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
		return Location(id, projectId, name, description ?: "", entitySetOf())
	}

	private suspend fun addNewLocation(location: Location) {
		locationRepository.addNewLocation(location)
	}
}