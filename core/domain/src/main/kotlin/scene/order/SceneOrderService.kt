package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.entities.updates.Update
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneUpdate
import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.storyevent.StoryEventRepository
import com.soyle.stories.domain.storyevent.StoryEventTimeService
import com.soyle.stories.domain.validation.NonBlankString

class SceneOrderService(
    private val sceneOrderRepository: SceneOrderRepository
) {

    suspend fun createScene(
        projectId: Project.Id,
        name: NonBlankString,
        proseId: Prose.Id
    ): Pair<SceneOrderUpdate, SceneUpdate<SceneCreated>> {
        val sceneOrder = sceneOrderRepository.getSceneOrderForProject(projectId) ?: SceneOrder(projectId, setOf())
        val sceneUpdate = Scene.create(projectId, name, proseId)
        return sceneOrder.withScene(sceneUpdate.scene.id) to sceneUpdate
    }

}