package com.soyle.stories.usecase.storyevent.coverage

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.domain.storyevent.exceptions.DuplicateStoryEventOperationException
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromScene
import com.soyle.stories.usecase.storyevent.coverage.uncover.UncoverStoryEventFromSceneUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Uncover Story Event from Scene Unit Test` {

    // Pre-requisites
    /** Project must be started */
    private val projectId = Project.Id()

    /** Story Event must exist and be covered by scene */
    private val storyEvent = makeStoryEvent(projectId = projectId, sceneId = Scene.Id())

    // Post Conditions
    /** Should update storyevent */
    private var updatedStoryEvent: StoryEvent? = null

    /** Should produce uncovered from scene event */
    private var storyEventUncoveredFromScene: StoryEventUncoveredFromScene? = null
    private fun allEvents() = listOfNotNull(
        storyEventUncoveredFromScene
    )

    // Repositories
    private val storyEventRepository = StoryEventRepositoryDouble(onUpdateStoryEvent = ::updatedStoryEvent::set)

    // Use Case
    private val useCase: UncoverStoryEventFromScene =
        UncoverStoryEventFromSceneUseCase(storyEventRepository)

    private fun coverStoryEventInScene() {
        runBlocking {
            useCase.invoke(storyEvent.id) { uncovered ->
                storyEventUncoveredFromScene = uncovered
            }
        }
    }

    @Test
    fun `Story Event Doesn't Exist`() {
        val error = assertThrows<StoryEventDoesNotExist> { coverStoryEventInScene() }

        error.storyEventId shouldBeEqualTo storyEvent.id.uuid
        updatedStoryEvent.shouldBeNull()
        allEvents().shouldBeEmpty()
    }

    @Test
    fun `Story Event is Not Covered`() {
        storyEventRepository.givenStoryEvent(storyEvent.withoutCoverage().storyEvent)

        val error = assertThrows<DuplicateStoryEventOperationException> { coverStoryEventInScene() }

        error.message.mustEqual("${storyEvent.id} already without coverage")
    }

    @Test
    fun `Story Event Covered by Scene`() {
        storyEventRepository.givenStoryEvent(storyEvent)

        coverStoryEventInScene()

        updatedStoryEvent.shouldNotBeNull().sceneId.shouldBeNull()
        storyEventUncoveredFromScene.shouldNotBeNull().run {
            storyEventId.shouldBeEqualTo(storyEvent.id)
        }
    }

}