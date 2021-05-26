package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene

class SceneRenamed(override val sceneId: Scene.Id, val sceneName: String) : SceneEvent() {
}