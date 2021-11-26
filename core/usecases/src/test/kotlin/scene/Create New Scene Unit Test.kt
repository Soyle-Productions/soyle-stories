package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.events.SceneCreated
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.scene.order.SceneOrder
import com.soyle.stories.domain.scene.order.SceneOrderService
import com.soyle.stories.domain.scene.order.SceneOrderUpdate
import com.soyle.stories.domain.scene.sceneName
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.StoryEventTimeService
import com.soyle.stories.domain.storyevent.events.StoryEventCoveredByScene
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene
import com.soyle.stories.usecase.scene.createNewScene.CreateNewSceneUseCase
import kotlinx.coroutines.runBlocking
import org.amshove.kluent.*
import org.junit.jupiter.api.*
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class `Create New Scene Unit Test` {

    // Pre Conditions
    /** A project has been started */
    val projectId = Project.Id()

    // Inputs
    val inputName = sceneName()

    // Post Conditions
    /** A scene should be created */
    var createdScene: Scene? = null
    var sceneCreated: SceneCreated? = null

    /** The scene order should be updated */
    var updatedSceneOrder: SceneOrder? = null
    var sceneOrderUpdated: SceneOrderUpdate<*>? = null

    /** A story event should be created */
    var createdStoryEvent: StoryEvent? = null
    var storyEventCreated: StoryEventCreated? = null
    var storyEventCoveredByScene: StoryEventCoveredByScene? = null

    /** Prose should be created for the scene */
    private var createdProse: Prose? = null

    // repositories
    private val sceneRepository = SceneRepositoryDouble(onAddNewScene = ::createdScene::set, onUpdateSceneOrder = ::updatedSceneOrder::set)
    private val proseRepository = ProseRepositoryDouble(onCreateProse = ::createdProse::set)
    private val storyEventRepository = StoryEventRepositoryDouble(onAddNewStoryEvent = ::createdStoryEvent::set)

    // useCase
    private val useCase: CreateNewScene =
        CreateNewSceneUseCase(
            SceneOrderService(),
            StoryEventTimeService(storyEventRepository),
            sceneRepository,
            proseRepository,
            storyEventRepository
        )

    private fun createScene(relativeSceneId: Scene.Id? = null, delta: Int = -1) = runBlocking {
        assert(delta == 1 || delta == -1)
        val request = CreateNewScene.RequestModel(inputName, projectId).let {
            if (relativeSceneId != null) {
                if (delta == -1) it.before(relativeSceneId)
                else it.after(relativeSceneId)
            } else it
        }
        useCase.invoke(request) {
            sceneCreated = it.sceneCreated
            storyEventCreated = it.storyEventCreated
            sceneOrderUpdated = it.sceneOrderUpdated
            storyEventCoveredByScene = it.storyEventCoveredByScene
        }
    }

    @Nested
    inner class `Create Scene with just a name` {

        @Test
        fun `should produce scene created event`() {
            createScene()

            val createdScene = `should create a scene`()
            val sceneCreatedEvent = sceneCreated.`should not be null`()
            sceneCreatedEvent.sceneId `should be equal to` createdScene.id
        }

        @Test
        fun `should updated scene order`() {
            createScene()

            val updatedSceneOrder = `scene order should be updated`()
            sceneOrderUpdated!!.sceneOrder.shouldBeEqualTo(updatedSceneOrder)
        }

        @Test
        fun `story event should be created and covered by scene`() {
            createScene()

            val createdStoryEvent = `story event should be created`()
            createdScene!!.storyEventId.mustEqual(createdStoryEvent.id)
            createdStoryEvent.name.mustEqual(inputName)

            val storyEventCoveredByScene = storyEventCoveredByScene!!
            storyEventCoveredByScene.storyEventId.mustEqual(createdStoryEvent.id)
            storyEventCoveredByScene.sceneId.mustEqual(createdScene!!.id)
            storyEventCoveredByScene.uncovered.mustEqual(null)
        }

        @Test
        fun `created story event should be at start of project`() {
            createScene()

            val createdStoryEvent = `story event should be created`()
            createdStoryEvent.time.shouldBeEqualTo(1u)
            createdStoryEvent.sceneId.mustEqual(createdScene!!.id)
            storyEventCreated!!.mustEqual(StoryEventCreated(
                createdStoryEvent.id,
                createdStoryEvent.name.value,
                createdStoryEvent.time,
                projectId
            ))
        }

        @Test
        fun `prose should be created for scene`() {
            createScene()

            val createdScene = `should create a scene`()
            val createdProse = createdProse.`should not be null`()
            createdProse.id.mustEqual(createdScene.proseId)
            createdProse.projectId `should be equal to` projectId
        }

        @Nested
        inner class `Given Story Events Exist in the Project` {

            init {
                listOf(
                    makeStoryEvent(projectId = projectId, time = 4u),
                    makeStoryEvent(projectId = projectId, time = 13u),
                    makeStoryEvent(projectId = projectId, time = 8u),
                    makeStoryEvent(projectId = projectId, time = 45u),
                    makeStoryEvent(projectId = projectId, time = 16u),
                    makeStoryEvent(projectId = Project.Id(), time = 67u)
                )
                    .onEach(storyEventRepository::givenStoryEvent)
            }

            @Test
            fun `should add story event to the end of the project`() {
                createScene()

                val createdStoryEvent = `story event should be created`()
                createdStoryEvent.time.shouldBeEqualTo(46u)
                storyEventCreated!!.mustEqual(StoryEventCreated(
                    createdStoryEvent.id,
                    createdStoryEvent.name.value,
                    createdStoryEvent.time,
                    projectId
                ))
            }

        }

        @Nested
        inner class `Given Other Scenes Exist in Project` {

            private val preExistingScenes = List(5) { makeScene(projectId = projectId) }
                .onEach(sceneRepository::givenScene)

            @Test
            fun `scene order should contain all pre-existing scene ids`() {
                createScene()

                `scene order should be updated`()
                    .order.shouldContainAll(preExistingScenes.map { it.id })
            }

        }
    }

    @Nested
    inner class `When Scene is Created Relative to Another Scene` {

        private val relativeSceneId = Scene.Id()

        @ParameterizedTest
        @ValueSource(ints = [-1, 1])
        fun `relative scene does not exist - should throw error`(delta: Int) {
            val error = assertThrows<SceneDoesNotExist> { createScene(relativeSceneId) }

            error.sceneId.mustEqual(relativeSceneId.uuid)
            listOf(
                createdScene,
                createdProse,
                createdStoryEvent,
                updatedSceneOrder,

                sceneCreated,
                storyEventCreated,
                sceneOrderUpdated
            ).shouldMatchAllWith {
                it == null
            }
        }

        @Nested
        inner class `Given Relative Scene Exists` {

            init {
                // place relative scene dead-center of the scene order
                repeat(2) { sceneRepository.givenScene(makeScene(projectId = projectId)) }
                sceneRepository.givenScene(makeScene(projectId = projectId, sceneId = relativeSceneId))
                repeat(2) { sceneRepository.givenScene(makeScene(projectId = projectId)) }
            }

            @Test
            fun `create before - should insert new scene before relative scene`() {
                createScene(relativeSceneId, -1)

                val updatedSceneOrder = `scene order should be updated`()
                updatedSceneOrder.order.size.shouldBeEqualTo(6)
                updatedSceneOrder.order.toList()[2].shouldBeEqualTo(createdScene!!.id)
            }

            @Test
            fun `create after - should insert new scene after relative scene`() {
                createScene(relativeSceneId, 1)

                val updatedSceneOrder = `scene order should be updated`()
                updatedSceneOrder.order.size.shouldBeEqualTo(6)
                updatedSceneOrder.order.toList()[3].shouldBeEqualTo(createdScene!!.id)
            }

        }

    }

    fun `should create a scene`(): Scene {
        val createdScene = createdScene.`should not be null`()
        createdScene.projectId.mustEqual(projectId)
        createdScene.name.mustEqual(inputName)
        return createdScene
    }

    fun `story event should be created`(): StoryEvent {
        return this.createdStoryEvent.`should not be null`()
    }

    fun `scene order should be updated`(): SceneOrder {
        val updatedSceneOrder = updatedSceneOrder.`should not be null`()
        updatedSceneOrder.projectId.shouldBeEqualTo(projectId)
        updatedSceneOrder.order.shouldContain(createdScene!!.id)
        return updatedSceneOrder
    }
/*
    @Nested
    inner class `Create Scene before relative Scene` {

        fun whenUseCaseIsExecuted(withName: NonBlankString) {
            val request = CreateNewScene.RequestModel(withName, relativeSceneId, true, SceneLocaleDouble())
            this@`Create New Scene Unit Test`.whenUseCaseIsExecuted(request)
        }

        @Test
        fun `relative scene does not exist`() {
            whenUseCaseIsExecuted(withName = validSceneName)
            sceneDoesNotExist(relativeSceneId).invoke(result)
        }

        @Test
        fun `create story event`() {
            given(sceneWithId = relativeSceneId)
            whenUseCaseIsExecuted(validSceneName)
            assertCreatedSceneIsBeforeRelativeScene()
            assertCreatedStoryEventIsBeforeRelativeSceneStoryEvent()
            assertStoryEventOutputNotified()
        }

        @Test
        fun `error in create event output does not effect use case`() {
            givenStoryEventOutputWillThrowError()
            given(sceneWithId = relativeSceneId)
            try {
                whenUseCaseIsExecuted(validSceneName)
            } catch (t: Throwable) {
                if (t != createStoryEventOutputException) throw t
            }
            assertCreatedSceneIsBeforeRelativeScene()
            assertCreatedStoryEventIsBeforeRelativeSceneStoryEvent()
            assertStoryEventOutputNotified()
            val result = assertValidResponseModel(result)
            assertEquals(0, result.sceneIndex)
        }

        @Test
        fun `output scenes with updated indices`() {
            given(sceneWithId = relativeSceneId, numberOfScenesAfterRelativeScene = 5)
            whenUseCaseIsExecuted(validSceneName)
            assertCreatedSceneIsBeforeRelativeScene()
            assertCreatedStoryEventIsBeforeRelativeSceneStoryEvent()
            assertStoryEventOutputNotified()
            val result = assertValidResponseModel(result)
            assertEquals(6, result.affectedScenes.size)
        }

    }


    private fun given(
        storyEventWithId: UUID? = null,
        sceneWithId: UUID? = null,
        numberOfScenesAfterRelativeScene: Int = 0
    ) {
        if (storyEventWithId != null) {
            runBlocking {
                storyEventRepository.addNewStoryEvent(
                    makeStoryEvent(StoryEvent.Id(storyEventWithId), time = 0u, projectId = projectId)
                )
            }
        }
        if (sceneWithId != null) {
            runBlocking {
                sceneRepository.createNewScene(
                    makeScene(
                        sceneId = Scene.Id(sceneWithId),
                        projectId = projectId,
                        storyEventId = StoryEvent.Id(storyEventId),
                        proseId = Prose.Id(proseId)
                    ),
                    sceneRepository.getSceneIdsInOrder(projectId) + Scene.Id(sceneWithId)
                )
                repeat(numberOfScenesAfterRelativeScene) {
                    val scene = makeScene(projectId = projectId)
                    sceneRepository.createNewScene(
                        scene,
                        sceneRepository.getSceneIdsInOrder(projectId) + scene.id
                    )
                }

                if (storyEventRepository.getStoryEventById(StoryEvent.Id(storyEventId)) == null) {
                    storyEventRepository.addNewStoryEvent(
                        makeStoryEvent(StoryEvent.Id(storyEventId), time = 0u, projectId = projectId)
                    )
                }
            }
        }
    }

    private var storyEventOutputExecution = {}
    private fun givenStoryEventOutputWillThrowError() {
        storyEventOutputExecution = {
            throw createStoryEventOutputException
        }
    }

    private fun whenUseCaseIsExecuted(requestModel: CreateNewScene.RequestModel) {
        val useCase: CreateNewScene = CreateNewSceneUseCase(
            projectId.uuid,
            sceneRepository,
            storyEventRepository,
            proseRepository,
            object : CreateStoryEvent {
                override suspend fun invoke(
                    request: CreateStoryEvent.RequestModel,
                    output: CreateStoryEvent.OutputPort
                ) {
                    createStoryEventRequest = request
                    val newStoryEvent = makeStoryEvent(name = request.name, projectId = request.projectId)
                    storyEventRepository.addNewStoryEvent(newStoryEvent)
                    output.receiveCreateStoryEventResponse(
                        CreateStoryEvent.ResponseModel(
                            StoryEventCreated(newStoryEvent.id, request.name.value, 0u, request.projectId),
                            null
                        )
                    )
                }
            })
        runBlocking {
            useCase.invoke(requestModel, object : CreateNewScene.OutputPort {
                override val createStoryEventOutputPort: CreateStoryEvent.OutputPort =
                    CreateStoryEvent.OutputPort { response ->
                        createStoryEventResult = response
                        storyEventOutputExecution.invoke()
                    }

                override fun receiveCreateNewSceneFailure(failure: Exception) {
                    result = failure
                }

                override fun receiveCreateNewSceneResponse(response: CreateNewScene.ResponseModel) {
                    result = response
                }
            })
        }
    }

    private fun assertStoryEventOutputNotified() {
        createStoryEventResult as CreateStoryEvent.ResponseModel
    }

    private fun assertStoryEventCreated() {
        val createStoryEventRequest = createStoryEventRequest!!
        assertEquals(validSceneName, createStoryEventRequest.name)
        assertEquals(projectId, createStoryEventRequest.projectId)
    }

    private fun assertProseCreated() {
        val proseId = createdProse!!.id
        val savedScene = savedScene!!
        assertEquals(proseId, savedScene.proseId)
    }

    private fun assertCreatedSceneIsBeforeRelativeScene() {
        val savedScene = savedScene!!
        runBlocking {
            assertEquals(savedScene.id, sceneRepository.getSceneIdsInOrder(projectId).first())
        }
    }

    private fun assertCreatedStoryEventIsBeforeRelativeSceneStoryEvent() {
        createStoryEventRequest!!
    }

    private fun assertSceneSavedCorrectly() {
        val savedScene = savedScene!!
        assertEquals(validSceneName, savedScene.name)
        assertEquals(projectId, savedScene.projectId)
        assertEquals(storyEventId, savedScene.storyEventId.uuid)
    }

    private fun assertSceneCreatedWithStoryEventId() {
        val savedScene = savedScene!!
        assertEquals(validSceneName, savedScene.name)
        assertEquals(projectId, savedScene.projectId)
        assertEquals(
            (createStoryEventResult as CreateStoryEvent.ResponseModel).createdStoryEvent.storyEventId,
            savedScene.storyEventId
        )
    }

    private fun assertValidResponseModel(actual: Any?): CreateNewScene.ResponseModel {
        val savedScene = savedScene!!
        actual as CreateNewScene.ResponseModel
        assertEquals(savedScene.id.uuid, actual.sceneId)
        assertEquals(validSceneName.value, actual.sceneName)
        assertEquals(createdProse!!.id, actual.sceneProse)
        return actual
    }
*/
}