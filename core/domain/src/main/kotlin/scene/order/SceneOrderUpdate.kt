package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.entities.updates.SuccessfulUpdate
import com.soyle.stories.domain.entities.updates.Update

sealed class SceneOrderUpdate<out E> : Update<SceneOrder> {
    abstract val sceneOrder: SceneOrder
    override fun component1(): SceneOrder = sceneOrder

    class Successful<out E>(override val sceneOrder: SceneOrder, override val change: E) : SceneOrderUpdate<E>(), SuccessfulUpdate<SceneOrder, E> {
        override fun component2(): E = change
    }
    class UnSuccessful(override val sceneOrder: SceneOrder, val reason: Throwable? = null) : SceneOrderUpdate<Nothing>()
}

typealias SuccessfulSceneOrderUpdate<T> = SceneOrderUpdate.Successful<T>
typealias UnSuccessfulSceneOrderUpdate = SceneOrderUpdate.UnSuccessful