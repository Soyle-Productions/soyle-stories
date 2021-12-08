package com.soyle.stories.domain.scene.character.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene

data class IncludedCharacterInScene(
    override val sceneId: Scene.Id,
    val characterInScene: Scene.IncludedCharacter
) : CharacterInSceneEvent() {
    override val characterId: Character.Id
        get() = characterInScene.characterId
}