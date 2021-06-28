package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene

data class CharacterDesireInSceneChanged(override val sceneId: Scene.Id, val characterId: Character.Id, val newDesire: String) : SceneEvent()