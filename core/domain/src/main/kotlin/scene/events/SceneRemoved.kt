package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene

data class SceneRemoved(override val sceneId: Scene.Id, val newOrder: List<Scene.Id>) : SceneEvent()