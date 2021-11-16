package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.order.exceptions.cannotAddSceneOutOfBounds
import com.soyle.stories.domain.scene.order.exceptions.sceneCannotBeAddedTwice
import com.soyle.stories.domain.storyevent.StoryEvent
import org.junit.jupiter.api.Test

class `Scene Order Unit Test` {

    @Test
    fun `adding scene should add it to the end of the list`() {
        val projectId = Project.Id()
        val order = SceneOrder(projectId, setOf())

        val sceneId = Scene.Id()
        val sceneCreated = SceneCreated(sceneId, "Some name", Prose.Id(), StoryEvent.Id())
        val update = order.withScene(sceneCreated)

        update as SuccessfulSceneOrderUpdate
        update.sceneOrder.order.size.mustEqual(1)
        update.sceneOrder.order.single().mustEqual(sceneId)
        update.sceneOrder.projectId.mustEqual(projectId)
        update.change.mustEqual(sceneCreated)
    }

    @Test
    fun `cannot add the same scene twice`() {
        val projectId = Project.Id()
        val sceneId = Scene.Id()
        val order = SceneOrder(projectId, setOf(sceneId))

        val update = order.withScene(SceneCreated(sceneId, "Some name", Prose.Id(), StoryEvent.Id()))

        update as UnSuccessfulSceneOrderUpdate
        update.sceneOrder.order.size.mustEqual(1)
        update.sceneOrder.order.single().mustEqual(sceneId)
        update.sceneOrder.projectId.mustEqual(projectId)

        update.reason.mustEqual(sceneCannotBeAddedTwice(sceneId))
    }

    @Test
    fun `should add new scenes to end of scene order`() {
        val projectId = Project.Id()
        val order = SceneOrder(projectId, List(5) { Scene.Id() }.toSet())

        val sceneId = Scene.Id()
        val sceneCreated = SceneCreated(sceneId, "Some name", Prose.Id(), StoryEvent.Id())
        val update = order.withScene(sceneCreated)

        update as SuccessfulSceneOrderUpdate
        update.sceneOrder.order.size.mustEqual(6)
        update.sceneOrder.order.last().mustEqual(sceneId)
        update.sceneOrder.projectId.mustEqual(projectId)
        update.change.mustEqual(sceneCreated)
    }

    @Test
    fun `should add scene at specified index`() {
        val projectId = Project.Id()
        val order = SceneOrder(projectId, List(5) { Scene.Id() }.toSet())

        val sceneId = Scene.Id()
        val sceneCreated = SceneCreated(sceneId, "Some name", Prose.Id(), StoryEvent.Id())
        val update = order.withScene(sceneCreated, at = 3)

        update as SuccessfulSceneOrderUpdate
        update.sceneOrder.order.size.mustEqual(6)
        update.sceneOrder.order.toList()[3].mustEqual(sceneId)
        update.change.mustEqual(sceneCreated)
    }

    @Test
    fun `cannot add scene at index less than negative one`() {
        val projectId = Project.Id()
        val order = SceneOrder(projectId, List(5) { Scene.Id() }.toSet())

        val sceneId = Scene.Id()
        val update = order.withScene(SceneCreated(sceneId, "Some name", Prose.Id(), StoryEvent.Id()), at = -2)

        update as UnSuccessfulSceneOrderUpdate
        update.sceneOrder.order.size.mustEqual(5)

        update.reason.mustEqual(cannotAddSceneOutOfBounds(sceneId, -2))
    }

    @Test
    fun `cannot add scene at index greater than size of current order`() {
        val projectId = Project.Id()
        val order = SceneOrder(projectId, List(5) { Scene.Id() }.toSet())

        val sceneId = Scene.Id()
        val update = order.withScene(SceneCreated(sceneId, "Some name", Prose.Id(), StoryEvent.Id()), at = 6)

        update as UnSuccessfulSceneOrderUpdate
        update.sceneOrder.order.size.mustEqual(5)

        update.reason.mustEqual(cannotAddSceneOutOfBounds(sceneId, 6))
    }

}