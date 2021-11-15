package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.entities.updates.SuccessfulUpdate
import com.soyle.stories.domain.entities.updates.Update

sealed class SceneOrderUpdate : Update<SceneOrder> {
    abstract val sceneOrder: SceneOrder
    override fun component1(): SceneOrder = sceneOrder

    class Successful(override val sceneOrder: SceneOrder) : SceneOrderUpdate(), SuccessfulUpdate<SceneOrder, SceneOrder> {
        override val change: SceneOrder = sceneOrder
        override fun component2(): SceneOrder = change
    }
    class UnSuccessful(override val sceneOrder: SceneOrder, val reason: Throwable? = null) : SceneOrderUpdate()
}

typealias SuccessfulSceneOrderUpdate = SceneOrderUpdate.Successful
typealias UnSuccessfulSceneOrderUpdate = SceneOrderUpdate.UnSuccessful