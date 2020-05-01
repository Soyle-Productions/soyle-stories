package com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection

import com.soyle.stories.characterarc.CharacterArcException
import com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSection
import com.soyle.stories.eventbus.Notifier

class UnlinkLocationFromCharacterArcSectionNotifier : UnlinkLocationFromCharacterArcSection.OutputPort, Notifier<UnlinkLocationFromCharacterArcSection.OutputPort>() {
	override fun receiveUnlinkLocationFromCharacterArcSectionFailure(failure: CharacterArcException) {
		notifyAll { it.receiveUnlinkLocationFromCharacterArcSectionFailure(failure) }
	}

	override fun receiveUnlinkLocationFromCharacterArcSectionResponse(response: UnlinkLocationFromCharacterArcSection.ResponseModel) {
		notifyAll { it.receiveUnlinkLocationFromCharacterArcSectionResponse(response) }
	}
}