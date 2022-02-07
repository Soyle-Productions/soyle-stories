package com.soyle.stories.scene.effects

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.effects.CharacterGainedInheritedMotivationInScene
import javafx.beans.binding.StringExpression
import tornadofx.objectProperty
import tornadofx.stringBinding

class CharacterWillGainInheritedMotivationViewModel(
    initialState: CharacterGainedInheritedMotivationInScene
) {

    private val _state = objectProperty(initialState)

    val sceneId: Scene.Id
        get() = _state.get().scene

    val characterName: StringExpression = _state.stringBinding { it?.characterName }

    val newMotivation: StringExpression = _state.stringBinding { it?.inheritedMotivation?.motivation }
    val newSourceSceneName: StringExpression = _state.stringBinding { it?.inheritedMotivation?.sceneName }
    val newSourceId: Scene.Id
        get() = _state.get().inheritedMotivation.sceneId

    val previousMotivation = _state.stringBinding { it?.previousInheritance?.motivation }
    val sceneName: StringExpression = _state.stringBinding { it?.sceneName }


}