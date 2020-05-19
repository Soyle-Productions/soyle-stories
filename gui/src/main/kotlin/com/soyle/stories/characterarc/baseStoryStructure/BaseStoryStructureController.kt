package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.characterarc.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionController
import com.soyle.stories.characterarc.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionController
import com.soyle.stories.characterarc.usecaseControllers.ChangeThematicSectionValueController
import com.soyle.stories.characterarc.viewBaseStoryStructure.ViewBaseStoryStructureController
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.location.usecases.listAllLocations.ListAllLocations

class BaseStoryStructureController(
  private val threadTransformer: ThreadTransformer,
  private val themeId: String,
  private val characterId: String,
  private val listAllLocations: ListAllLocations,
  private val listAllLocationsOutputPort: ListAllLocations.OutputPort,
  private val viewBaseStoryStructureController: ViewBaseStoryStructureController,
  private val changeThematicSectionValueController: ChangeThematicSectionValueController,
  private val linkLocationToCharacterArcSectionController: LinkLocationToCharacterArcSectionController,
  private val unlinkLocationToCharacterArcSectionController: UnlinkLocationFromCharacterArcSectionController
) : BaseStoryStructureViewListener {

	override fun getBaseStoryStructure() {
		viewBaseStoryStructureController.getBaseStoryStructure(characterId, themeId)
		threadTransformer.async {
			listAllLocations.invoke(listAllLocationsOutputPort)
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