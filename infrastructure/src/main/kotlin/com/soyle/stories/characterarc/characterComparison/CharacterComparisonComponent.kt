/**
 * Created by Brendan
 * Date: 3/3/2020
 * Time: 8:33 AM
 */
package com.soyle.stories.characterarc.characterComparison

import com.soyle.stories.characterarc.usecaseControllers.*
import com.soyle.stories.di.characterarc.CharacterArcComponent
import tornadofx.Component
import tornadofx.ScopedInstance

class CharacterComparisonComponent : Component(), ScopedInstance {

    override val scope: CharacterComparisonScope = super.scope as CharacterComparisonScope
    private val characterArcComponent = tornadofx.find<CharacterArcComponent>(scope = scope.projectScope)

    private val characterComparisonPresenter by lazy {
        CharacterComparisonPresenter(
          find<CharacterComparisonModel>(),
          scope.themeId,
          characterArcComponent.characterArcEvents
        )
    }

    val characterComparisonViewListener: CharacterComparisonViewListener by lazy {
        CharacterComparisonViewListenerImpl(
          CharacterComparisonController(
            scope.themeId,
            characterArcComponent.listAllCharacterArcs,
            characterComparisonPresenter,
            characterArcComponent.compareCharacters,
            characterComparisonPresenter
          ),
          characterArcComponent.changeThematicSectionValueController,
          characterArcComponent.includeCharacterInComparisonController(scope.themeId),
          characterArcComponent.promoteMinorCharacterController(scope.themeId),
          characterArcComponent.deleteLocalCharacterArcController(scope.themeId),
          characterArcComponent.changeStoryFunctionController(scope.themeId),
          characterArcComponent.changeCentralMoralQuestionController(scope.themeId),
          characterArcComponent.changeCharacterPropertyController(scope.themeId),
          characterArcComponent.changeCharacterPerspectivePropertyController(scope.themeId),
          characterArcComponent.removeCharacterFromLocalComparisonController(scope.themeId)
        )
    }
}