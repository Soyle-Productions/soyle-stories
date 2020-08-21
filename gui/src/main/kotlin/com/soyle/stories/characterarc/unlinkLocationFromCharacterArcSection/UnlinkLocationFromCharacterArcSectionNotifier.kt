package com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection

import com.soyle.stories.characterarc.CharacterArcException
import com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSection
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer

class UnlinkLocationFromCharacterArcSectionNotifier(
	private val threadTransformer: ThreadTransformer
) : UnlinkLocationFromCharacterArcSection.OutputPort, Notifier<UnlinkLocationFromCharacterArcSection.OutputPort>() {
	override fun receiveUnlinkLocationFromCharacterArcSectionFailure(failure: CharacterArcException) {
		threadTransformer.async {
			notifyAll { it.receiveUnlinkLocationFromCharacterArcSectionFailure(failure) }
		}
	}

	override fun receiveUnlinkLocationFromCharacterArcSectionResponse(response: UnlinkLocationFromCharacterArcSection.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveUnlinkLocationFromCharacterArcSectionResponse(response) }
		}
	}
}