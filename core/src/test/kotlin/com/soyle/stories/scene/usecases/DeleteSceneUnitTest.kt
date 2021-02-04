package com.soyle.stories.scene.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.nonBlankStr
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.scene.SceneDoesNotExist
import com.soyle.stories.scene.SceneException
import com.soyle.stories.scene.doubles.LocaleDouble
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.makeScene
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.usecases.deleteScene.DeleteScene
import com.soyle.stories.scene.usecases.deleteScene.DeleteSceneUseCase
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
			useCase.invoke(sceneId, LocaleDouble(), object : DeleteScene.OutputPort {
				override fun receiveDeleteSceneFailure(failure: SceneException) {
					result = failure
				}

				override fun receiveDeleteSceneResponse(responseModel: DeleteScene.ResponseModel) {
					result = responseModel
				}
			})
		}
	}
}