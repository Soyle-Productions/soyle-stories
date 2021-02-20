package com.soyle.stories.usecase.scene.locationsInScene

import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.LocationRemovedFromScene
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneDoesNotUseLocation
import com.soyle.stories.usecase.scene.locationsInScene.removeLocationFromScene.RemoveLocationFromScene
import com.soyle.stories.usecase.scene.locationsInScene.removeLocationFromScene.RemoveLocationFromSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Remove Location From Scene Unit Test` {

    private val scene = makeScene()
    private val location = makeLocation()

    private var updatedScene: Scene? = null
    private var result: RemoveLocationFromScene.ResponseModel? = null

    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)

    @Test
    fun `scene does not exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            removeLocationFromScene()
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given scene exists` {
        init { sceneRepository.givenScene(scene) }

        @Test
        fun `should throw location not used error`() {
            val error = assertThrows<SceneDoesNotUseLocation> {
                removeLocationFromScene()
            }
            error.sceneId.mustEqual(scene.id)
            error.locationId.mustEqual(location.id)
        }

        @Nested
        inner class `Given scene uses location` {
            init {
                sceneRepository.givenScene(scene.withLocationLinked(location))
            }

            @Test
            fun `should remove location from scene`() {
                removeLocationFromScene()
                assertFalse(updatedScene!!.contains(location.id))
            }

            @Test
            fun `should output location removed from scene event`() {
                removeLocationFromScene()
                with (result ?: throw AssertionError("No response received")) {
                    locationRemovedFromScene.mustEqual(LocationRemovedFromScene(scene.id, SceneSettingLocation(location)))
                }
            }
        }
    }

    private fun removeLocationFromScene() {
        val useCase: RemoveLocationFromScene = RemoveLocationFromSceneUseCase(sceneRepository)
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