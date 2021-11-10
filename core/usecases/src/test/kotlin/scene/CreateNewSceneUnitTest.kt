package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocaleDouble
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.domain.storyevent.storyEventName
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.scene.createNewScene.CreateNewScene
import com.soyle.stories.usecase.scene.createNewScene.CreateNewSceneUseCase
import com.soyle.stories.usecase.storyevent.StoryEventItem
import com.soyle.stories.usecase.storyevent.StoryEventRepository
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.storyEventDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.*

class CreateNewSceneUnitTest {

	private val createStoryEventOutputException = Exception("I was thrown from the output of creating a story event.")

	val projectId = Project.Id()
	val validSceneName = NonBlankString.create("Valid Scene Name")!!
	val storyEventId = StoryEvent.Id().uuid
	val proseId = Prose.Id().uuid
	val relativeSceneId = Scene.Id().uuid

	var createStoryEventRequest: CreateStoryEvent.RequestModel? = null
	var createStoryEventResult: Any? = null
	var savedScene: Scene? = null
	var result: Any? = null
	private var createdProse: Prose? = null

	private val proseRepository = ProseRepositoryDouble(onCreateProse = ::createdProse::set)

	@Nested
	inner class `Create Scene with just a name` {

		fun whenUseCaseIsExecuted(withName: NonBlankString) {
			val request = CreateNewScene.RequestModel(withName, SceneLocaleDouble())
			this@CreateNewSceneUnitTest.whenUseCaseIsExecuted(request)
		}

		@Test
		fun `create story event`() {
			whenUseCaseIsExecuted(validSceneName)
			assertStoryEventCreated()
			assertStoryEventOutputNotified()
			assertSceneCreatedWithStoryEventId()
		}

		@Test
		fun `create prose`() {
			whenUseCaseIsExecuted(validSceneName)
			assertProseCreated()
		}

		@Test
		fun `error in create event output does not effect use case`() {
			givenStoryEventOutputWillThrowError()
			try { whenUseCaseIsExecuted(validSceneName) }
			catch (t: Throwable) {
				if (t != createStoryEventOutputException) throw t
			}
			assertStoryEventCreated()
			assertStoryEventOutputNotified()
			assertSceneCreatedWithStoryEventId()
		}

	}

	@Nested
	inner class `Create Scene with story event` {

		fun whenUseCaseIsExecuted(withName: NonBlankString) {
			val request = CreateNewScene.RequestModel(withName, storyEventId, SceneLocaleDouble())
			this@CreateNewSceneUnitTest.whenUseCaseIsExecuted(request)
		}

		@Test
		fun `story event does not exist`() {
			whenUseCaseIsExecuted(withName = validSceneName)
			storyEventDoesNotExist(storyEventId).invoke(result)
		}

		@Test
		fun `valid name and story event`() {
			given(storyEventWithId = storyEventId, sceneWithId = relativeSceneId)
			whenUseCaseIsExecuted(withName = validSceneName)
			assertSceneSavedCorrectly()
			val result = assertValidResponseModel(result)
			assertEquals(1, result.sceneIndex)
		}
	}

	@Nested
	inner class `Create Scene before relative Scene` {

		fun whenUseCaseIsExecuted(withName: NonBlankString) {
			val request = CreateNewScene.RequestModel(withName, relativeSceneId, true, SceneLocaleDouble())
			this@CreateNewSceneUnitTest.whenUseCaseIsExecuted(request)
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
			try { whenUseCaseIsExecuted(validSceneName) }
			catch (t: Throwable) {
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

	private val sceneRepository: SceneRepository = SceneRepositoryDouble(onAddNewScene = {
		savedScene = it
	})
	private val storyEventRepository: StoryEventRepository = StoryEventRepositoryDouble()

	private fun given(storyEventWithId: UUID? = null, sceneWithId: UUID? = null, numberOfScenesAfterRelativeScene: Int = 0) {
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
					makeScene(sceneId = Scene.Id(sceneWithId), projectId = projectId, storyEventId = StoryEvent.Id(storyEventId), proseId = Prose.Id(proseId)),
				sceneRepository.getSceneIdsInOrder(projectId) + Scene.Id(sceneWithId)
				)
				repeat(numberOfScenesAfterRelativeScene) {
					val scene = makeScene(projectId = projectId)
					sceneRepository.createNewScene(scene,
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

	private fun whenUseCaseIsExecuted(requestModel: CreateNewScene.RequestModel)
	{
		val useCase: CreateNewScene = CreateNewSceneUseCase(projectId.uuid, sceneRepository, storyEventRepository, proseRepository, object : CreateStoryEvent {
			override suspend fun invoke(request: CreateStoryEvent.RequestModel, output: CreateStoryEvent.OutputPort) {
				createStoryEventRequest = request
				val newStoryEvent = makeStoryEvent(name = request.name, projectId = request.projectId)
				storyEventRepository.addNewStoryEvent(newStoryEvent)
				output.receiveCreateStoryEventResponse(CreateStoryEvent.ResponseModel(
					StoryEventCreated(newStoryEvent.id, request.name.value, 0u, request.projectId),
					null
				))
			}
		})
		runBlocking {
			useCase.invoke(requestModel, object : CreateNewScene.OutputPort {
				override val createStoryEventOutputPort: CreateStoryEvent.OutputPort = object : CreateStoryEvent.OutputPort {
					override suspend fun receiveCreateStoryEventResponse(response: CreateStoryEvent.ResponseModel) {
						createStoryEventResult = response
						storyEventOutputExecution.invoke()
					}
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

	private fun assertSceneSavedCorrectly()
	{
		val savedScene = savedScene!!
		assertEquals(validSceneName, savedScene.name)
		assertEquals(projectId, savedScene.projectId)
		assertEquals(storyEventId, savedScene.storyEventId.uuid)
	}

	private fun assertSceneCreatedWithStoryEventId() {
		val savedScene = savedScene!!
		assertEquals(validSceneName, savedScene.name)
		assertEquals(projectId, savedScene.projectId)
		assertEquals((createStoryEventResult as CreateStoryEvent.ResponseModel).createdStoryEvent.storyEventId, savedScene.storyEventId)
	}

	private fun assertValidResponseModel(actual: Any?): CreateNewScene.ResponseModel
	{
		val savedScene = savedScene!!
		actual as CreateNewScene.ResponseModel
		assertEquals(savedScene.id.uuid, actual.sceneId)
		assertEquals(validSceneName.value, actual.sceneName)
		assertEquals(createdProse!!.id, actual.sceneProse)
		return actual
	}

}