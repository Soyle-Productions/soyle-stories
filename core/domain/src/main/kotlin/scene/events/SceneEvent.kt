package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene

abstract class SceneEvent {
    abstract val sceneId: Scene.Id
}