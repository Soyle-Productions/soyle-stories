package com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection

import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.character.arc.section.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSection

class UnlinkLocationFromCharacterArcSectionNotifier(
	private val threadTransformer: ThreadTransformer
) : UnlinkLocationFromCharacterArcSection.OutputPort, Notifier<UnlinkLocationFromCharacterArcSection.OutputPort>() {
	override fun receiveUnlinkLocationFromCharacterArcSectionFailure(failure: Exception) {
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