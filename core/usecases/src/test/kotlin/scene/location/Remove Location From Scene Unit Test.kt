package com.soyle.stories.usecase.scene.location

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.events.LocationRemovedFromScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneDoesNotUseLocation
import com.soyle.stories.usecase.scene.location.removeLocationFromScene.RemoveLocationFromScene
import com.soyle.stories.usecase.scene.location.removeLocationFromScene.RemoveLocationFromSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Remove Location From Scene Unit Test` {

    private val scene = makeScene()
    private val location = makeLocation()

    private var updatedScene: Scene? = null
    private var updatedLocation: Location? = null
    private var result: RemoveLocationFromScene.ResponseModel? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)
    private val locationRepository = LocationRepositoryDouble(onUpdateLocation = ::updatedLocation::set)

    @Test
    fun `scene does not exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            removeLocationFromScene()
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given scene exists` {

        init {
            sceneRepository.givenScene(scene.withLocationLinked(location).scene)
        }

        @Test
        fun `should remove location from scene`() {
            removeLocationFromScene()
            assertFalse(updatedScene!!.contains(location.id))
        }

        @Test
        fun `should output location removed from scene event`() {
            removeLocationFromScene()
            with(result ?: throw AssertionError("No response received")) {
                locationRemovedFromScene.mustEqual(
                    LocationRemovedFromScene(scene.id, SceneSettingLocation(location))
                )
            }
        }

        @Test
        fun `should not output hosted scene removed event`() {
            removeLocationFromScene()
            with(result ?: throw AssertionError("No response received")) {
                assertNull(hostedSceneRemoved)
            }
        }

        @Nested
        inner class `Given scene uses location` {

            init {
                locationRepository.givenLocation(location.withSceneHosted(scene.id, scene.name.value).location)
            }

            @Test
            fun `should remove location from scene`() {
                removeLocationFromScene()
                assertFalse(updatedScene!!.contains(location.id))
            }

            @Test
            fun `should output location removed from scene event`() {
                removeLocationFromScene()
                with(result ?: throw AssertionError("No response received")) {
                    locationRemovedFromScene.mustEqual(
                        LocationRemovedFromScene(scene.id, SceneSettingLocation(location))
                    )
                }
            }

            @Test
            fun `should remove hosted scene from location`() {
                removeLocationFromScene()
                updatedLocation!!.id.mustEqual(location.id)
                updatedLocation!!.hostedScenes.getEntityById(scene.id).let(::assertNull)
            }

            @Test
            fun `should output hosted scene removed event`() {
                removeLocationFromScene()
                with(result ?: throw AssertionError("No response received")) {
                    hostedSceneRemoved.mustEqual(
                        HostedSceneRemoved(location.id, scene.id)
                    )
                }
            }
        }
    }

    private fun removeLocationFromScene() {
        val useCase: RemoveLocationFromScene = RemoveLocationFromSceneUseCase(sceneRepository, locationRepository)
        val output = object : RemoveLocationFromScene.OutputPort {
            override suspend fun locationRemovedFromScene(response: RemoveLocationFromScene.ResponseModel) {
                result = response

            }
        }
        runBlocking {
            useCase.invoke(scene.id, location.id, output)
        }
    }
}
