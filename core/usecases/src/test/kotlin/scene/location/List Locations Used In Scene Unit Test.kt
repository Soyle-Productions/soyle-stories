package com.soyle.stories.usecase.scene.location

import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInScene
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `List Locations Used In Scene Unit Test` {

    private val scene = makeScene()

    private val sceneRepository = SceneRepositoryDouble()

    private var result: ListLocationsUsedInScene.ResponseModel? = null

    @Test
    fun `scene does not exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            listLocationsUsedInScene()
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given scene exists` {
        init { sceneRepository.givenScene(scene) }

        @Test
        fun `should output empty list`() {
            listLocationsUsedInScene()
            assertTrue(result!!.isEmpty())
        }

        @Nested
        inner class `Given scene has locations` {
            private val linkedLocations = List(5) { makeLocation() }

            init {
                linkedLocations.fold(scene) { nextScene, location -> nextScene.withLocationLinked(location).scene }
                    .let(sceneRepository::givenScene)
            }

            @Test
            fun `should output linked locations`() {
                listLocationsUsedInScene()
                result!!.map { it.id }.toSet().mustEqual(linkedLocations.map { it.id }.toSet())
                result!!.map { it.locationName }.toSet().mustEqual(linkedLocations.map { it.name.value }.toSet())
            }
        }
    }

    private fun listLocationsUsedInScene() {
        val useCase: ListLocationsUsedInScene = ListLocationsUsedInSceneUseCase(sceneRepository)
        val output = object : ListLocationsUsedInScene.OutputPort {
            override suspend fun receiveLocationsUsedInScene(response: ListLocationsUsedInScene.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id, output)
        }
    }

}