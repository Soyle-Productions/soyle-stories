package com.soyle.stories.usecase.scene.storyevent

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredByScene
import com.soyle.stories.usecase.scene.storyevent.list.ListStoryEventsCoveredBySceneUseCase
import com.soyle.stories.usecase.scene.storyevent.list.StoryEventsInScene
import com.soyle.stories.usecase.storyevent.StoryEventItem
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `List Story Events Covered by Scene Unit Test` {

    // Summary
    /** Reports the story events that are covered by the requested scene */

    // Preconditions
    /** A project has been created and opened */
    private val projectId = Project.Id()
    /** The scene exists */
    private val scene = makeScene(projectId = projectId)

    // post conditions
    /** outputs a list of story event items */
    private var storyEventItems: StoryEventsInScene? = null
    private val sceneRepository = SceneRepositoryDouble()
    private val storyEventRepository = StoryEventRepositoryDouble()

    // Use Case
    private val useCase: ListStoryEventsCoveredByScene = ListStoryEventsCoveredBySceneUseCase(sceneRepository, storyEventRepository)
    private fun listStoryEventsCoveredByScene(sceneId: Scene.Id = scene.id) {
        runBlocking {
            useCase.invoke(sceneId) {
                storyEventItems = it
            }
        }
    }

    @Test
    fun `scene doesn't exist - should throw error`() {
        val error = assertThrows<SceneDoesNotExist>(::listStoryEventsCoveredByScene)
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given Scene Exists` {

        init {
            sceneRepository.givenScene(scene)
        }

        private val storyEvents = List(6) { makeStoryEvent(projectId = projectId) } + List(4) { makeStoryEvent() }
        init {
            storyEvents.onEach(storyEventRepository::givenStoryEvent)
        }

        @Test
        fun `no covered story events - should output empty list`() {
            listStoryEventsCoveredByScene()

            assertTrue(storyEventItems!!.isEmpty()) { "Expected to be empty" }
        }

        @Nested
        inner class `Given Some Story Events are Covered by Scene` {

            private val coveredStoryEvents = storyEvents.filter { it.projectId == projectId }.shuffled().take(3)
                .map { it.coveredByScene(scene.id).storyEvent }
                .onEach(storyEventRepository::givenStoryEvent)

            @Test
            fun `should output list of covered story events`() {
                listStoryEventsCoveredByScene()

                storyEventItems!!.size.mustEqual(3) { "Size of returned list was incorrect" }
                assertEquals(
                    coveredStoryEvents.map { it.id }.toSet(),
                    storyEventItems!!.map { it.storyEventId }.toSet()
                )
                storyEventItems!!.forEach { item ->
                    val backingItem = coveredStoryEvents.single { it.id == item.storyEventId }
                    item.storyEventName.mustEqual(backingItem.name.value)
                    item.time.mustEqual(backingItem.time.toLong())
                }
            }

            @Test
            fun `should output requested scene id`() {
                listStoryEventsCoveredByScene()

                storyEventItems!!.sceneId.mustEqual(scene.id)
            }

        }

    }


}