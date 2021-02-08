package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.characterarc.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionController
import com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionController
import com.soyle.stories.characterarc.usecaseControllers.ChangeThematicSectionValueController
import com.soyle.stories.characterarc.viewBaseStoryStructure.ViewBaseStoryStructureController
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.usecase.location.listAllLocations.ListAllLocations

class BaseStoryStructureController(
  private val threadTransformer: ThreadTransformer,
  private val themeId: String,
  private val characterId: String,
  private val listAllLocations: ListAllLocations,
  private val viewBaseStoryStructureController: ViewBaseStoryStructureController,
  private val changeThematicSectionValueController: ChangeThematicSectionValueController,
  private val linkLocationToCharacterArcSectionController: LinkLocationToCharacterArcSectionController,
  private val unlinkLocationToCharacterArcSectionController: UnlinkLocationFromCharacterArcSectionController,
  private val eventReceiver: BaseStoryStructureEventReceiver
) : BaseStoryStructureViewListener {

	override fun getBaseStoryStructure() {
		viewBaseStoryStructureController.getBaseStoryStructure(characterId, themeId, eventReceiver)
		threadTransformer.async {
			listAllLocations.invoke(eventReceiver)
		}
	}

	override fun changeSectionValue(sectionId: String, value: String) {
		changeThematicSectionValueController.changeThematicSectionValue(sectionId, value)
	}

	override fun linkLocation(sectionId: String, locationId: String) {
        linkLocationToCharacterArcSectionController.linkLocation(sectionId, locationId)
	}

	override fun unlinkLocation(sectionId: String) {
		unlinkLocationToCharacterArcSectionController.unlinkLocationFromCharacterArcSection(sectionId)
	}
}