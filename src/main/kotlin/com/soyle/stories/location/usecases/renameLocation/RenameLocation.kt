package com.soyle.stories.location.usecases.renameLocation

import com.soyle.stories.common.SingleNonBlankLine
import com.soyle.stories.entities.Location
import com.soyle.stories.entities.LocationRenamed
import com.soyle.stories.prose.MentionTextReplaced

interface RenameLocation {
	suspend operator fun invoke(id: Location.Id, name: SingleNonBlankLine, output: OutputPort)

	class ResponseModel(
		val locationRenamed: LocationRenamed,
		val mentionTextReplaced: List<MentionTextReplaced>
	)

	interface OutputPort {
		suspend fun receiveRenameLocationResponse(response: ResponseModel)
	}
}