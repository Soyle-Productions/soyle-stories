package com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection

import com.soyle.stories.usecase.character.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSection
import com.soyle.stories.common.ThreadTransformer
import java.util.*

class UnlinkLocationFromCharacterArcSectionControllerImpl(
  private val threadTransformer: ThreadTransformer,
  private val unlinkLocationFromCharacterArcSection: UnlinkLocationFromCharacterArcSection,
  private val unlinkLocationFromCharacterArcSectionOutputPort: UnlinkLocationFromCharacterArcSection.OutputPort
) : UnlinkLocationFromCharacterArcSectionController {

	override fun unlinkLocationFromCharacterArcSection(sectionId: String) {
		val characterArcSectionId = UUID.fromString(sectionId)
		threadTransformer.async {
			unlinkLocationFromCharacterArcSection.invoke(characterArcSectionId, unlinkLocationFromCharacterArcSectionOutputPort)
		}
	}
}