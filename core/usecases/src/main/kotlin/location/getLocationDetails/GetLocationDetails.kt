package com.soyle.stories.usecase.location.getLocationDetails

import com.soyle.stories.usecase.location.HostedSceneItem
import java.util.UUID

interface GetLocationDetails {

	suspend operator fun invoke(locationId: UUID, output: OutputPort)

	class ResponseModel(
		val locationId: UUID,
		val locationName: String,
		val locationDescription: String,
		val hostedScenes: List<HostedSceneItem>
	)

	fun interface OutputPort {
		suspend fun receiveGetLocationDetailsResponse(response: ResponseModel)
	}
}
