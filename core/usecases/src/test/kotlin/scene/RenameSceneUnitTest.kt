package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocale
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.renameScene.RenameScene
import com.soyle.stories.usecase.scene.renameScene.RenameSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class RenameSceneUnitTest {

	private val projectId = Project.Id(UUID.randomUUID())
	private val sceneId: UUID = UUID.randomUUID()
	private val originalName: NonBlankString = NonBlankString.create("First Scene Name")!!
	private val inputName: NonBlankString = NonBlankString.create("Scene Name")!!
	private val sceneDoesNotExistMessage = "Scene does not exist"
	private val sceneNameCannotBeBlankMessage = "Scene name cannot be blank"

	private lateinit var sceneRepository: SceneRepository

	private var updatedScene: Scene? = null
	private var result: Any? = null

	@BeforeEach
	fun clear() {
		sceneRepository = SceneRepositoryDouble()
		updatedScene = null
		result = null
	}

	@Test
	fun `scene does not exist`() {
		givenNoScenes()
		whenUseCaseIsExecuted()
		val result = result as SceneDoesNotExist
		result.sceneId.mustEqual(sceneId)
		assertEquals(sceneDoesNotExistMessage, result.localizedMessage)
	}

	@Test
	fun `valid name is output`() {
		given(sceneWithId = sceneId)
		whenUseCaseIsExecuted()
		assertResultIsValidResponseModel()
	}

	@Test
	fun `same name is not persisted`() {
		given(sceneWithId = sceneId, andName = inputName)
		whenUseCaseIsExecuted()
		assertResultIsValidResponseModel()
		assertLocationNotUpdated()
	}

	@Test
	fun `modified valid name is persisted`() {
		given(sceneWithId = sceneId)
		whenUseCaseIsExecuted()
		assertResultIsValidResponseModel()
		assertOnlyLocationNameUpdated()
	}

	private fun givenNoScenes() = given()
	private fun given(sceneWithId: UUID? = null, andName: NonBlankString? = null) {
		sceneRepository = SceneRepositoryDouble(
		  initialScenes = listOfNotNull(
			sceneWithId?.let { makeScene(Scene.Id(it), projectId, andName ?: originalName) }
		  ),
		  onUpdateScene = { updatedScene = it }
		)
	}

	private fun whenUseCaseIsExecuted(withName: NonBlankString = inputName) {
		val useCase: RenameScene = RenameSceneUseCase(sceneRepository)
		val request = RenameScene.RequestModel(sceneId, withName, object : SceneLocale {
			override val sceneNameCannotBeBlank: String = sceneNameCannotBeBlankMessage
			override val sceneDoesNotExist: String = sceneDoesNotExistMessage
		})
		runBlocking {
			useCase.invoke(request, object : RenameScene.OutputPort {
				override fun receiveRenameSceneFailure(failure: Exception) {
					result = failure
				}

				override fun receiveRenameSceneResponse(response: RenameScene.ResponseModel) {
					result = response
				}
			})
		}
	}

	private fun assertResultIsValidResponseModel() {
		val result = result as RenameScene.ResponseModel
		result.sceneId.mustEqual(sceneId)
		result.newName.mustEqual(inputName.value)
	}

	private fun assertLocationNotUpdated() {
		val updatedScene = updatedScene
		updatedScene.mustEqual(null) { "Scene should not have been updated" }
	}

	private fun assertOnlyLocationNameUpdated() {
		val updatedScene = updatedScene!!
		updatedScene.name.mustEqual(inputName)
	}

}