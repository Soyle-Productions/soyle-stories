package com.soyle.stories.domain.scene.events

import com.soyle.stories.domain.scene.Scene

class CompoundEvent<E: SceneEvent>(val events: List<E>) : SceneEvent()
{
    override val sceneId: Scene.Id
        get() = events.first().sceneId
}