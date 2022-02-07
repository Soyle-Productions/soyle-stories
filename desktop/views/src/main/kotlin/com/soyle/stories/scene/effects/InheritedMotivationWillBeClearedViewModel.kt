package com.soyle.stories.scene.effects

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.usecase.scene.character.effects.InheritedCharacterMotivationInSceneCleared
import javafx.beans.binding.StringExpression
import tornadofx.objectProperty
import tornadofx.stringBinding

class InheritedMotivationWillBeClearedViewModel(
    initialState: InheritedCharacterMotivationInSceneCleared
) {

    private val _state = objectProperty(initialState)

    val sceneId: Scene.Id
        get() = _state.get().scene

    val characterName: StringExpression = _state.stringBinding { it?.characterName }

    val motivation: StringExpression = _state.stringBinding { it?.inheritedMotivation?.motivation }

    val sceneName: StringExpression = _state.stringBinding { it?.sceneName }

}