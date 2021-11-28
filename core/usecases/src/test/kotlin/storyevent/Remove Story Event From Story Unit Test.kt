package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventNoLongerHappens
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.create.CreateStoryEventUseCase
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProject
import com.soyle.stories.usecase.storyevent.remove.RemoveStoryEventFromProjectUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.amshove.kluent.shouldNotContain
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Remove Story Event From Story Unit Test` {

    // Summary
    /** The specified story event is removed from the project and all references made to it are invalidated */

    // Preconditions
    /** A project has been created and opened */
    private val projectId = Project.Id()

    /** The story event exists */
    private val storyEvent = makeStoryEvent(projectId = projectId)

    // post conditions
    /** outputs a story event removed event */
    private var removedStoryEvent: StoryEventNoLongerHappens? = null

    /** May output a story event removed from scene event */
    private var removedFromScene: StoryEventRemovedFromScene? = null

    /** removes the story event from the repository */
    private var removedStoryEventId: StoryEvent.Id? = null

    /** May update a scene */
    private var updatedScene: Scene? = null

    // Repositories
    private val storyEventRepository = StoryEventRepositoryDouble(onRemoveStoryEvent = ::removedStoryEventId::set)
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)

    // Use Case
    private val useCase: RemoveStoryEventFromProject =
        RemoveStoryEventFromProjectUseCase(storyEventRepository, sceneRepository)

    private fun removeStoryEvent() {
        runBlocking {
            useCase.invoke(storyEvent.id) {
                removedStoryEvent = it.storyEventNoLongerHappens
                removedFromScene = it.storyEventRemovedFromScene
            }
        }
    }

    @Test
    fun `story event must exist`() {
        val error = assertThrows<StoryEventDoesNotExist> { removeStoryEvent() }

        error.storyEventId.mustEqual(storyEvent.id.uuid)
        removedStoryEvent.shouldBeNull()
        removedFromScene.shouldBeNull()
        removedStoryEventId.shouldBeNull()
        updatedScene.shouldBeNull()
    }

    @Test
    fun `story event must be removed from repository`() {
        storyEventRepository.givenStoryEvent(storyEvent)

        removeStoryEvent()

        removedStoryEventId.mustEqual(storyEvent.id)
    }

    @Test
    fun `story event no longer happens event must be produced`() {
        storyEventRepository.givenStoryEvent(storyEvent)

        removeStoryEvent()

        removedStoryEvent.mustEqual(StoryEventNoLongerHappens(storyEvent.id))
    }

    @Test
    fun `should not update scene`() {
        storyEventRepository.givenStoryEvent(storyEvent)

        removeStoryEvent()

        removedFromScene.shouldBeNull()
        updatedScene.shouldBeNull()
    }

    @Nested
    inner class `Given Story Event is Covered by Scene` {

        private val scene = makeScene(projectId = storyEvent.projectId)

        init {
            storyEventRepository.givenStoryEvent(storyEvent.coveredByScene(scene.id).storyEvent)
            sceneRepository.givenScene(scene.withStoryEvent(storyEvent).scene)
        }

        @Test
        fun `should update scene`() {
            removeStoryEvent()

            updatedScene.shouldNotBeNull().id.shouldBeEqualTo(scene.id)
            updatedScene.shouldNotBeNull().coveredStoryEvents.shouldNotContain(storyEvent.id)
        }

        @Test
        fun `should produce story event removed from scene event`() {
            removeStoryEvent()

            removedFromScene!!.shouldBeEqualTo(StoryEventRemovedFromScene(scene.id, storyEvent.id))
        }

    }

}