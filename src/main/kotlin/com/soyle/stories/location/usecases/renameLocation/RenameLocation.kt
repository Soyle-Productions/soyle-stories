package com.soyle.stories.location.usecases.renameLocation

import com.soyle.stories.location.LocationException
import java.util.*

interface RenameLocation {
	suspend operator fun invoke(id: UUID, name: String, output: OutputPort)

	class ResponseModel(val locationId: UUID, val newName: String)

	interface OutputPort {
		fun receiveRenameLocationFailure(failure: LocationException)
		fun receiveRenameLocationResponse(response: ResponseModel)
	}
}