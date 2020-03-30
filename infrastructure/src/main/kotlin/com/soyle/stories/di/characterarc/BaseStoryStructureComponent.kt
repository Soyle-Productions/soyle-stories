/**
 * Created by Brendan
 * Date: 3/2/2020
 * Time: 5:56 PM
 */
package com.soyle.stories.di.characterarc

import com.soyle.stories.characterarc.baseStoryStructure.*
import com.soyle.stories.common.ThreadTransformerImpl
import tornadofx.Component
import tornadofx.ScopedInstance
import tornadofx.find

class BaseStoryStructureComponent : Component(), ScopedInstance {

    override val scope: BaseStoryStructureScope = super.scope as BaseStoryStructureScope
    private val characterArcComponent = find<CharacterArcComponent>(scope = scope.projectScope)

    val viewBaseStoryStructureController = ViewBaseStoryStructureController(
        ThreadTransformerImpl,
        characterArcComponent.viewBaseStoryStructure,
        BaseStoryStructurePresenter(
            find<BaseStoryStructureModel>(),
            characterArcComponent.eventBus
        )
    )

    val baseStoryStructureViewListener: BaseStoryStructureViewListener by lazy {
        BaseStoryStructureController(
            scope.themeId,
            scope.characterId,
            viewBaseStoryStructureController,
            characterArcComponent.changeThematicSectionValueController
        )
    }

}