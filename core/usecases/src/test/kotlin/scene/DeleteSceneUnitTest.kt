package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocaleDouble
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.deleteScene.DeleteScene
import com.soyle.stories.usecase.scene.deleteScene.DeleteSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class DeleteSceneUnitTest {

	private val scene = makeScene()

	private var deletedScene: Scene? = null
	private val updatedLocations: List<Location>
	private var result: Any? = null

	private val sceneRepository = SceneRepositoryDouble(onRemoveScene = ::deletedScene::set)
	private val locationRepository: LocationRepositoryDouble
	init {
	    val locationsList = mutableListOf<Location>()
		locationRepository = LocationRepositoryDouble(onUpdateLocation = locationsList::add)
		updatedLocations = locationsList
	}

	@Test
	fun `scene does not exist`() {
		whenUseCaseIsExecuted()
		val result = result as SceneDoesNotExist
		result.sceneId.mustEqual(scene.id.uuid)
	}

	@Test
	fun `existing scene is deleted`() {
		sceneRepository.givenScene(scene)
		whenUseCaseIsExecuted()
		val deletedScene = deletedScene!!
		deletedScene.id.uuid.mustEqual(scene.id.uuid)
		val result = result as DeleteScene.ResponseModel
		result.sceneId.mustEqual(scene.id.uuid)
		assertTrue(updatedLocations.isEmpty())
	}

	@Nested
	inner class `Given Locations are Linked`
	{

		private val unlinkedLocations = List(3) { makeLocation() }
		private val locations = List(5) { makeLocation().withSceneHosted(scene.id, scene.name.value).location }

		init {
			unlinkedLocations.forEach(locationRepository::givenLocation)
			locations.forEach(locationRepository::givenLocation)
			locations.fold(scene) { nextScene, location -> nextScene.withLocationLinked(location).scene }
				.let(sceneRepository::givenScene)
		}

		@Test
		fun `should update locations to remove hosted scene`() {
			whenUseCaseIsExecuted()
			updatedLocations.map { it.id }.toSet().mustEqual(locations.map { it.id }.toSet())
			updatedLocations.forEach {
				it.hostedScenes.containsEntityWithId(scene.id).mustEqual(false)
			}
		}

		@Test
		fun `should output hosted scenes removed events`() {
			whenUseCaseIsExecuted()
			with (result as DeleteScene.ResponseModel) {
				hostedScenesRemoved.map { it.locationId }.toSet().mustEqual(locations.map { it.id }.toSet())
				hostedScenesRemoved.forEach { it.sceneId.mustEqual(scene.id) }
			}
		}

	}

	private fun whenUseCaseIsExecuted() {
		val useCase: DeleteScene = DeleteSceneUseCase(sceneRepository, locationRepository)
		runBlocking {
			useCase.invoke(scene.id.uuid, SceneLocaleDouble(), object : DeleteScene.OutputPort {
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
