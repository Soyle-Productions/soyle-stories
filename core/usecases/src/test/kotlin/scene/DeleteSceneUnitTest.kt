package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocaleDouble
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene
import com.soyle.stories.usecase.scene.deleteScene.DeleteSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import java.util.*

class DeleteSceneUnitTest {

	private val projectId = Project.Id(UUID.randomUUID())
	private val sceneId: UUID = UUID.randomUUID()

	private lateinit var repository: SceneRepository

	private var deletedScene: Scene? = null
	private var result: Any? = null

	@Test
	fun `scene does not exist`() {
		givenNoScenes()
		whenUseCaseIsExecuted()
		val result = result as SceneDoesNotExist
		result.sceneId.mustEqual(sceneId)
	}

	@Test
	fun `existing scene is deleted`() {
		given(sceneWithId = sceneId)
		whenUseCaseIsExecuted()
		val deletedScene = deletedScene!!
		deletedScene.id.uuid.mustEqual(sceneId)
		val result = result as DeleteScene.ResponseModel
		result.sceneId.mustEqual(sceneId)
	}

	private fun givenNoScenes() = given()
	private fun given(sceneWithId: UUID? = null) {
		repository = SceneRepositoryDouble(
		  initialScenes = listOfNotNull(
			sceneWithId?.let { makeScene(Scene.Id(it), projectId) }
		  ),
		  onRemoveScene = { deletedScene = it }
		)
	}

	private fun whenUseCaseIsExecuted() {
		val useCase: DeleteScene = DeleteSceneUseCase(repository)
		runBlocking {
			useCase.invoke(sceneId, SceneLocaleDouble(), object : DeleteScene.OutputPort {
				override fun receiveDeleteSceneFailure(failure: Exception) {
					result = failure
				}

				override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {
					result = responseModel
				}
			})
		}
	}
}