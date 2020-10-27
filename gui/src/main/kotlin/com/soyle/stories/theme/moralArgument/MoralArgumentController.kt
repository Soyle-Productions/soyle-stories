package com.soyle.stories.theme.moralArgument

import com.soyle.stories.characterarc.addArcSectionToMoralArgument.AddArcSectionToMoralArgumentController
import com.soyle.stories.characterarc.changeSectionValue.ChangeSectionValueController
import com.soyle.stories.common.ThreadTransformer
import com.soyle.stories.scene.usecases.coverCharacterArcSectionsInScene.GetAvailableCharacterArcSectionTypesForCharacterArc
import com.soyle.stories.theme.changeThemeDetails.ChangeCentralMoralQuestionController
import com.soyle.stories.theme.changeThemeDetails.ChangeThemeLineController
import com.soyle.stories.theme.outlineMoralArgument.OutlineMoralArgumentController
import com.soyle.stories.theme.usecases.outlineMoralArgument.GetMoralProblemAndThemeLineInTheme
import java.util.*

class MoralArgumentController(
    themeId: String,
    private val threadTransformer: ThreadTransformer,
    private val getMoralProblemAndThemeLine: GetMoralProblemAndThemeLineInTheme,
    private val getMoralProblemAndThemeLineOutput: GetMoralProblemAndThemeLineInTheme.OutputPort,
    private val outlineMoralArgumentController: OutlineMoralArgumentController,
    private val getAvailableArcSectionTypesForCharacterArc: GetAvailableCharacterArcSectionTypesForCharacterArc,
    private val getAvailableArcSectionTypesForCharacterArcOutput: GetAvailableCharacterArcSectionTypesForCharacterArc.OutputPort,
    private val addArcSectionToMoralArgumentController: AddArcSectionToMoralArgumentController,
    private val changeCentralMoralQuestionController: ChangeCentralMoralQuestionController,
    private val changeThemeLineController: ChangeThemeLineController,
    private val changeSectionValueController: ChangeSectionValueController
) : MoralArgumentViewListener {

    private val themeId: UUID = UUID.fromString(themeId)

    override fun getValidState() {
        threadTransformer.async {
            getMoralProblemAndThemeLine.invoke(themeId, getMoralProblemAndThemeLineOutput)
        }
    }

    override fun outlineMoralArgument(characterId: String) {
        outlineMoralArgumentController.outlineMoralArgument(themeId.toString(), characterId)
    }

    override fun getAvailableArcSectionTypesToAdd(characterId: String) {
        val preparedCharacterId = UUID.fromString(characterId)
        threadTransformer.async {
            getAvailableArcSectionTypesForCharacterArc.invoke(
                themeId,
                preparedCharacterId,
                getAvailableArcSectionTypesForCharacterArcOutput
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
}