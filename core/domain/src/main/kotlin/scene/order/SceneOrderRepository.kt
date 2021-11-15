package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.project.Project

/** A read-only repository to access scene orders for a project */
interface SceneOrderRepository {
    suspend fun getSceneOrderForProject(projectId: Project.Id): SceneOrder?
}