package com.soyle.stories.usecase.scene.location

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events.SceneHostedAtLocation
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneLocaleDouble
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.domain.scene.events.LocationUsedInScene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.validation.entitySetOfNotNull
import com.soyle.stories.usecase.location.LocationDoesNotExist
import com.soyle.stories.usecase.location.locationDoesNotExist
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToScene
import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToSceneUseCase
import com.soyle.stories.usecase.scene.sceneDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class LinkLocationToSceneUnitTest {

    private val scene = makeScene()
    private val location = makeLocation()

    private var updatedLocation: Location? = null
    private var updatedScene: Scene? = null
    private var result: LinkLocationToScene.ResponseModel? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)
    private val locationRepository = LocationRepositoryDouble(onUpdateLocation = ::updatedLocation::set)

    @Test
    fun `scene does not exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            linkLocationToScene()
        }
        error shouldBe sceneDoesNotExist(scene.id.uuid)
        assertNull(updatedLocation)
        assertNull(updatedScene)
        assertNull(result)
    }

    @Test
    fun `location does not exist`() {
        sceneRepository.givenScene(scene)
        val error = assertThrows<LocationDoesNotExist> {
            linkLocationToScene()
        }
        error shouldBe locationDoesNotExist(location.id.uuid)
        assertNull(updatedLocation)
        assertNull(updatedScene)
        assertNull(result)
    }

    @Nested
    inner class `Given Scene and Location Exist`
    {

        init {
            sceneRepository.givenScene(scene)
            locationRepository.givenLocation(location)
        }

        @Test
        fun `should update scene`() {
            linkLocationToScene()
            updatedScene!!.run {
                assertEquals(scene.id, id)
                assertEquals(location.id, settings.firstOrNull()?.id)
            }
        }

        @Test
        fun `should update location`() {
            linkLocationToScene()
            updatedLocation!!.id.mustEqual(location.id)
            updatedLocation!!.hostedScenes.containsEntityWithId(scene.id).mustEqual(true)
            updatedLocation!!.hostedScenes.getEntityById(scene.id)!!.sceneName.mustEqual(scene.name.value)
        }

        @Test
        fun `should output response`() {
            linkLocationToScene()
            result!!.run {
                locationUsedInScene.mustEqual(LocationUsedInScene(scene.id, SceneSettingLocation(location.id, location.name.value)))
                sceneHostedAtLocation.mustEqual(SceneHostedAtLocation(location.id, scene.id, scene.name.value))
            }
        }

        @Nested
        inner class `Given Location Already Linked`
        {

            init {
                sceneRepository.givenScene(scene.withLocationLinked(location).scene)
            }

            @Test
            fun `should not update scene`() {
                linkLocationToScene()
                assertNull(updatedScene)
            }

            @Test
            fun `should not update location`() {
                linkLocationToScene()
                assertNull(updatedLocation)
            }

            @Test
            fun `should not output result`() {
                linkLocationToScene()
                assertNull(result)
            }

        }

    }

    private fun linkLocationToScene() {
        val requestModel = LinkLocationToScene.RequestModel(scene.id, location.id)
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

}