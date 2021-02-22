package com.soyle.stories.characterarc.baseStoryStructure.presenters

import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureViewModel
import com.soyle.stories.characterarc.baseStoryStructure.StoryStructureSectionViewModel
import com.soyle.stories.gui.View
import com.soyle.stories.usecase.character.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSection

class LinkLocationToCharacterArcSectionPresenter(
  private val view: View.Nullable<BaseStoryStructureViewModel>
) : LinkLocationToCharacterArcSection.OutputPort {
	override fun receiveLinkLocationToCharacterArcSectionResponse(response: LinkLocationToCharacterArcSection.ResponseModel) {
		val sectionId = response.characterArcSectionId.toString()
		val locationId = response.locationId.toString()
		view.updateOrInvalidated {
			val section = this.sections.find { it.sectionId == sectionId } ?: return@updateOrInvalidated this
			val location = this.availableLocations.find { it.id.uuid.toString() == locationId } ?: return@updateOrInvalidated this
			withSections(
			  sections = sections.map {
				  if (it.sectionId == section.sectionId) {
					  StoryStructureSectionViewModel(section.sectionTemplateName, sectionId, section.sectionValue,section.subsections, location)
				  } else it
			  }
			)
		}
	}

	override fun receiveLinkLocationToCharacterArcSectionFailure(failure: Exception) {}
}