package com.soyle.stories.scene.usecases

import com.soyle.stories.entities.Scene
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.SceneNameCannotBeBlank
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.usecases.createNewScene.CreateNewScene
import com.soyle.stories.scene.usecases.createNewScene.CreateNewSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CreateNewSceneUnitTest {

	val validSceneName = "Valid Scene Name"

	var savedScene: Scene? = null
	var result: Any? = null

	@Test
	fun `name cannot be blank`() {
		whenUseCaseIsExecuted()
		val result = result as SceneNameCannotBeBlank
	}

	@Test
	fun `name is not blank`() {
		whenUseCaseIsExecuted(withName = validSceneName)
		assertSceneSavedCorrectly()
		assertValidResponseModel(result)
	}

	private fun whenUseCaseIsExecuted(withName: String = "")
	{
		val useCase: CreateNewScene = CreateNewSceneUseCase(object : SceneRepository {
			override suspend fun createNewScene(scene: Scene) {
				savedScene = scene
			}
		})
		runBlocking {
			useCase.invoke(withName, object : CreateNewScene.OutputPort {
				override fun receiveCreateNewSceneFailure(failure: SceneException) {
					result = failure
				}

				override fun receiveCreateNewSceneResponse(response: CreateNewScene.ResponseModel) {
					result = response
				}
			})
		}
	}

	private fun assertSceneSavedCorrectly()
	{
		val savedScene = savedScene!!
		assertEquals(validSceneName, savedScene.name)
	}

	private fun assertValidResponseModel(actual: Any?)
	{
		val savedScene = savedScene!!
		actual as CreateNewScene.ResponseModel
		assertEquals(savedScene.id.uuid, actual.sceneId)
		assertEquals(validSceneName, actual.sceneName)
	}

}