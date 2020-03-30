/**
 * Created by Brendan
 * Date: 3/3/2020
 * Time: 8:33 AM
 */
package com.soyle.stories.di.characterarc

import com.soyle.stories.characterarc.characterComparison.*
import com.soyle.stories.theme.usecases.compareCharacters.CompareCharacters
import tornadofx.Component
import tornadofx.ScopedInstance

class CharacterComparisonComponent : Component(), ScopedInstance {

    override val scope: CharacterComparisonScope = super.scope as CharacterComparisonScope
    private val characterArcComponent = tornadofx.find<CharacterArcComponent>(scope = scope.projectScope)

    private val characterComparisonPresenter by lazy {
        CharacterComparisonPresenter(
            find<CharacterComparisonModel>(),
            scope.themeId,
            characterArcComponent.eventBus
        )
    }

    private val compareCharactersOutputPort: CompareCharacters.OutputPort
        get() = characterComparisonPresenter

    val characterComparisonViewListener: CharacterComparisonViewListener by lazy {
        CharacterComparisonController(
            characterArcComponent.listAllCharacterArcs,
            characterComparisonPresenter,
            characterArcComponent.compareCharacters,
            compareCharactersOutputPort,
            characterArcComponent.includeCharacterInComparison,
            characterArcComponent.includeCharacterInComparisonOutputPort,
            characterArcComponent.promoteMinorCharacter,
            characterArcComponent.promoteMinorCharacterOutputPort,
            characterArcComponent.deleteLocalCharacterArc,
            characterArcComponent.deleteLocalCharacterArcOutputPort,
            characterArcComponent.changeThematicSectionValueController,
            characterArcComponent.changeStoryFunction,
            characterArcComponent.changeStoryFunctionOutputPort,
            characterArcComponent.changeCentralMoralQuestion,
            characterArcComponent.changeCentralMoralQuestionOutputPort,
            characterArcComponent.changeCharacterPropertyValue,
            characterArcComponent.changeCharacterPropertyValueOutputPort,
            characterArcComponent.changeCharacterPerspectivePropertyValue,
            characterArcComponent.changeCharacterPerspectivePropertyValueOutputPort,
            characterArcComponent.removeCharacterFromLocalComparison,
            characterArcComponent.removeCharacterFromLocalComparisonOutputPort
        )
    }
}