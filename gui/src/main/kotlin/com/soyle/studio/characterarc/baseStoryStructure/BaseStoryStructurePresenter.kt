package com.soyle.studio.characterarc.baseStoryStructure

import com.soyle.studio.characterarc.viewBaseStoryStructure.ViewBaseStoryStructure

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 10:34 PM
 */
class BaseStoryStructurePresenter(
    private val view: BaseStoryStructureView
) : ViewBaseStoryStructure.OutputPort {
    override fun receiveViewBaseStoryStructureResponse(response: ViewBaseStoryStructure.BaseStoryStructure) {
        view.update {
            BaseStoryStructureViewModel(
                response.themeId.toString(),
                response.characterId.toString(),
                response.sections.map {
                    StoryStructureSectionViewModel(
                        it.templateName,
                        it.arcSectionId.toString(),
                        it.value,
                        it.subSections.map { (subSectionName, value) ->
                            SubSectionViewModel(it.templateName, it.arcSectionId.toString(), subSectionName, value)
                        })
                }
            )
        }
    }

    override fun receiveViewBaseStoryStructureFailure(failure: Exception) {
        throw failure
    }
}