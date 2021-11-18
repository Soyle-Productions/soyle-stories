package com.soyle.stories.domain.scene.order

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.events.SceneRemoved
import com.soyle.stories.domain.scene.order.exceptions.cannotAddSceneOutOfBounds
import com.soyle.stories.domain.scene.order.exceptions.sceneAlreadyAtIndex
import com.soyle.stories.domain.scene.order.exceptions.sceneCannotBeAddedTwice
import com.soyle.stories.domain.storyevent.StoryEvent
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import kotlin.IndexOutOfBoundsException

class `Scene Order Unit Test` {

    @Nested
    inner class `Can add a newly created scene` {

        val projectId = Project.Id()
        val sceneId = Scene.Id()

        @Test
        fun `adding scene should add it to the end of the list`() {
            val order = SceneOrder.initializeInProject(projectId)

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
            val order = SceneOrder.reInstantiate(projectId, listOf(sceneId))

            val update = order.withScene(SceneCreated(sceneId, "Some name", Prose.Id(), StoryEvent.Id()))

            update as UnSuccessfulSceneOrderUpdate
            update.sceneOrder.order.size.mustEqual(1)
            update.sceneOrder.order.single().mustEqual(sceneId)
            update.sceneOrder.projectId.mustEqual(projectId)

            update.reason.mustEqual(sceneCannotBeAddedTwice(sceneId))
        }

        @Test
        fun `should add new scenes to end of scene order`() {
            val order = SceneOrder.reInstantiate(projectId, List(5) { Scene.Id() })

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
            val order = SceneOrder.reInstantiate(projectId, List(5) { Scene.Id() })

            val sceneCreated = SceneCreated(sceneId, "Some name", Prose.Id(), StoryEvent.Id())
            val update = order.withScene(sceneCreated, at = 3)

            update as SuccessfulSceneOrderUpdate
            update.sceneOrder.order.size.mustEqual(6)
            update.sceneOrder.order.toList()[3].mustEqual(sceneId)
            update.change.mustEqual(sceneCreated)
        }

        @Test
        fun `cannot add scene at index less than negative one`() {
            val order = SceneOrder.reInstantiate(projectId, List(5) { Scene.Id() })

            val update = order.withScene(SceneCreated(sceneId, "Some name", Prose.Id(), StoryEvent.Id()), at = -2)

            update as UnSuccessfulSceneOrderUpdate
            update.sceneOrder.order.size.mustEqual(5)

            update.reason.mustEqual(cannotAddSceneOutOfBounds(sceneId, -2))
        }

        @Test
        fun `cannot add scene at index greater than size of current order`() {
            val order = SceneOrder.reInstantiate(projectId, List(5) { Scene.Id() })

            val update = order.withScene(SceneCreated(sceneId, "Some name", Prose.Id(), StoryEvent.Id()), at = 6)

            update as UnSuccessfulSceneOrderUpdate
            update.sceneOrder.order.size.mustEqual(5)

            update.reason.mustEqual(cannotAddSceneOutOfBounds(sceneId, 6))
        }

    }

    @Nested
    inner class `Can Move Scene to Different Position` {

        @Test
        fun `when scene is not in scene order, should not return modification interface`() {
            val modifications = SceneOrder.initializeInProject(Project.Id()).withScene(Scene.Id())

            assertNull(modifications)
        }

        @Nested
        inner class `Given Scene is in Scene Order` {

            val sceneId = Scene.Id()

            @Test
            fun `when no other scenes exist, no where for it to move`() {
                val sceneOrder = SceneOrder.reInstantiate(Project.Id(), listOf(sceneId))
                val modifications = sceneOrder.withScene(sceneId)!!
                val update = modifications.movedTo(1)

                update as UnSuccessfulSceneOrderUpdate
                update.sceneOrder === sceneOrder
                update.reason as IndexOutOfBoundsException
            }

            @Nested
            inner class `Given Other Scenes Exist` {

                val sceneOrder = SceneOrder.reInstantiate(Project.Id(),
                    listOf(Scene.Id(), Scene.Id()) + sceneId +
                            listOf(Scene.Id(), Scene.Id())
                )

                @Test
                fun `if current index is used, no update should happen`() {
                    val update = sceneOrder.withScene(sceneId)!!.movedTo(2)

                    update as UnSuccessfulSceneOrderUpdate
                    update.reason.mustEqual(sceneAlreadyAtIndex(sceneId, 2))
                }

                @ParameterizedTest
                @ValueSource(ints = [-1, 5])
                fun `index out of bounds should not produce update`(index: Int) {
                    val update = sceneOrder.withScene(sceneId)!!.movedTo(index)

                    update as UnSuccessfulSceneOrderUpdate
                    update.reason as IndexOutOfBoundsException
                }

                @Test
                fun `moving back should offset events before`() {
                    val update = sceneOrder.withScene(sceneId)!!.movedTo(0)

                    update as SuccessfulSceneOrderUpdate
                    update.sceneOrder.order.mustEqual(
                        sceneOrder.order.toList().let {
                            setOf(
                                sceneId,
                                it[0],
                                it[1],
                                it[3],
                                it[4]
                            )
                        }
                    )
                }

                @Test
                fun `moving forward should offset events after`() {
                    val update = sceneOrder.withScene(sceneId)!!.movedTo(4)

                    update as SuccessfulSceneOrderUpdate
                    update.sceneOrder.order.mustEqual(
                        sceneOrder.order.toList().let {
                            setOf(
                                it[0],
                                it[1],
                                it[3],
                                it[4],
                                sceneId,
                            )
                        }
                    )
                }

            }

        }

    }

    @Nested
    inner class `Can Remove Scene` {

        val sceneId = Scene.Id()
        val sceneOrder = SceneOrder
                .initializeInProject(Project.Id())
                .withScene(SceneCreated(sceneId, "", Prose.Id(), StoryEvent.Id())).sceneOrder

        @Test
        fun `scene should no longer be in scene order`() {
            val update = sceneOrder.withScene(sceneId)!!.removed()

            update.sceneOrder.order.contains(sceneId).mustEqual(false)
        }

        @Test
        fun `should produce scene removed event`() {
            val update = sceneOrder.withScene(sceneId)!!.removed()

            update as SuccessfulSceneOrderUpdate
            update.change.sceneId.mustEqual(sceneId)
            update.change.newOrder.mustEqual(update.sceneOrder.order.toList())
        }

        @Nested
        inner class `Given Other Scenes Exist` {

            val otherScenes = List(5) { Scene.Id() }
            val sceneOrder = SceneOrder
                .reInstantiate(Project.Id(), otherScenes + sceneId)

            @Test
            fun `should still contain other scenes`() {
                val update = sceneOrder.withScene(sceneId)!!.removed()

                update as SuccessfulSceneOrderUpdate
                otherScenes.forEach {
                    update.sceneOrder.order.contains(it).mustEqual(true)
                    update.change.newOrder.contains(it).mustEqual(true)
                }
            }

        }

    }

}