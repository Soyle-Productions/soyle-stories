package com.soyle.stories.characterarc.baseStoryStructure.presenters

import com.soyle.stories.characterarc.baseStoryStructure.BaseStoryStructureViewModel
import com.soyle.stories.characterarc.baseStoryStructure.StoryStructureSectionViewModel
import com.soyle.stories.gui.View
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValue

internal class ChangeThematicSectionValuePresenter(
    private val view: View.Nullable<BaseStoryStructureViewModel>
) : ChangeThematicSectionValue.OutputPort {

    override fun receiveChangeThematicSectionValueFailure(failure: Exception) {
    }

    override fun receiveChangeThematicSectionValueResponse(response: ChangeThematicSectionValue.ResponseModel) {
        view.updateOrInvalidated {
            val section = sections.find { it.sectionId == response.thematicSectionId.toString() }
                ?: return@updateOrInvalidated this
            withSections(
              sections.map {
                  if (it.sectionId == section.sectionId) StoryStructureSectionViewModel(
                    section.sectionTemplateName,
                    section.sectionId,
                    response.newValue,
                    section.subsections,
                    section.linkedLocation
                  )
                  else it
              }
            )
        }
    }

}