package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.order.exceptions.cannotAddSceneOutOfBounds
import com.soyle.stories.domain.scene.order.exceptions.sceneCannotBeAddedTwice

class SceneOrder(
    val projectId: Project.Id,
    val order: Set<Scene.Id>
) {

    fun withScene(sceneId: Scene.Id, at: Int = -1): SceneOrderUpdate {
        if (sceneId in order) return noUpdate(sceneCannotBeAddedTwice(sceneId))
        if (at !in -1 .. order.size) return noUpdate(cannotAddSceneOutOfBounds(sceneId, at))
        if (at < 0) {
            return SuccessfulSceneOrderUpdate(SceneOrder(projectId, order + sceneId))
        } else {
            val indexedOrder = order.toList()
            val newOrder = indexedOrder.subList(0, at) + sceneId + indexedOrder.subList(at, indexedOrder.size)
            return SuccessfulSceneOrderUpdate(SceneOrder(projectId, newOrder.toSet()))
        }
    }

    private fun noUpdate(reason: Throwable) = UnSuccessfulSceneOrderUpdate(this, reason)

}