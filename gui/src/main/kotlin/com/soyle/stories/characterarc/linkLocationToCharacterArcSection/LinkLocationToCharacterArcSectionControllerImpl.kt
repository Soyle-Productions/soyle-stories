package com.soyle.stories.characterarc.linkLocationToCharacterArcSection

import com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSection
import com.soyle.stories.common.ThreadTransformer
import java.util.*

class LinkLocationToCharacterArcSectionControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val linkLocationToCharacterArcSection: LinkLocationToCharacterArcSection,
  private val linkLocationToCharacterArcSectionOutputPort: LinkLocationToCharacterArcSection.OutputPort

) : LinkLocationToCharacterArcSectionController {
	override fun linkLocation(characterArcSectionId: String, locationId: String) {
		val characterArcSectionUUID = UUID.fromString(characterArcSectionId)
		val locationUUID = UUID.fromString(locationId)
		threadTransformer.async {
			linkLocationToCharacterArcSection.invoke(
			  characterArcSectionUUID, locationUUID, linkLocationToCharacterArcSectionOutputPort
			)
		}
	}
}