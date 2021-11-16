package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.entities.updates.Change
import com.soyle.stories.domain.scene.Scene

abstract class SceneEvent : Change<Scene.Id> {
    abstract val sceneId: Scene.Id
    override fun component1(): Scene.Id = sceneId
}