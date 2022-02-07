package com.soyle.stories.domain.scene.character.events

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SceneEvent

data class CharacterDesireInSceneChanged(override val sceneId: Scene.Id, val characterId: Character.Id, val newDesire: String) : SceneEvent()