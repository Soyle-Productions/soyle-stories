package com.soyle.stories.scene.usecases

import com.soyle.stories.common.mustEqual
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Scene
import com.soyle.stories.entities.StoryEvent
import com.soyle.stories.scene.doubles.SceneRepositoryDouble
import com.soyle.stories.scene.repositories.SceneRepository
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenes
import com.soyle.stories.scene.usecases.listAllScenes.ListAllScenesUseCase
import com.soyle.stories.scene.usecases.listAllScenes.SceneItem
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
			Scene(Scene.Id(it), projectId, "Unique Scene Name: $it", StoryEvent.Id(), mapOf())
		}
		sceneRepository = SceneRepositoryDouble(
		  initialScenes = storedScenes
		)
	}

	private fun whenUseCaseIsExecuted() {
		val useCase: ListAllScenes = ListAllScenesUseCase(projectId.uuid, sceneRepository)
		runBlocking {
			useCase.invoke(object : ListAllScenes.OutputPort {
				override fun receiveListAllScenesResponse(response: ListAllScenes.ResponseModel) {
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
		  .mustEqual(storedScenes.map(Scene::name).toSet()) { "Scene names do not match" }
	}
}