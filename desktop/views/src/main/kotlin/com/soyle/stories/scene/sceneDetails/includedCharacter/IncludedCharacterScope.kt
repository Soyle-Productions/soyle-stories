package com.soyle.stories.scene.sceneDetails.includedCharacter

import com.soyle.stories.common.SubProjectScope
import com.soyle.stories.common.ToolScope
import com.soyle.stories.di.get
import com.soyle.stories.project.ProjectScope
import com.soyle.stories.scene.sceneDetails.SceneDetailsScope
import com.soyle.stories.scene.sceneDetails.includedCharacters.IncludedCharactersInSceneState
import javafx.beans.value.ObservableValue
import tornadofx.Scope

class IncludedCharacterScope(
    val sceneId: String,
    val storyEventId: String,
    initialState: IncludedCharacterInSceneViewModel,
    val sceneDetailsScope: SceneDetailsScope
) : SubProjectScope(sceneDetailsScope.projectScope) {

    val characterId = initialState.characterId

    val includedCharactersInSceneState: IncludedCharactersInSceneState
        get() = sceneDetailsScope.get()

    private var _initialState: IncludedCharacterInSceneViewModel? = initialState
    fun consumeInitialState(): IncludedCharacterInSceneViewModel?
    {
        return _initialState?.also {
            _initialState = null
        }
    }

}