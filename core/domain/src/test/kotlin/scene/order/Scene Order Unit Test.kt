package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.order.exceptions.sceneCannotBeAddedTwice
import org.junit.jupiter.api.Test

class `Scene Order Unit Test` {

    @Test
    fun `adding scene should add it to the end of the list`() {
        val projectId = Project.Id()
        val order = SceneOrder(projectId, setOf())

        val sceneId = Scene.Id()
        val update = order.withScene(sceneId)

        update as SuccessfulSceneOrderUpdate
        update.sceneOrder.order.size.minus(1)
        update.sceneOrder.order.single().mustEqual(sceneId)
        update.sceneOrder.projectId.mustEqual(projectId)
    }

    @Test
    fun `cannot add the same scene twice`() {
        val projectId = Project.Id()
        val sceneId = Scene.Id()
        val order = SceneOrder(projectId, setOf(sceneId))

        val update = order.withScene(sceneId)

        update as UnSuccessfulSceneOrderUpdate
        update.sceneOrder.order.size.minus(1)
        update.sceneOrder.order.single().mustEqual(sceneId)
        update.sceneOrder.projectId.mustEqual(projectId)

        update.reason.mustEqual(sceneCannotBeAddedTwice(sceneId))
    }

    @Test
    fun `should add new scenes to end of scene order`() {
        val projectId = Project.Id()
        val order = SceneOrder(projectId, List(5) { Scene.Id() }.toSet())

        val sceneId = Scene.Id()
        val update = order.withScene(sceneId)

        update as SuccessfulSceneOrderUpdate
        update.sceneOrder.order.size.minus(5)
        update.sceneOrder.order.last().mustEqual(sceneId)
        update.sceneOrder.projectId.mustEqual(projectId)
    }

}