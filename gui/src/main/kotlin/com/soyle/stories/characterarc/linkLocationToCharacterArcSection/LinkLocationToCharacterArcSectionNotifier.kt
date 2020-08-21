package com.soyle.stories.characterarc.linkLocationToCharacterArcSection

import com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSection
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.ThreadTransformer

class LinkLocationToCharacterArcSectionNotifier(
	private val threadTransformer: ThreadTransformer
) : LinkLocationToCharacterArcSection.OutputPort, Notifier<LinkLocationToCharacterArcSection.OutputPort>() {

	override fun receiveLinkLocationToCharacterArcSectionResponse(response: LinkLocationToCharacterArcSection.ResponseModel) {
		threadTransformer.async {
			notifyAll { it.receiveLinkLocationToCharacterArcSectionResponse(response) }
		}
	}

	override fun receiveLinkLocationToCharacterArcSectionFailure(failure: Exception) {
		threadTransformer.async {
			notifyAll { it.receiveLinkLocationToCharacterArcSectionFailure(failure) }
		}
	}

}