package com.soyle.stories.domain.scene

import com.soyle.stories.domain.entities.updates.Update
import com.soyle.stories.domain.entities.updates.SuccessfulUpdate
import com.soyle.stories.domain.entities.updates.UnSuccessfulUpdate
import com.soyle.stories.domain.scene.events.CompoundEvent
import com.soyle.stories.domain.scene.events.SceneEvent

sealed class SceneUpdate<out T : SceneEvent> : Update<Scene> {
    abstract val scene: Scene
    override operator fun component1() = scene

    class UnSuccessful(override val scene: Scene, override val reason: Throwable? = null) : SceneUpdate<Nothing>(),
        UnSuccessfulUpdate<Scene>

    class Successful<out T : SceneEvent>(override val scene: Scene, val event: T) : SceneUpdate<T>(),
        SuccessfulUpdate<Scene, T> {
        override val change: T
            get() = event

        override fun component2(): T = change
    }

}

typealias UnSuccessfulSceneUpdate = SceneUpdate.UnSuccessful

typealias SuccessfulSceneUpdate<T> = SceneUpdate.Successful<T>

internal fun <E : SceneEvent> SceneUpdate<CompoundEvent<E>>.then(op: Scene.() -> SceneUpdate<CompoundEvent<E>>): SceneUpdate<CompoundEvent<E>> {
    val nextUpdate = scene.op()
    when {
        this is SceneUpdate.Successful && nextUpdate is SceneUpdate.Successful -> {
            return SceneUpdate.Successful(nextUpdate.scene, CompoundEvent(event.events + nextUpdate.event.events))
        }
        nextUpdate !is SceneUpdate.Successful -> {
            return this
        }
        else -> {
            return nextUpdate
        }
    }
}