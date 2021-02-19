package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.locationsInScene.listLocationsToUse.ListAvailableLocationsToUseInScene
import com.soyle.stories.usecase.scene.locationsInScene.listLocationsToUse.ListAvailableLocationsToUseInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `List Available Locations To Use In Scene Unit Test` {

    private val scene = makeScene()

    private val sceneRepository = SceneRepositoryDouble()
    private val locationRepository = LocationRepositoryDouble()

    private var result: ListAvailableLocationsToUseInScene.ResponseModel? = null

    @Test
    fun `scene does not exist`() {
        val error = assertThrows<SceneDoesNotExist> {
            listAvailableLocations()
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given Scene Exists` {
        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `should output empty list`() {
            listAvailableLocations()
            assertTrue(result!!.isEmpty())
        }

        @Nested
        inner class `Given Locations` {

            private val locationsInProject = List(5) { makeLocation(projectId = scene.projectId) }

            init {
                repeat(8) { locationRepository.givenLocation(makeLocation()) }
                locationsInProject.forEach(locationRepository::givenLocation)
            }

            @Test
            fun `should only list locations in same project`() {
                listAvailableLocations()
                result!!.map { it.id }.toSet().mustEqual(locationsInProject.map { it.id }.toSet())
            }

            @Nested
            inner class `Given Location Linked`
            {

                private val location = locationsInProject.random()
                init {
                    sceneRepository.givenScene(scene.withLocationLinked(location.id))
                }

                @Test
                fun `should not list linked location`() {
                    listAvailableLocations()
                    assertNull(result!!.find { it.id == location.id })
                }

            }

        }

    }

    private fun listAvailableLocations() {
        val useCase: ListAvailableLocationsToUseInScene =
            ListAvailableLocationsToUseInSceneUseCase(sceneRepository, locationRepository)
        val output = object : ListAvailableLocationsToUseInScene.OutputPort {
            override suspend fun receiveAvailableLocationsToUseInScene(response: ListAvailableLocationsToUseInScene.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id, output)
        }
    }

}