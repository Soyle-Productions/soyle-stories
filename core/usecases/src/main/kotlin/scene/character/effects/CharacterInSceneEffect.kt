package com.soyle.stories.usecase.scene.character.effects

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene

interface CharacterInSceneEffect {
    val scene: Scene.Id
    val sceneName: String
    val character: Character.Id
    val characterName: String?
}