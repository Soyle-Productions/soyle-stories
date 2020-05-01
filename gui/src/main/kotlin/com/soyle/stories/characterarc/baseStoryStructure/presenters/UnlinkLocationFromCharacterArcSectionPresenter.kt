package com.soyle.stories.characterarc.baseStoryStructure.presenters

import com.soyle.stories.characterarc.CharacterArcException
import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureView
import com.soyle.stories.characterarc.baseStoryStructure.StoryStructureSectionViewModel
import com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSection

class UnlinkLocationFromCharacterArcSectionPresenter(
  private val view: BaseStoryStructureView
) : UnlinkLocationFromCharacterArcSection.OutputPort {

	override fun receiveUnlinkLocationFromCharacterArcSectionResponse(response: UnlinkLocationFromCharacterArcSection.ResponseModel) {
		val sectionId = response.characterArcSectionId.toString()
		view.updateOrInvalidated {
			val section = this.sections.find { it.sectionId == sectionId } ?: return@updateOrInvalidated this
			withSections(
			  sections = sections.map {
				  if (it.sectionId == section.sectionId) {
					  StoryStructureSectionViewModel(section.sectionTemplateName, sectionId, section.sectionValue,section.subsections, null)
				  } else it
			  }
			)
		}
	}

	override fun receiveUnlinkLocationFromCharacterArcSectionFailure(failure: CharacterArcException) {}
}