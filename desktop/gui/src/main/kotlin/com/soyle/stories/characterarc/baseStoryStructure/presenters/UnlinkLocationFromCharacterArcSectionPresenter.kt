package com.soyle.stories.characterarc.baseStoryStructure.presenters

import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureViewModel
import com.soyle.stories.characterarc.baseStoryStructure.StoryStructureSectionViewModel
import com.soyle.stories.gui.View
import com.soyle.stories.usecase.character.arc.section.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSection

class UnlinkLocationFromCharacterArcSectionPresenter(
  private val view: View.Nullable<BaseStoryStructureViewModel>
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

	override fun receiveUnlinkLocationFromCharacterArcSectionFailure(failure: Exception) {}
}