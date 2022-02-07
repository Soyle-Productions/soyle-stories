package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events.HostedSceneRenamed
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.*
import com.soyle.stories.domain.scene.events.SceneRenamed
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.renameScene.RenameScene
import com.soyle.stories.usecase.scene.renameScene.RenameSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class RenameSceneUnitTest {

    private val scene = makeScene()
    private val inputName = sceneName()

	private var updatedScene: Scene? = null
	private val updatedLocations: List<Location>
    private var result: Result<RenameScene.ResponseModel>? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)
	private val locationRepository: LocationRepositoryDouble
	init {
		val mutableUpdatedLocations = mutableListOf<Location>()
		locationRepository = LocationRepositoryDouble(onUpdateLocation = mutableUpdatedLocations::add)
		updatedLocations = mutableUpdatedLocations
	}

	private val renameScene: RenameScene = RenameSceneUseCase(sceneRepository, locationRepository)

    @Test
    fun `scene does not exist`() = runBlocking {
		renameScene(requestModel(), output())
        val result = result!!.exceptionOrNull() as SceneDoesNotExist
        result.sceneId.mustEqual(scene.id.uuid)
    }

	@Nested
	inner class `Given Scene Exists` {

		init {
			sceneRepository.givenScene(scene)
		}

		@Test
		fun `should output scene renamed`() = runBlocking {
			renameScene(requestModel(), output())
			result!!.getOrNull().shouldBe(responseModel())
		}

		@Test
		fun `should update scene with new name`() = runBlocking {
			renameScene(requestModel(), output())
			updatedScene!!.name.mustEqual(inputName)
		}

		@Nested
		inner class `Given Locations Host the Scene` {

			private val locations = List(5) { makeLocation().withSceneHosted(scene.id, scene.name.value).location }

			init {
			    locations.forEach(locationRepository::givenLocation)
				sceneRepository.givenScene(locations.fold(scene) { a, b -> a.withLocationLinked(b).scene })
			}

			@Test
			fun `should update locations`() = runBlocking {
				renameScene(requestModel(), output())
				updatedLocations.size.mustEqual(locations.size)
				updatedLocations.map { it.id }.toSet().mustEqual(locations.map { it.id }.toSet())
				updatedLocations.forEach { it.hostedScenes.getEntityById(scene.id)!!.sceneName.mustEqual(inputName.value) }
			}

			@Test
			fun `should output updated location events`() = runBlocking {
				renameScene(requestModel(), output())
				result!!.getOrNull().shouldBe(responseModel(
					expectedLocationEvents = locations.map { HostedSceneRenamed(it.id, scene.id, inputName.value) }
				))
			}

			@Nested
			inner class `Given Scene Already has Name` {

				@Test
				fun `should not update scene`() = runBlocking {
					renameScene(requestModel(withName = scene.name), output())
					result!!.getOrNull().shouldBe(responseModel(
						expectedName = scene.name.value,
						expectedSceneRenamedEvent = null
					))
					assertNull(updatedScene)
				}

				@Test
				fun `should not update locations`() = runBlocking {
					renameScene(requestModel(withName = scene.name), output())
					updatedLocations.mustEqual(emptyList<Location>())
				}

				@Test
				fun `should output scene id and requested name`() = runBlocking {
					renameScene(requestModel(withName = scene.name), output())
					result!!.getOrNull().shouldBe(responseModel(
						expectedName = scene.name.value,
						expectedSceneRenamedEvent = null,
						expectedLocationEvents = emptyList()
					))
				}

			}

		}

	}

    private fun requestModel(withName: NonBlankString = inputName) =
        RenameScene.RequestModel(scene.id.uuid, withName)

    private fun output() = object : RenameScene.OutputPort {
        override suspend fun receiveRenameSceneFailure(failure: Exception) {
            result = Result.failure(failure)
        }

        override suspend fun receiveRenameSceneResponse(response: RenameScene.ResponseModel) {
            result = Result.success(response)
        }
    }

	private fun responseModel(
		expectedName: String = inputName.value,
		expectedSceneRenamedEvent: SceneRenamed? = SceneRenamed(scene.id, inputName.value),
		expectedLocationEvents: List<HostedSceneRenamed> = emptyList()
	) = fun(actual: Any?) {
		actual as RenameScene.ResponseModel
		actual.sceneId.mustEqual(scene.id)
		actual.requestedName.mustEqual(expectedName)
		actual.sceneRenamed.mustEqual(expectedSceneRenamedEvent)
		actual.hostedScenesRenamed.mustEqual(expectedLocationEvents)
	}

}
