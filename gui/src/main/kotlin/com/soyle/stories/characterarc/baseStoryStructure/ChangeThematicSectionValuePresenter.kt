package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValue

internal class ChangeThematicSectionValuePresenter(
    private val view: BaseStoryStructureView
) : ChangeThematicSectionValue.OutputPort {

    override fun receiveChangeThematicSectionValueFailure(failure: Exception) {
    }

    override fun receiveChangeThematicSectionValueResponse(response: ChangeThematicSectionValue.ResponseModel) {
        view.update {
            val section = sections.find { it.sectionId == response.thematicSectionId.toString() }
                ?: return@update this
            BaseStoryStructureViewModel(
                sections.map {
                    if (it.sectionId == section.sectionId) StoryStructureSectionViewModel(
                        section.sectionTemplateName,
                        section.sectionId,
                        response.newValue,
                        section.subsections
                    )
                    else it
                }
            )
        }
    }

}