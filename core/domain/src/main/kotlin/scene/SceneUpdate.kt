package com.soyle.stories.domain.scene

import com.soyle.stories.domain.scene.events.CompoundEvent
import com.soyle.stories.domain.scene.events.SceneEvent

sealed class SceneUpdate<out T> {
    abstract val scene: Scene
    operator fun component1() = scene
}

class WithoutChange(override val scene: Scene, val reason: Any? = null) : SceneUpdate<Nothing>()
class Updated<out T : SceneEvent>(override val scene: Scene, val event: T) : SceneUpdate<T>() {
    operator fun component2() = event
}

internal fun <E : SceneEvent> SceneUpdate<CompoundEvent<E>>.then(op: Scene.() -> SceneUpdate<CompoundEvent<E>>): SceneUpdate<CompoundEvent<E>>
{
    val nextUpdate = scene.op()
    when {
        this is Updated && nextUpdate is Updated -> {
            return Updated(nextUpdate.scene, CompoundEvent(event.events + nextUpdate.event.events))
        }
        nextUpdate !is Updated -> {
            return this
        }
        else -> {
            return nextUpdate
        }
    }
}