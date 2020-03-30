/**
 * Created by Brendan
 * Date: 3/3/2020
 * Time: 8:35 AM
 */
package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.characterarc.usecaseControllers.ChangeThematicSectionValueController
import com.soyle.stories.characterarc.usecases.deleteLocalCharacterArc.DeleteLocalCharacterArc
import com.soyle.stories.characterarc.usecases.listAllCharacterArcs.ListAllCharacterArcs
import com.soyle.stories.theme.usecases.changeCentralMoralQuestion.ChangeCentralMoralQuestion
import com.soyle.stories.theme.usecases.changeCharacterPerspectivePropertyValue.ChangeCharacterPerspectivePropertyValue
import com.soyle.stories.theme.usecases.changeCharacterPropertyValue.ChangeCharacterPropertyValue
import com.soyle.stories.theme.usecases.changeStoryFunction.ChangeStoryFunction
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharacters
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter
import com.soyle.stories.theme.usecases.removeCharacterFromComparison.RemoveCharacterFromLocalComparison
import java.util.*

class CharacterComparisonController(
    private val listAllCharacterArcs: ListAllCharacterArcs,
    private val listAllCharacterArcsOutputPort: ListAllCharacterArcs.OutputPort,
    private val compareCharacters: CompareCharacters,
    private val compareCharactersOutputPort: CompareCharacters.OutputPort,
    private val includeCharacterInComparison: IncludeCharacterInComparison,
    private val includeCharacterInComparisonOutputPort: IncludeCharacterInComparison.OutputPort,
    private val promoteMinorCharacter: PromoteMinorCharacter,
    private val promoteMinorCharacterOutputPort: PromoteMinorCharacter.OutputPort,
    private val demoteMajorCharacter: DeleteLocalCharacterArc,
    private val demoteMajorCharacterOutputPort: DeleteLocalCharacterArc.OutputPort,
    private val changeThematicSectionValueController: ChangeThematicSectionValueController,
    private val changeStoryFunction: ChangeStoryFunction,
    private val changeStoryFunctionOutputPort: ChangeStoryFunction.OutputPort,
    private val changeCentralMoralQuestion: ChangeCentralMoralQuestion,
    private val changeCentralMoralQuestionOutputPort: ChangeCentralMoralQuestion.OutputPort,
    private val changeCharacterPropertyValue: ChangeCharacterPropertyValue,
    private val changeCharacterPropertyValueOutputPort: ChangeCharacterPropertyValue.OutputPort,
    private val changeCharacterPerspectivePropertyValue: ChangeCharacterPerspectivePropertyValue,
    private val changeCharacterPerspectivePropertyValueOutputPort: ChangeCharacterPerspectivePropertyValue.OutputPort,
    private val removeCharacterFromLocalComparison: RemoveCharacterFromLocalComparison,
    private val removeCharacterFromLocalComparisonOutputPort: RemoveCharacterFromLocalComparison.OutputPort
) : CharacterComparisonViewListener {

    override suspend fun getCharacterComparison(themeId: String, characterId: String) {
        listAllCharacterArcs.invoke(listAllCharacterArcsOutputPort)
        compareCharacters.invoke(
            UUID.fromString(themeId),
            UUID.fromString(characterId),
            compareCharactersOutputPort
        )
    }

    override suspend fun addCharacterToComparison(themeId: String, characterId: String) {
        includeCharacterInComparison.invoke(
            UUID.fromString(characterId),
            UUID.fromString(themeId),
            includeCharacterInComparisonOutputPort
        )
    }

    override suspend fun promoteCharacter(themeId: String, characterId: String) {
        PromoteMinorCharacter.RequestModel(
            UUID.fromString(themeId),
            UUID.fromString(characterId)
        )
            .let { promoteMinorCharacter.invoke(it, promoteMinorCharacterOutputPort) }
    }

    override suspend fun demoteCharacter(themeId: String, characterId: String) {
        demoteMajorCharacter.invoke(
            UUID.fromString(themeId),
            UUID.fromString(characterId),
            demoteMajorCharacterOutputPort
        )
    }

    override suspend fun updateValue(sectionId: String, value: String) {
        changeThematicSectionValueController.changeThematicSectionValue(sectionId, value)
    }

    override suspend fun setStoryFunction(
        themeId: String,
        characterId: String,
        targetCharacterId: String,
        storyFunction: String
    ) {
        changeStoryFunction.invoke(
            ChangeStoryFunction.RequestModel(
                UUID.fromString(themeId),
                UUID.fromString(characterId),
                UUID.fromString(targetCharacterId),
                ChangeStoryFunction.StoryFunction.valueOf(storyFunction)
            ),
            changeStoryFunctionOutputPort
        )
    }

    override suspend fun updateCentralMoralQuestion(themeId: String, question: String) {
        changeCentralMoralQuestion.invoke(
            UUID.fromString(themeId),
            question,
            changeCentralMoralQuestionOutputPort
        )
    }

    override suspend fun changeCharacterPropertyValue(
        themeId: String,
        characterId: String,
        property: String,
        value: String
    ) {
        changeCharacterPropertyValue.invoke(
            ChangeCharacterPropertyValue.RequestModel(
                UUID.fromString(themeId),
                UUID.fromString(characterId),
                ChangeCharacterPropertyValue.Property.valueOf(property),
                value
            ),
            changeCharacterPropertyValueOutputPort
        )
    }

    override suspend fun changeSharedPropertyValue(
        themeId: String,
        perspectiveCharacterId: String,
        targetCharacterId: String,
        property: String,
        value: String
    ) {
        changeCharacterPerspectivePropertyValue.invoke(
            ChangeCharacterPerspectivePropertyValue.RequestModel(
                UUID.fromString(themeId),
                UUID.fromString(perspectiveCharacterId),
                UUID.fromString(targetCharacterId),
                ChangeCharacterPerspectivePropertyValue.Property.valueOf(property),
                value
            ),
            changeCharacterPerspectivePropertyValueOutputPort
        )
    }

    override suspend fun removeCharacterFromComparison(themeId: String, characterId: String) {
        removeCharacterFromLocalComparison.invoke(
            UUID.fromString(themeId),
            UUID.fromString(characterId),
            removeCharacterFromLocalComparisonOutputPort
        )
    }
}