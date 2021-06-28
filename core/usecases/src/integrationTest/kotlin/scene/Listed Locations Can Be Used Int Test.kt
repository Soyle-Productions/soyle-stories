package com.soyle.stories.usecase.scene

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.SceneLocaleDouble
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.location.listAllLocations.LocationItem
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToScene
import com.soyle.stories.usecase.scene.location.linkLocationToScene.LinkLocationToSceneUseCase
import com.soyle.stories.usecase.scene.location.listLocationsToUse.ListAvailableLocationsToUseInScene
import com.soyle.stories.usecase.scene.location.listLocationsToUse.ListAvailableLocationsToUseInSceneUseCase
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInScene
import com.soyle.stories.usecase.scene.location.listLocationsUsed.ListLocationsUsedInSceneUseCase
import com.soyle.stories.usecase.scene.location.removeLocationFromScene.RemoveLocationFromScene
import com.soyle.stories.usecase.scene.location.removeLocationFromScene.RemoveLocationFromSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class `Listed Locations Can Be Used Int Test` {

    private val scene = makeScene()
    private val location = makeLocation(projectId = scene.projectId)

    private val locationRepository = LocationRepositoryDouble()
    private val sceneRepository = SceneRepositoryDouble()

    private val sceneLocale = SceneLocaleDouble()

    init {
        sceneRepository.givenScene(scene)
        locationRepository.givenLocation(location)
        repeat(4) { locationRepository.givenLocation(makeLocation(projectId = scene.projectId)) }
    }

    @Test
    fun `can link a listed location`() {
        val availableLocations = listAvailableLocationsToUseInScene()
        linkLocationToScene(availableLocations.first().id)
    }

    @Test
    fun `used location should not be listed`()
    {
        linkLocationToScene(location.id)
        val availableLocations = listAvailableLocationsToUseInScene()
        assertNull(availableLocations.find {
            it.id == location.id
        })
    }

    @Test
    fun `linking a second location should not replace first`() {
        linkLocationToScene(location.id)
        val availableLocations = listAvailableLocationsToUseInScene()
        linkLocationToScene(availableLocations.first().id)
        sceneRepository.scenes.getValue(scene.id).settings.map { it.id }.toSet().mustEqual(setOf(location.id, availableLocations.first().id))
    }

    @Test
    fun `used locations should be listed in the scene`() {
        linkLocationToScene(location.id)
        val availableLocations = listAvailableLocationsToUseInScene()
        linkLocationToScene(availableLocations.first().id)
        val usedLocations = listLocationsUsedInScene()
        usedLocations.map { it.id }.toSet().mustEqual(setOf(location.id, availableLocations.first().id))
    }

    @Test
    fun `listed used locations can be removed from scene`() {
        linkLocationToScene(location.id)
        val availableLocations = listAvailableLocationsToUseInScene()
        linkLocationToScene(availableLocations.first().id)
        val usedLocations = listLocationsUsedInScene()
        removeLocationFromScene(usedLocations.first().id)
        sceneRepository.scenes.getValue(scene.id).settings.map { it.id }.toSet().mustEqual(usedLocations.drop(1).map { it.id }.toSet())
    }

    @Test
    fun `removed locations should be available again`() {
        linkLocationToScene(location.id)
        linkLocationToScene(listAvailableLocationsToUseInScene().first().id)
        removeLocationFromScene(location.id)
        val availableLocations = listAvailableLocationsToUseInScene()
        assertNotNull(availableLocations.find { it.id == location.id })
    }

    private fun linkLocationToScene(locationId: Location.Id): LinkLocationToScene.ResponseModel {
        val useCase: LinkLocationToScene = LinkLocationToSceneUseCase(sceneRepository, locationRepository)
        var result: LinkLocationToScene.ResponseModel? = null
        val output = object : LinkLocationToScene.OutputPort {
            override suspend fun locationLinkedToScene(response: LinkLocationToScene.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(LinkLocationToScene.RequestModel(scene.id, locationId, sceneLocale), output)
        }
        return result!!
    }

    private fun listAvailableLocationsToUseInScene(): ListAvailableLocationsToUseInScene.ResponseModel
    {
        val useCase: ListAvailableLocationsToUseInScene = ListAvailableLocationsToUseInSceneUseCase(sceneRepository, locationRepository)
        var result: ListAvailableLocationsToUseInScene.ResponseModel? = null
        val output = object : ListAvailableLocationsToUseInScene.OutputPort {
            override suspend fun receiveAvailableLocationsToUseInScene(response: ListAvailableLocationsToUseInScene.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id, output)
        }
        return result!!
    }

    private fun listLocationsUsedInScene(): List<LocationItem>
    {
        val useCase: ListLocationsUsedInScene = ListLocationsUsedInSceneUseCase(sceneRepository)
        var result: ListLocationsUsedInScene.ResponseModel? = null
        val output = object : ListLocationsUsedInScene.OutputPort {
            override suspend fun receiveLocationsUsedInScene(response: ListLocationsUsedInScene.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(scene.id, output)
        }
        return result!!
    }

    private fun removeLocationFromScene(locationId: Location.Id) {
        val useCase: RemoveLocationFromScene = RemoveLocationFromSceneUseCase(sceneRepository)
        val output = object : RemoveLocationFromScene.OutputPort {
            override suspend fun locationRemovedFromScene(response: RemoveLocationFromScene.ResponseModel) {}
        }
        runBlocking {
            useCase.invoke(scene.id, locationId, output)
        }
    }

}