package com.soyle.stories.usecase.location.renameLocation

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events.LocationRenamed
import com.soyle.stories.domain.prose.events.MentionTextReplaced
import com.soyle.stories.domain.scene.events.SceneSettingLocationRenamed
import com.soyle.stories.domain.validation.SingleNonBlankLine

interface RenameLocation {
	suspend operator fun invoke(id: Location.Id, name: SingleNonBlankLine, output: OutputPort)

	class ResponseModel(
        val locationRenamed: LocationRenamed,
        val mentionTextReplaced: List<MentionTextReplaced>,
        val sceneSettingLocationsRenamed: List<SceneSettingLocationRenamed>
	)

	interface OutputPort {
		suspend fun receiveRenameLocationResponse(response: ResponseModel)
	}
}