package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.Updated
import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.sceneName
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Scene Order Service Unit Test` {

    private val sceneOrderRepository = object : SceneOrderRepository {
        private val sceneOrders = mutableMapOf<Project.Id, SceneOrder>()
        fun givenSceneOrder(sceneOrder: SceneOrder) {
            sceneOrders[sceneOrder.projectId] = sceneOrder
        }

        override suspend fun getSceneOrderForProject(projectId: Project.Id): SceneOrder? = sceneOrders[projectId]
    }

    private val service = SceneOrderService(sceneOrderRepository)
    private val projectId = Project.Id()
    private val proseId = Prose.Id()
    private val inputName = sceneName()

    @Nested
    inner class `Given No Scenes Exist Yet` {

        @Test
        fun `should create new scene in project`() {
            val (_, update) = runBlocking { service.createScene(projectId, inputName, proseId) }

            update as Updated<*>
            val event = update.event as SceneCreated
            event.name.mustEqual(inputName.value)
            event.proseId.mustEqual(proseId)
            event.storyEventId.mustEqual(update.scene.storyEventId)
        }

        @Test
        fun `first scene should be at the first index`() {
            val (update, sceneUpdate) = runBlocking { service.createScene(projectId, inputName, proseId) }

            update as SuccessfulSceneOrderUpdate
            update.sceneOrder.order.size.mustEqual(1)
            update.sceneOrder.projectId.mustEqual(projectId)
            update.sceneOrder.order.single().mustEqual(sceneUpdate.scene.id)
        }

    }

    @Nested
    inner class `Given Scenes Exist Already` {

        init {
            sceneOrderRepository.givenSceneOrder(SceneOrder(projectId, List(4) { Scene.Id() }.toSet()))
        }

        @Test
        fun `scene should be added to the end of the project`() {
            val (update, sceneUpdate) = runBlocking { service.createScene(projectId, inputName, proseId) }

            update as SuccessfulSceneOrderUpdate
            update.sceneOrder.order.size.mustEqual(5)
            update.sceneOrder.order.last().mustEqual(sceneUpdate.scene.id)
        }

    }

}