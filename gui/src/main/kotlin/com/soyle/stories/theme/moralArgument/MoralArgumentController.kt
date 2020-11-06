package com.soyle.stories.theme.moralArgument

import com.soyle.stories.characterarc.addArcSectionToMoralArgument.AddArcSectionToMoralArgumentController
import com.soyle.stories.characterarc.changeSectionValue.ChangeSectionValueController
import com.soyle.stories.characterarc.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgumentController
import com.soyle.stories.characterarc.usecases.addCharacterArcSectionToMoralArgument.ListAvailableArcSectionTypesToAddToMoralArgument
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.theme.changeThemeDetails.changeCentralMoralQuestion.ChangeCentralMoralQuestionController
import com.soyle.stories.theme.changeThemeDetails.changeThematicRevelation.ChangeThematicRevelationController
import com.soyle.stories.theme.changeThemeDetails.changeThemeLine.ChangeThemeLineController
import com.soyle.stories.theme.usecases.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharacters
import com.soyle.stories.theme.usecases.outlineMoralArgument.GetMoralArgumentFrame
import com.soyle.stories.theme.usecases.outlineMoralArgument.OutlineMoralArgumentForCharacterInTheme
import java.util.*

class MoralArgumentController(
    themeId: String,
    private val threadTransformer: ThreadTransformer,
    private val getAvailablePerspectiveCharacters: ListAvailablePerspectiveCharacters,
    private val getAvailablePerspectiveCharactersOutput: ListAvailablePerspectiveCharacters.OutputPort,
    private val getMoralProblemAndThemeLine: GetMoralArgumentFrame,
    private val getMoralProblemAndThemeLineOutput: GetMoralArgumentFrame.OutputPort,
    private val outlineMoralArgument: OutlineMoralArgumentForCharacterInTheme,
    private val outlineMoralArgumentOutput: OutlineMoralArgumentForCharacterInTheme.OutputPort,
    private val listAvailableArcSectionTypesToAddToMoralArgument: ListAvailableArcSectionTypesToAddToMoralArgument,
    private val listAvailableArcSectionTypesToAddToMoralArgumentOutput: ListAvailableArcSectionTypesToAddToMoralArgument.OutputPort,
    private val addArcSectionToMoralArgumentController: AddArcSectionToMoralArgumentController,
    private val changeCentralMoralQuestionController: ChangeCentralMoralQuestionController,
    private val changeThemeLineController: ChangeThemeLineController,
    private val changeSectionValueController: ChangeSectionValueController,
    private val changeThematicRevelationController: ChangeThematicRevelationController,
    private val moveCharacterArcSectionInMoralArgumentController: MoveCharacterArcSectionInMoralArgumentController
) : MoralArgumentViewListener {

    private val themeId: UUID = UUID.fromString(themeId)

    override fun getValidState() {
        threadTransformer.async {
            getMoralProblemAndThemeLine.invoke(themeId, getMoralProblemAndThemeLineOutput)
        }
    }

    override fun getPerspectiveCharacters() {
        threadTransformer.async {
            getAvailablePerspectiveCharacters.invoke(themeId, getAvailablePerspectiveCharactersOutput)
        }
    }

    override fun outlineMoralArgument(characterId: String) {
        val preparedCharacterId = UUID.fromString(characterId)
        threadTransformer.async {
            outlineMoralArgument.invoke(themeId, preparedCharacterId, outlineMoralArgumentOutput)
        }
    }

    override fun getAvailableArcSectionTypesToAdd(characterId: String) {
        val preparedCharacterId = UUID.fromString(characterId)
        threadTransformer.async {
            listAvailableArcSectionTypesToAddToMoralArgument.invoke(
                themeId,
                preparedCharacterId,
                listAvailableArcSectionTypesToAddToMoralArgumentOutput
            )
        }
    }

    override fun addCharacterArcSectionType(characterId: String, sectionTemplateId: String) {
        addArcSectionToMoralArgumentController.addCharacterArcSectionToMoralArgument(
            themeId.toString(), characterId, sectionTemplateId, null
        )
    }

    override fun addCharacterArcSectionTypeAtIndex(characterId: String, sectionTemplateId: String, index: Int) {
        addArcSectionToMoralArgumentController.addCharacterArcSectionToMoralArgument(
            themeId.toString(), characterId, sectionTemplateId, index
        )
    }

    override fun setMoralProblem(problem: String) {
        changeCentralMoralQuestionController.updateCentralMoralQuestion(themeId.toString(), problem)
    }

    override fun setThemeLine(themeLine: String) {
        changeThemeLineController.changeThemeLine(themeId.toString(), themeLine)
    }

    override fun setValueOfArcSection(characterId: String, arcSectionId: String, value: String) {
        changeSectionValueController.changeValueOfArcSection(
            themeId.toString(),
            characterId,
            arcSectionId,
            value
        )
    }

    override fun setThematicRevelation(revelation: String) {
        changeThematicRevelationController.changeThematicRevelation(themeId.toString(), revelation)
    }

    override fun moveSectionTo(arcSectionId: String, characterId: String, index: Int) {
        moveCharacterArcSectionInMoralArgumentController.moveSectionInMoralArgument(arcSectionId, themeId.toString(), characterId, index)
    }
}