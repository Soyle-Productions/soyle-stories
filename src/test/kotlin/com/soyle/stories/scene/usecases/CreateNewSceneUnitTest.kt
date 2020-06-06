package com.soyle.stories.scene.usecases

import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.scene.SceneNameCannotBeBlank
import com.soyle.stories.scene.doubles.LocaleDouble
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.sceneDoesNotExist
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene
import com.soyle.stories.scene.usecases.createNewScene.CreateNewSceneUseCase
import com.soyle.stories.storyevent.StoryEventException
import com.soyle.stories.storyevent.doubles.StoryEventRepositoryDouble
import com.soyle.stories.storyevent.repositories.StoryEventRepository
import com.soyle.stories.storyevent.storyEventDoesNotExist
import com.soyle.stories.storyevent.usecases.StoryEventItem
import com.soyle.stories.storyevent.usecases.createStoryEvent.CreateStoryEvent
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class CreateNewSceneUnitTest {

	private val createStoryEventOutputException = Exception("I was thrown from the output of creating a story event.")

	val projectId = Project.Id()
	val validSceneName = "Valid Scene Name"
	val storyEventId = StoryEvent.Id().uuid
	val relativeSceneId = Scene.Id().uuid

	var createStoryEventRequest: CreateStoryEvent.RequestModel? = null
	var createStoryEventResult: Any? = null
	var savedScene: Scene? = null
	var result: Any? = null

	@Nested
	inner class `Create Scene with just a name` {

		fun whenUseCaseIsExecuted(withName: String = "") {
			val request = CreateNewScene.RequestModel(withName, LocaleDouble())
			this@CreateNewSceneUnitTest.whenUseCaseIsExecuted(request)
		}

		@Test
		fun `name cannot be blank`() {
			whenUseCaseIsExecuted()
			val result = result as SceneNameCannotBeBlank
		}

		@Test
		fun `create story event`() {
			whenUseCaseIsExecuted(validSceneName)
			assertStoryEventCreated()
			assertStoryEventOutputNotified()
			assertSceneCreatedWithStoryEventId()
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

		fun whenUseCaseIsExecuted(withName: String = "") {
			val request = CreateNewScene.RequestModel(withName, storyEventId, LocaleDouble())
			this@CreateNewSceneUnitTest.whenUseCaseIsExecuted(request)
		}

		@Test
		fun `name cannot be blank`() {
			whenUseCaseIsExecuted()
			val result = result as SceneNameCannotBeBlank
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

		fun whenUseCaseIsExecuted(withName: String = "") {
			val request = CreateNewScene.RequestModel(withName, relativeSceneId, true, LocaleDouble())
			this@CreateNewSceneUnitTest.whenUseCaseIsExecuted(request)
		}

		@Test
		fun `name cannot be blank`() {
			whenUseCaseIsExecuted()
			val result = result as SceneNameCannotBeBlank
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
				  StoryEvent(StoryEvent.Id(storyEventWithId), "", projectId, null, null, null, emptyList())
				)
			}
		}
		if (sceneWithId != null) {
			runBlocking {
				sceneRepository.createNewScene(Scene(Scene.Id(sceneWithId), projectId, "", StoryEvent.Id(storyEventId), null, listOf()),
				sceneRepository.getSceneIdsInOrder(projectId) + Scene.Id(sceneWithId)
				)
				repeat(numberOfScenesAfterRelativeScene) {
					val scene = Scene(projectId, "", StoryEvent.Id())
					sceneRepository.createNewScene(scene,
					  sceneRepository.getSceneIdsInOrder(projectId) + scene.id
					)
				}

				if (storyEventRepository.getStoryEventById(StoryEvent.Id(storyEventId)) == null) {
					storyEventRepository.addNewStoryEvent(
					  StoryEvent(StoryEvent.Id(storyEventId), "", projectId, null, null, null, emptyList())
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
		val useCase: CreateNewScene = CreateNewSceneUseCase(projectId.uuid, sceneRepository, storyEventRepository, object : CreateStoryEvent {
			override suspend fun invoke(request: CreateStoryEvent.RequestModel, output: CreateStoryEvent.OutputPort) {
				createStoryEventRequest = request
				val newStoryEvent = StoryEvent(request.name, request.projectId?.let(Project::Id) ?: projectId)
				storyEventRepository.addNewStoryEvent(newStoryEvent)
				output.receiveCreateStoryEventResponse(CreateStoryEvent.ResponseModel(
				  StoryEventItem(newStoryEvent.id.uuid, request.name, 0),
				  emptyList()
				))
			}
		})
		runBlocking {
			useCase.invoke(requestModel, object : CreateNewScene.OutputPort {
				override val createStoryEventOutputPort: CreateStoryEvent.OutputPort = object : CreateStoryEvent.OutputPort {
					override fun receiveCreateStoryEventResponse(response: CreateStoryEvent.ResponseModel) {
						createStoryEventResult = response
						storyEventOutputExecution.invoke()
					}

					override fun receiveCreateStoryEventFailure(failure: StoryEventException) {
						createStoryEventResult = failure
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
		val actual = createStoryEventResult as CreateStoryEvent.ResponseModel
	}

	private fun assertStoryEventCreated() {
		val createStoryEventRequest = createStoryEventRequest!!
		assertEquals(validSceneName, createStoryEventRequest.name)
		if (createStoryEventRequest.projectId != null) {
			assertEquals(projectId.uuid, createStoryEventRequest.projectId)
		}
	}

	private fun assertCreatedSceneIsBeforeRelativeScene() {
		val savedScene = savedScene!!
		runBlocking {
			assertEquals(savedScene.id, sceneRepository.getSceneIdsInOrder(projectId).first())
		}
	}

	private fun assertCreatedStoryEventIsBeforeRelativeSceneStoryEvent() {
		val createStoryEventRequest = createStoryEventRequest!!
		assertEquals(storyEventId, createStoryEventRequest.relativeStoryEventId)
		assertTrue(createStoryEventRequest.before)
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
		assertEquals((createStoryEventResult as CreateStoryEvent.ResponseModel).storyEventId, savedScene.storyEventId.uuid)
	}

	private fun assertValidResponseModel(actual: Any?): CreateNewScene.ResponseModel
	{
		val savedScene = savedScene!!
		actual as CreateNewScene.ResponseModel
		assertEquals(savedScene.id.uuid, actual.sceneId)
		assertEquals(validSceneName, actual.sceneName)
		return actual
	}

}