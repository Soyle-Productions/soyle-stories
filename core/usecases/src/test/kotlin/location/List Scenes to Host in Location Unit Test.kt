package com.soyle.stories.usecase.location

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.location.hostedScene.listAvailableScenes.ListScenesToHostInLocation
import com.soyle.stories.usecase.location.hostedScene.listAvailableScenes.ListScenesToHostInLocationUseCase
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `List Scenes to Host in Location Unit Test` {

    private val locationRepository = LocationRepositoryDouble()
    private val sceneRepository = SceneRepositoryDouble()

    private val listScenesToHostInLocation: ListScenesToHostInLocation =
        ListScenesToHostInLocationUseCase(locationRepository, sceneRepository)
    private var result: ListScenesToHostInLocation.ResponseModel? = null

    @Test
    fun `should throw location doesn't exist error`() {
        val locationId = Location.Id()
        val error = assertThrows<LocationDoesNotExist> {
            runBlocking { listScenesToHostInLocation(locationId, output()) }
        }
        error.locationId.mustEqual(locationId.uuid)
    }

    @Nested
    inner class `Given Location Exists` {

        private val location = makeLocation()
        init {
            locationRepository.givenLocation(location)
        }

        @AfterEach
        fun `should output location id`() {
            result!!.locationId.mustEqual(location.id)
        }

        @Test
        fun `should output no scenes`() {
            runBlocking { listScenesToHostInLocation(location.id, output()) }
            result!!.availableScenesToHost.size.mustEqual(0)
        }

        @Nested
        inner class `Given Scenes in Project` {

            private val scenesInProject = List(8) { makeScene(projectId = location.projectId) }
            init {
                scenesInProject.forEach(sceneRepository::givenScene)
                repeat(5) { sceneRepository.givenScene(makeScene()) }
            }

            @Test
            fun `should output all scenes in project`() {
                runBlocking { listScenesToHostInLocation(location.id, output()) }
                result!!.availableScenesToHost.size.mustEqual(scenesInProject.size)
                result!!.availableScenesToHost.map { it.sceneId }.toSet()
                    .mustEqual(scenesInProject.map { it.id }.toSet())
                result!!.availableScenesToHost.forEach { availableSceneToHost ->
                    val associatedScene = scenesInProject.find { it.id == availableSceneToHost.sceneId }!!
                    availableSceneToHost.sceneName.mustEqual(associatedScene.name.value)
                }
            }

            @Nested
            inner class `Given some scenes already hosted` {

                private val hostedScenes = scenesInProject.shuffled().take(3)
                init {
                    hostedScenes.fold(location) { a, b -> a.withSceneHosted(b.id, b.name.value).location }
                        .let(locationRepository::givenLocation)
                }

                @Test
                fun `should not output hosted scenes`() {
                    runBlocking { listScenesToHostInLocation(location.id, output()) }
                    result!!.availableScenesToHost.size.mustEqual(5)
                    result!!.availableScenesToHost.map { it.sceneId }.toSet()
                        .mustEqual(scenesInProject.map { it.id }.toSet() - hostedScenes.map { it.id })
                }
            }
        }
    }

    private fun output() = ListScenesToHostInLocation.OutputPort {
        result = it
    }
}
