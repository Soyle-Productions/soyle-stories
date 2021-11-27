package com.soyle.stories.usecase.storyevent.coverage

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneAlreadyCoversStoryEvent
import com.soyle.stories.domain.scene.events.StoryEventAddedToScene
import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene
import com.soyle.stories.domain.storyevent.events.StoryEventUncoveredFromScene
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.storyevent.StoryEventDoesNotExist
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInScene
import com.soyle.stories.usecase.storyevent.coverage.cover.CoverStoryEventInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Cover Story Event in Scene Unit Test` {

    // Pre-requisites
    /** Project must be started */
    private val projectId = Project.Id()
    /** Scene must exist */
    private val scene = makeScene(projectId = projectId, coveredStoryEvents = emptySet())
    /** Story Event must exist */
    private val storyEvent = makeStoryEvent(projectId = projectId)

    // Post Conditions
    /** Should update up to two story events */
    private val updatedStoryEvents: List<StoryEvent>
    /** Should update scene */
    private var updatedScene: Scene? = null
    /** Should produce up to two scene events */
    private var storyEventAddedToScene: StoryEventAddedToScene? = null
    private var storyEventRemovedFromScene: StoryEventRemovedFromScene? = null
    /** Should produce up to two story event events */
    private var storyEventCoveredByScene: StoryEventCoveredByScene? = null
    private var storyEventUncoveredFromScene: StoryEventUncoveredFromScene? = null
    private fun allEvents() = listOfNotNull(
        storyEventAddedToScene,
        storyEventRemovedFromScene,
        storyEventCoveredByScene,
        storyEventUncoveredFromScene
    )

    // Repositories
    private val storyEventRepository: StoryEventRepositoryDouble
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)

    init {
        val mutableUpdatedStoryEvents = mutableListOf<StoryEvent>()
        storyEventRepository = StoryEventRepositoryDouble(onUpdateStoryEvent = mutableUpdatedStoryEvents::add)
        updatedStoryEvents = mutableUpdatedStoryEvents
    }

    // Use Case
    private val useCase: CoverStoryEventInScene = CoverStoryEventInSceneUseCase(storyEventRepository, sceneRepository)
    private fun coverStoryEventInScene() {
        runBlocking {
            useCase.invoke(storyEvent.id, scene.id) {
                storyEventAddedToScene = it.storyEventAddedToScene
                storyEventRemovedFromScene = it.storyEventRemovedFromScene
                storyEventCoveredByScene = it.storyEventCoveredByScene
                storyEventUncoveredFromScene = it.storyEventUncoveredFromScene
            }
        }
    }

    @Test
    fun `Story Event Doesn't Exist`() {
        val error = assertThrows<StoryEventDoesNotExist> { coverStoryEventInScene() }

        error.storyEventId shouldBeEqualTo storyEvent.id.uuid
        updatedStoryEvents.shouldBeEmpty()
        updatedScene.shouldBeNull()
        allEvents().shouldBeEmpty()
    }

    @Test
    fun `Scene Doesn't Exist`() {
        storyEventRepository.givenStoryEvent(storyEvent)

        val error = assertThrows<SceneDoesNotExist> { coverStoryEventInScene() }

        error.sceneId shouldBeEqualTo scene.id.uuid
        updatedStoryEvents.shouldBeEmpty()
        updatedScene.shouldBeNull()
        allEvents().shouldBeEmpty()
    }

    @Test
    fun `Scene Does Not Yet Cover Story Event`() {
        storyEventRepository.givenStoryEvent(storyEvent)
        sceneRepository.givenScene(scene)

        coverStoryEventInScene()

        updatedStoryEvents.single().id shouldBeEqualTo storyEvent.id
        updatedStoryEvents.single().sceneId shouldBeEqualTo scene.id
        updatedScene!!.mustEqual(scene.withStoryEvent(storyEvent.id).scene)
        storyEventCoveredByScene.shouldNotBeNull().mustEqual(StoryEventCoveredByScene(storyEvent.id, scene.id, null))
        storyEventAddedToScene.shouldNotBeNull().mustEqual(StoryEventAddedToScene(scene.id, storyEvent.id))
        storyEventRemovedFromScene.shouldBeNull()
        storyEventUncoveredFromScene.shouldBeNull()
    }

    @Test
    fun `Scene Already Covers Story Event`() {
        storyEventRepository.givenStoryEvent(storyEvent.coveredByScene(scene.id).storyEvent)
        sceneRepository.givenScene(scene.withStoryEvent(storyEvent.id).scene)

        assertThrows<SceneAlreadyCoversStoryEvent> { coverStoryEventInScene() }

        updatedStoryEvents.shouldBeEmpty()
        updatedScene.shouldBeNull()
        allEvents().shouldBeEmpty()
    }

    @Test
    fun `Scene Already Covers Another Story Event`() {
        val otherStoryEvent = makeStoryEvent(sceneId = scene.id)

        storyEventRepository.givenStoryEvent(storyEvent)
        storyEventRepository.givenStoryEvent(otherStoryEvent)
        sceneRepository.givenScene(scene.withStoryEvent(otherStoryEvent.id).scene)

        coverStoryEventInScene()

        updatedStoryEvents.size shouldBeEqualTo 2
        updatedStoryEvents.single { it.id == storyEvent.id }
        updatedStoryEvents.single { it.id == otherStoryEvent.id }
        updatedScene!!.mustEqual(scene.withStoryEvent(storyEvent.id).scene)
        storyEventCoveredByScene.shouldNotBeNull().mustEqual(StoryEventCoveredByScene(storyEvent.id, scene.id, null))
        storyEventAddedToScene.shouldNotBeNull().mustEqual(StoryEventAddedToScene(scene.id, storyEvent.id))
        storyEventRemovedFromScene.shouldNotBeNull().mustEqual(StoryEventRemovedFromScene(scene.id, otherStoryEvent.id))
        storyEventUncoveredFromScene.shouldNotBeNull().mustEqual(StoryEventUncoveredFromScene(otherStoryEvent.id, scene.id))
    }

    @Test
    fun `Scene Already Covers Story Event but Story Event is Out of Sync`() {
        storyEventRepository.givenStoryEvent(storyEvent) // doesn't know scene covers it
        sceneRepository.givenScene(scene.withStoryEvent(storyEvent.id).scene)

        coverStoryEventInScene()

        updatedStoryEvents.single().id.mustEqual(storyEvent.id)
        updatedScene.shouldBeNull()
        storyEventCoveredByScene.shouldNotBeNull().mustEqual(StoryEventCoveredByScene(storyEvent.id, scene.id, null))
        storyEventUncoveredFromScene.shouldBeNull()
        storyEventAddedToScene.shouldBeNull()
        storyEventRemovedFromScene.shouldBeNull()
    }

    @Test
    fun `Story Event Already Covered by Scene but Scene is Out of Sync`() {
        storyEventRepository.givenStoryEvent(storyEvent.coveredByScene(scene.id).storyEvent)
        sceneRepository.givenScene(scene) // doesn't know it covers story event

        coverStoryEventInScene()

        updatedStoryEvents.shouldBeEmpty()
        updatedScene.shouldNotBeNull().mustEqual(scene.withStoryEvent(storyEvent.id).scene)
        storyEventCoveredByScene.shouldBeNull()
        storyEventUncoveredFromScene.shouldBeNull()
        storyEventAddedToScene.shouldNotBeNull().mustEqual(StoryEventAddedToScene(scene.id, storyEvent.id))
        storyEventRemovedFromScene.shouldBeNull()
    }

}