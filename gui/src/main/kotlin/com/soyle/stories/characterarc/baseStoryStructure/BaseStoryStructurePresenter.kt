package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.characterarc.eventbus.EventBus
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValue

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 10:34 PM
 */
class BaseStoryStructurePresenter(
    private val view: BaseStoryStructureView,
    eventBus: EventBus
) : ViewBaseStoryStructure.OutputPort {

    private val changeThematicSectionValueOutputPort: ChangeThematicSectionValue.OutputPort =
        ChangeThematicSectionValuePresenter(view)

    init {
        eventBus.changeThematicSectionValue.addListener(changeThematicSectionValueOutputPort)
    }

    override fun receiveViewBaseStoryStructureResponse(response: ViewBaseStoryStructure.ResponseModel) {
        view.update {
            BaseStoryStructureViewModel(
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

    override fun receiveViewBaseStoryStructureFailure(failure: Exception) {}
}