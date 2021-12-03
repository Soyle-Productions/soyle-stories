package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.scene.listAllScenes.ListAllScenes
import com.soyle.stories.usecase.scene.listAllScenes.ListAllScenesUseCase
import com.soyle.stories.usecase.scene.listAllScenes.SceneItem
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

class ListAllScenesUnitTest {

	private val projectId = Project.Id(UUID.randomUUID())
	private val sceneIds: List<UUID> = List(5) { UUID.randomUUID() }

	private var storedScenes: List<Scene> = emptyList()
	private lateinit var sceneRepository: SceneRepository

	private var result: Any? = null

	@BeforeEach
	fun clear() {
		storedScenes = emptyList()
		sceneRepository = SceneRepositoryDouble()
		result = null
	}

	@Test
	fun `no scenes`() {
		givenNoScenes()
		whenUseCaseIsExecuted()
		assertOutputIsEmpty()
	}

	@Test
	fun `some scenes`() {
		given(sceneIds = sceneIds)
		whenUseCaseIsExecuted()
		assertResultIsValidResponseModel()
	}

	private fun givenNoScenes() = given()
	private fun given(sceneIds: List<UUID> = emptyList()) {
		storedScenes = sceneIds.map {
			makeScene(Scene.Id(it), projectId)
		}
		sceneRepository = SceneRepositoryDouble(
		  initialScenes = storedScenes
		)
	}

	private fun whenUseCaseIsExecuted() {
		val useCase: ListAllScenes = ListAllScenesUseCase(projectId.uuid, sceneRepository)
		runBlocking {
			useCase.invoke(object : ListAllScenes.OutputPort {
				override suspend fun receiveListAllScenesResponse(response: ListAllScenes.ResponseModel) {
					result = response
				}
			})
		}
	}
	private fun assertOutputIsEmpty() {
		val result = result as ListAllScenes.ResponseModel
		result.scenes.isEmpty().mustEqual(true) { "Output scenes is not empty" }
	}
	private fun assertResultIsValidResponseModel() {
		val result = result as ListAllScenes.ResponseModel
		result.scenes.map(SceneItem::id).toSet().mustEqual(sceneIds.toSet()) { "Not all scenes output" }
		result.scenes.map(SceneItem::sceneName).toSet()
		  .mustEqual(storedScenes.map(Scene::name).map(NonBlankString::value).toSet()) { "Scene names do not match" }
		result.scenes.map(SceneItem::proseId).toSet().mustEqual(storedScenes.map(Scene::proseId).toSet())
	}
}