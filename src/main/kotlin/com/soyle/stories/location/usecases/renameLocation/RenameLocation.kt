package com.soyle.stories.location.usecases.renameLocation

import com.soyle.stories.common.SingleNonBlankLine
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.LocationRenamed

interface RenameLocation {
	suspend operator fun invoke(id: Location.Id, name: SingleNonBlankLine, output: OutputPort)

	class ResponseModel(val locationRenamed: LocationRenamed)

	interface OutputPort {
		suspend fun receiveRenameLocationResponse(response: ResponseModel)
	}
}