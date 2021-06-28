package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneFrameValue

data class SceneFrameValueChanged(override val sceneId: Scene.Id, val newValue: SceneFrameValue) : SceneEvent()