package com.soyle.stories.usecase.scene.locationsInScene

import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocaleDouble
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.validation.entitySetOfNotNull
import com.soyle.stories.usecase.location.locationDoesNotExist
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.linkLocationToScene.LinkLocationToScene
import com.soyle.stories.usecase.scene.linkLocationToScene.LinkLocationToSceneUseCase
import com.soyle.stories.usecase.scene.sceneDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class LinkLocationToSceneUnitTest {

    private val sceneId = Scene.Id()
    private val location = makeLocation()
    private val locationId = location.id

    private var updatedScene: Scene? = null
    private var result: Any? = null

    @Test
    fun `scene does not exist`() {
        whenLocationIsLinkedToScene()
        assertNull(updatedScene)
        sceneDoesNotExist(sceneId.uuid).invoke(result)
    }

    @Test
    fun `location does not exist`() {
        givenSceneExists()
        whenLocationIsLinkedToScene()
        assertNull(updatedScene)
        locationDoesNotExist(locationId.uuid).invoke(result)
    }

    @Test
    fun `happy path`() {
        givenSceneExists()
        givenLocationExists()
        whenLocationIsLinkedToScene()
        updatedScene().invoke(updatedScene)
        responseModel().invoke(result)
    }

    @Test
    fun `clear location`() {
        givenSceneExists(hasLinkedLocation = true)
        whenLocationIsClearedFromScene()
        updatedScene(clearedLocation = true).invoke(updatedScene)
        responseModel(clearedLocation = true).invoke(result)
    }

    @ParameterizedTest
    @ValueSource(booleans = [true, false])
    fun `same value`(hasLinkedLocation: Boolean) {
        givenSceneExists(hasLinkedLocation = hasLinkedLocation)
        givenLocationExists()
        if (hasLinkedLocation) whenLocationIsLinkedToScene()
        else whenLocationIsClearedFromScene()
        assertNull(updatedScene)
        responseModel(clearedLocation = !hasLinkedLocation).invoke(result)
    }

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = { updatedScene = it })
    private val locationRepository = LocationRepositoryDouble()

    private fun givenSceneExists(hasLinkedLocation: Boolean = false) {
        sceneRepository.scenes[sceneId] = makeScene(
            sceneId,
            settings = entitySetOfNotNull(SceneSettingLocation(location).takeIf { hasLinkedLocation })
        )
    }

    private fun givenLocationExists() {
        locationRepository.locations[locationId] = makeLocation(id = locationId)
    }

    private fun whenLocationIsClearedFromScene() {
        whenUseCaseIsExecuted(LinkLocationToScene.RequestModel(sceneId.uuid, null, SceneLocaleDouble()))
    }

    private fun whenLocationIsLinkedToScene() {
        whenUseCaseIsExecuted(LinkLocationToScene.RequestModel(sceneId.uuid, locationId.uuid, SceneLocaleDouble()))
    }

    private fun whenUseCaseIsExecuted(requestModel: LinkLocationToScene.RequestModel) {
        val useCase: LinkLocationToScene = LinkLocationToSceneUseCase(sceneRepository, locationRepository)
        val output = object : LinkLocationToScene.OutputPort {
            override fun failedToLinkLocationToScene(failure: Exception) {
                result = failure
            }

            override fun locationLinkedToScene(response: LinkLocationToScene.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(requestModel, output)
        }
    }

    private fun updatedScene(clearedLocation: Boolean = false): (Any?) -> Unit = { actual ->
        actual as Scene
        assertEquals(sceneId, actual.id)
        if (clearedLocation) assertNull(actual.settings.firstOrNull())
        else assertEquals(locationId, actual.settings.firstOrNull()?.id)
    }

    private fun responseModel(clearedLocation: Boolean = false): (Any?) -> Unit = { actual ->
        actual as LinkLocationToScene.ResponseModel
        assertEquals(sceneId.uuid, actual.sceneId)

        if (clearedLocation) assertNull(actual.locationId)
        else assertEquals(locationId.uuid, actual.locationId)
    }

}