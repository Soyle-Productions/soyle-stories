package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene

data class IncludedCharacterInScene(override val sceneId: Scene.Id, val characterInScene: Scene.IncludedCharacter) : SceneEvent()