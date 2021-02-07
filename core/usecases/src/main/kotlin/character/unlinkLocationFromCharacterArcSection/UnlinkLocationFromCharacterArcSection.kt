package com.soyle.stories.usecase.character.unlinkLocationFromCharacterArcSection

import java.util.*

interface UnlinkLocationFromCharacterArcSection {
	suspend operator fun invoke(characterArcSectionId: UUID, output: OutputPort)

	class ResponseModel(val characterArcSectionId: UUID)

	interface OutputPort {
		fun receiveUnlinkLocationFromCharacterArcSectionFailure(failure: Exception)
		fun receiveUnlinkLocationFromCharacterArcSectionResponse(response: ResponseModel)
	}

}