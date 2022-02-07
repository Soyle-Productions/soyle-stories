package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.sceneName
import com.soyle.stories.domain.storyevent.StoryEvent
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Scene Order Service Unit Test` {

    private val service = SceneOrderService()
    private val projectId = Project.Id()
    private val storyEventId = StoryEvent.Id()
    private val proseId = Prose.Id()
    private val inputName = sceneName()

    @Nested
    inner class `Given No Scenes Exist Yet` {

        val sceneOrder = SceneOrder.initializeInProject(projectId)

        @Test
        fun `should create new scene in project`() {
            val update = runBlocking { service.createScene(sceneOrder, inputName, proseId) }

            update as SuccessfulSceneOrderUpdate
            val sceneUpdate = update.change
            sceneUpdate.change.name.mustEqual(inputName.value)
            sceneUpdate.change.proseId.mustEqual(proseId)
        }

        @Test
        fun `first scene should be at the first index`() {
            val update = runBlocking { service.createScene(sceneOrder, inputName, proseId) }

            update as SuccessfulSceneOrderUpdate
            val sceneUpdate = update.change
            update.sceneOrder.order.size.mustEqual(1)
            update.sceneOrder.order.single().mustEqual(sceneUpdate.scene.id)
        }

    }

    @Nested
    inner class `Given Scenes Exist Already` {

        val sceneOrder = SceneOrder.reInstantiate(projectId, List(4) { Scene.Id() })

        @Test
        fun `scene should be added to the end of the project`() {
            val update = runBlocking { service.createScene(sceneOrder, inputName, proseId) }

            update as SuccessfulSceneOrderUpdate
            update.sceneOrder.order.size.mustEqual(5)
            update.sceneOrder.order.last().mustEqual(update.change.scene.id)
        }

        @Test
        fun `new scene should be at provided index`() {
            val update = runBlocking { service.createScene(sceneOrder, inputName, proseId, 2) }

            update as SuccessfulSceneOrderUpdate
            update.sceneOrder.order.size.mustEqual(5)
            update.sceneOrder.order.toList()[2].mustEqual(update.change.scene.id)
        }

        @Test
        fun `if scene insertion fails, should not create new scene`() {
            val update = runBlocking { service.createScene(sceneOrder, inputName, proseId, -4) }

            update as UnSuccessfulSceneOrderUpdate
        }

    }

}