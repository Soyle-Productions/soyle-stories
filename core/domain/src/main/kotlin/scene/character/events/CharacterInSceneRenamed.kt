package com.soyle.stories.domain.scene.character.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene

data class CharacterInSceneRenamed(
    override val sceneId: Scene.Id,
    override val characterId: Character.Id,
    val newName: String
) : CharacterInSceneEvent()