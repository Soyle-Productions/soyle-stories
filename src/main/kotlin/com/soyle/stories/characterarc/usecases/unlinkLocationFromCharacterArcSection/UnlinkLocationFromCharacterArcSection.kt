package com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection

import com.soyle.stories.characterarc.CharacterArcException
import java.util.*

interface UnlinkLocationFromCharacterArcSection {
	suspend operator fun invoke(characterArcSectionId: UUID, output: OutputPort)

	class ResponseModel(val characterArcSectionId: UUID)

	interface OutputPort {
		fun receiveUnlinkLocationFromCharacterArcSectionFailure(failure: CharacterArcException)
		fun receiveUnlinkLocationFromCharacterArcSectionResponse(response: ResponseModel)
	}

}