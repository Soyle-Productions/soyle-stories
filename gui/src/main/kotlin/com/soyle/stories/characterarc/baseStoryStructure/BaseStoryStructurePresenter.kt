package com.soyle.stories.characterarc.baseStoryStructure

import com.soyle.stories.characterarc.baseStoryStructure.presenters.ChangeThematicSectionValuePresenter
import com.soyle.stories.characterarc.eventbus.CharacterArcEvents
import com.soyle.stories.characterarc.usecases.viewBaseStoryStructure.ViewBaseStoryStructure
import com.soyle.stories.eventbus.listensTo
import com.soyle.stories.theme.usecases.changeThematicSectionValue.ChangeThematicSectionValue

/**
 * Created by Brendan
 * Date: 2/9/2020
 * Time: 10:34 PM
 */
class BaseStoryStructurePresenter(
  private val view: BaseStoryStructureView,
  characterArcEvents: CharacterArcEvents
) : ViewBaseStoryStructure.OutputPort {

    private val changeThematicSectionValueOutputPort: ChangeThematicSectionValue.OutputPort =
      ChangeThematicSectionValuePresenter(view)

    init {
        changeThematicSectionValueOutputPort listensTo characterArcEvents.changeThematicSectionValue
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