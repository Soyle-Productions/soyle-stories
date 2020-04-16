package com.soyle.stories.location.usecases.listAllLocations

interface ListAllLocations {

	suspend operator fun invoke(output: OutputPort)

	class ResponseModel(val locations: List<LocationItem>)

	interface OutputPort {
		fun receiveListAllLocationsResponse(response: ResponseModel)
	}

}