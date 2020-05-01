package com.soyle.stories.characterarc.baseStoryStructure.presenters

import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureView
import com.soyle.stories.characterarc.baseStoryStructure.StoryStructureSectionViewModel
import com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSection

class LinkLocationToCharacterArcSectionPresenter(
  private val view: BaseStoryStructureView
) : LinkLocationToCharacterArcSection.OutputPort {
	override fun receiveLinkLocationToCharacterArcSectionResponse(response: LinkLocationToCharacterArcSection.ResponseModel) {
		val sectionId = response.characterArcSectionId.toString()
		val locationId = response.locationId.toString()
		view.updateOrInvalidated {
			val section = this.sections.find { it.sectionId == sectionId } ?: return@updateOrInvalidated this
			val location = this.availableLocations.find { it.id == locationId } ?: return@updateOrInvalidated this
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