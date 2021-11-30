package com.soyle.stories.usecase.storyevent.coverage

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneDoesNotCoverStoryEvent
import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.domain.storyevent.exceptions.StoryEventAlreadyWithoutCoverage
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
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

    /** Scene must exist and contain story event */
    private val scene =
        makeScene(sceneId = storyEvent.sceneId!!, projectId = projectId, coveredStoryEvents = setOf(storyEvent.id))

    // Post Conditions
    /** Should update storyevent */
    private var updatedStoryEvent: StoryEvent? = null

    /** May update scene */
    private var updatedScene: Scene? = null

    /** May produce removed from scene event */
    private var storyEventRemovedFromScene: StoryEventRemovedFromScene? = null
    /** Alternatively, may produce scene update failure */
    private var sceneUpdateFailure: Throwable? = null

    /** Should produce uncovered from scene event */
    private var storyEventUncoveredFromScene: StoryEventUncoveredFromScene? = null
    private fun allEvents() = listOfNotNull(
        storyEventRemovedFromScene,
        storyEventUncoveredFromScene
    )

    // Repositories
    private val storyEventRepository = StoryEventRepositoryDouble(onUpdateStoryEvent = ::updatedStoryEvent::set)
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)

    // Use Case
    private val useCase: UncoverStoryEventFromScene =
        UncoverStoryEventFromSceneUseCase(storyEventRepository, sceneRepository)

    private fun coverStoryEventInScene() {
        runBlocking {
            useCase.invoke(storyEvent.id) { uncovered, removed ->
                storyEventUncoveredFromScene = uncovered
                storyEventRemovedFromScene = removed.getOrNull()
                sceneUpdateFailure = removed.exceptionOrNull()
            }
        }
    }

    @Test
    fun `Story Event Doesn't Exist`() {
        val error = assertThrows<StoryEventDoesNotExist> { coverStoryEventInScene() }

        error.storyEventId shouldBeEqualTo storyEvent.id.uuid
        updatedStoryEvent.shouldBeNull()
        updatedScene.shouldBeNull()
        allEvents().shouldBeEmpty()
    }

    @Test
    fun `Story Event is Not Covered`() {
        storyEventRepository.givenStoryEvent(storyEvent.withoutCoverage().storyEvent)

        assertThrows<StoryEventAlreadyWithoutCoverage> { coverStoryEventInScene() }
    }

    @Test
    fun `Scene Doesn't Exist`() {
        storyEventRepository.givenStoryEvent(storyEvent)

        coverStoryEventInScene()

        updatedStoryEvent.shouldNotBeNull().sceneId.shouldBeNull()
        updatedScene.shouldBeNull()

        storyEventUncoveredFromScene.shouldNotBeNull().run {
            storyEventId.shouldBeEqualTo(storyEvent.id)
            previousSceneId.shouldBeEqualTo(scene.id)
        }
        storyEventRemovedFromScene.shouldBeNull()
        sceneUpdateFailure.shouldBeInstanceOf<SceneDoesNotExist>().sceneId.shouldBeEqualTo(scene.id.uuid)
    }

    @Test
    fun `Scene Does Not Cover Story Event (out of sync)`() {
        storyEventRepository.givenStoryEvent(storyEvent)
        sceneRepository.givenScene(scene.withoutStoryEvent(storyEvent.id).scene)

        coverStoryEventInScene()

        updatedStoryEvent.shouldNotBeNull().sceneId.shouldBeNull()
        updatedScene.shouldBeNull()

        storyEventUncoveredFromScene.shouldNotBeNull().run {
            storyEventId.shouldBeEqualTo(storyEvent.id)
            previousSceneId.shouldBeEqualTo(scene.id)
        }
        storyEventRemovedFromScene.shouldBeNull()
        sceneUpdateFailure.shouldNotBeNull().shouldBeEqualTo(SceneDoesNotCoverStoryEvent(scene.id, storyEvent.id))
    }

    @Test
    fun `Story Event and Scene are In Sync`() {
        storyEventRepository.givenStoryEvent(storyEvent)
        sceneRepository.givenScene(scene)

        coverStoryEventInScene()

        updatedStoryEvent.shouldNotBeNull().sceneId.shouldBeNull()
        updatedScene.shouldNotBeNull().coveredStoryEvents.shouldNotContain(storyEvent.id)

        storyEventUncoveredFromScene.shouldNotBeNull().run {
            storyEventId.shouldBeEqualTo(storyEvent.id)
            previousSceneId.shouldBeEqualTo(scene.id)
        }
        storyEventRemovedFromScene.shouldNotBeNull().run {
            sceneId.mustEqual(scene.id)
            storyEventId.mustEqual(storyEvent.id)
        }
    }

}