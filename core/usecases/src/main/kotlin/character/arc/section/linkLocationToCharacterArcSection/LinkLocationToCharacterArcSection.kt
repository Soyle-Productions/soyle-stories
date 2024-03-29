package com.soyle.stories.usecase.character.arc.section.linkLocationToCharacterArcSection

import java.util.*

interface LinkLocationToCharacterArcSection {
	suspend operator fun invoke(characterArcSectionId: UUID, locationId: UUID, output: OutputPort)

	class ResponseModel(val characterArcSectionId: UUID, val locationId: UUID)

	interface OutputPort
	{
		fun receiveLinkLocationToCharacterArcSectionFailure(failure: Exception)
		fun receiveLinkLocationToCharacterArcSectionResponse(response: ResponseModel)
	}
}