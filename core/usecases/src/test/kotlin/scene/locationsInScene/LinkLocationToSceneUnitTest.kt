package com.soyle.stories.usecase.scene.locationsInScene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocaleDouble
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.validation.entitySetOfNotNull
import com.soyle.stories.usecase.location.LocationDoesNotExist
import com.soyle.stories.usecase.location.locationDoesNotExist
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.linkLocationToScene.LinkLocationToScene
import com.soyle.stories.usecase.scene.linkLocationToScene.LinkLocationToSceneUseCase
import com.soyle.stories.usecase.scene.sceneDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LinkLocationToSceneUnitTest {

    private val sceneId = Scene.Id()
    private val location = makeLocation()
    private val locationId = location.id

    private var updatedScene: Scene? = null
    private var result: Any? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)
    private val locationRepository = LocationRepositoryDouble()

    @Test
    fun `scene does not exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            whenLocationIsLinkedToScene()
        }
        error shouldBe sceneDoesNotExist(sceneId.uuid)
        assertNull(updatedScene)
        assertNull(result)
    }

    @Test
    fun `location does not exist`() {
        givenSceneExists()
        val error = assertThrows<LocationDoesNotExist> {
            whenLocationIsLinkedToScene()
        }
        error shouldBe locationDoesNotExist(locationId.uuid)
        assertNull(updatedScene)
        assertNull(result)
    }

    @Test
    fun `happy path`() {
        givenSceneExists()
        givenLocationExists()
        whenLocationIsLinkedToScene()
        with (updatedScene as Scene) {
            assertEquals(sceneId, id)
            assertEquals(locationId, settings.firstOrNull()?.id)
        }
        responseModel().invoke(result)
    }

    @Test
    fun `link second location`() {
        givenSceneExists(hasLinkedLocation = true)
        givenLocationExists()
        val secondLocation = makeLocation()
        locationRepository.givenLocation(secondLocation)
        whenLocationIsLinkedToScene(secondLocation)
        with (updatedScene as Scene) {
            assertEquals(sceneId, id)
            assertTrue(settings.containsEntityWithId(location.id))
            assertTrue(settings.containsEntityWithId(secondLocation.id))
        }
        responseModel(secondLocation.id).invoke(result)
    }

    @Test
    fun `linking the same location should not update scene`() {
        givenSceneExists(hasLinkedLocation = true)
        givenLocationExists()
        whenLocationIsLinkedToScene()
        assertNull(updatedScene)
        assertNull(result)
    }

    private fun givenSceneExists(hasLinkedLocation: Boolean = false) {
        sceneRepository.scenes[sceneId] = makeScene(
            sceneId,
            settings = entitySetOfNotNull(SceneSettingLocation(location).takeIf { hasLinkedLocation })
        )
    }

    private fun givenLocationExists() {
        locationRepository.locations[locationId] = makeLocation(id = locationId)
    }

    private fun whenLocationIsLinkedToScene(location: Location = this.location) {
        whenUseCaseIsExecuted(LinkLocationToScene.RequestModel(sceneId, location.id, SceneLocaleDouble()))
    }

    private fun whenUseCaseIsExecuted(requestModel: LinkLocationToScene.RequestModel) {
        val useCase: LinkLocationToScene = LinkLocationToSceneUseCase(sceneRepository, locationRepository)
        val output = object : LinkLocationToScene.OutputPort {
            override suspend fun locationLinkedToScene(response: LinkLocationToScene.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(requestModel, output)
        }
    }

    private fun responseModel(locationId: Location.Id = this.locationId): (Any?) -> Unit = { actual ->
        actual as LinkLocationToScene.ResponseModel
        assertEquals(sceneId, actual.locationUsedInScene.sceneId)

        assertEquals(locationId, actual.locationUsedInScene.sceneSetting.id)
    }

}