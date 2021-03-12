package com.soyle.stories.domain.scene

import com.soyle.stories.domain.scene.events.SceneEvent

sealed class SceneUpdate<out T> {
    abstract val scene: Scene
    operator fun component1() = scene
}

class WithoutChange(override val scene: Scene) : SceneUpdate<Nothing>()
class Updated<out T : SceneEvent>(override val scene: Scene, val event: T) : SceneUpdate<T>() {
    operator fun component2() = event
}