package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.order.exceptions.sceneCannotBeAddedTwice

class SceneOrder(
    val projectId: Project.Id,
    val order: Set<Scene.Id>
) {

    fun withScene(sceneId: Scene.Id): SceneOrderUpdate {
        if (sceneId in order) return noUpdate(sceneCannotBeAddedTwice(sceneId))
        return SuccessfulSceneOrderUpdate(SceneOrder(projectId, order + sceneId))
    }

    private fun noUpdate(reason: Throwable) = UnSuccessfulSceneOrderUpdate(this, reason)

}