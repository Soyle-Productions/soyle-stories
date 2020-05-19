package com.soyle.stories.characterarc.linkLocationToCharacterArcSection

import com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSection
import com.soyle.stories.common.Notifier

class LinkLocationToCharacterArcSectionNotifier : LinkLocationToCharacterArcSection.OutputPort, Notifier<LinkLocationToCharacterArcSection.OutputPort>() {

	override fun receiveLinkLocationToCharacterArcSectionResponse(response: LinkLocationToCharacterArcSection.ResponseModel) {
		notifyAll { it.receiveLinkLocationToCharacterArcSectionResponse(response) }
	}

	override fun receiveLinkLocationToCharacterArcSectionFailure(failure: Exception) {
		notifyAll { it.receiveLinkLocationToCharacterArcSectionFailure(failure) }
	}

}