package com.soyle.stories.usecase.location

import com.soyle.stories.domain.location.locationName
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.scene.sceneName
import com.soyle.stories.domain.str
import com.soyle.stories.usecase.location.getLocationDetails.GetLocationDetails
import com.soyle.stories.usecase.location.getLocationDetails.GetLocationDetailsUseCase
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class GetLocationDetailsUnitTest {

    val location = makeLocation(description = "Description ${str()}")

    private val locationRepository = LocationRepositoryDouble()
    private val getLocationDetails: GetLocationDetails = GetLocationDetailsUseCase(locationRepository)

    private var result: Result<GetLocationDetails.ResponseModel>? = null

    @Test
    fun `location does not exist`() {
        val result = assertThrows<LocationDoesNotExist> {
            runBlocking {
                getLocationDetails(location.id.uuid, output())
            }
        }
        assertEquals(location.id.uuid, result.locationId)
    }

    @Test
    fun `location exists`() = runBlocking {
        locationRepository.givenLocation(location)
        getLocationDetails(location.id.uuid, output())
        val result = result!!.getOrThrow()
        assertEquals(location.id.uuid, result.locationId)
        assertEquals(location.name.value, result.locationName)
        assertEquals(location.description, result.locationDescription)
    }

    @Nested
    inner class `Given Scenes are Hosted` {

        private val location = this@GetLocationDetailsUnitTest.location
            .withSceneHosted(Scene.Id(), sceneName().value).location
            .withSceneHosted(Scene.Id(), sceneName().value).location
            .withSceneHosted(Scene.Id(), sceneName().value).location

        init {
            locationRepository.givenLocation(location)
        }

        @Test
        fun `should output scene items`() = runBlocking {
            getLocationDetails(location.id.uuid, output())
            with(result!!.getOrThrow()) {
                hostedScenes.size.mustEqual(location.hostedScenes.size)
                hostedScenes.map { it.sceneId }.toSet().mustEqual(location.hostedScenes.map { it.id }.toSet())
                hostedScenes.forEach {
                    it.sceneName.mustEqual(location.hostedScenes.getEntityById(it.sceneId)!!.sceneName)
                }
            }
        }
    }

    private fun output() = object : GetLocationDetails.OutputPort {
        override suspend fun receiveGetLocationDetailsResponse(response: GetLocationDetails.ResponseModel) {
            result = Result.success(response)
        }
    }
}
