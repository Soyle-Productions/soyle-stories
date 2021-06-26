package com.soyle.stories.usecase.scene.location

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.events.HostedSceneRemoved
import com.soyle.stories.domain.location.events.SceneHostedAtLocation
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingCannotBeReplacedBySameLocation
import com.soyle.stories.domain.scene.events.LocationRemovedFromScene
import com.soyle.stories.domain.scene.events.LocationUsedInScene
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.location.LocationDoesNotExist
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.SceneDoesNotUseLocation
import com.soyle.stories.usecase.scene.location.replace.ReplaceSettingInScene
import com.soyle.stories.usecase.scene.location.replace.ReplaceSettingInSceneUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Replace Setting in Scene Unit Test` {

    private val sceneId = Scene.Id()
    private val locationId = Location.Id()
    private val idOfReplacementLocation = Location.Id()
    private val request = ReplaceSettingInScene.RequestModel(sceneId, locationId, idOfReplacementLocation)

    private var updatedScene: Scene? = null
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = ::updatedScene::set)

    private val updatedLocations = mutableListOf<Location>()
    private val locationRepository = LocationRepositoryDouble(onUpdateLocation = updatedLocations::add)

    private var locationRemovedFromScene: LocationRemovedFromScene? = null
    private var sceneHostedAtLocation: SceneHostedAtLocation? = null
    private var hostedSceneRemoved: HostedSceneRemoved? = null
    private fun output() = object : ReplaceSettingInScene.OutputPort {
        override suspend fun replaceSettingInSceneResponse(
            response: ReplaceSettingInScene.ResponseModel
        ) {
            locationRemovedFromScene = response.locationRemovedFromScene
            sceneHostedAtLocation = response.sceneHostedAtLocation
            hostedSceneRemoved = response.hostedSceneRemoved
        }
    }

    private val replaceSettingInScene: ReplaceSettingInScene = ReplaceSettingInSceneUseCase(sceneRepository, locationRepository)

    @Test
    fun `should throw error given scene does not exist`() = runBlocking {
        val action = suspend { replaceSettingInScene(request, output()) }
        // then
        val error = assertThrows<SceneDoesNotExist> { action() }
        error.sceneId.mustEqual(sceneId.uuid)
    }

    @Nested
    inner class `Given Scene Exists`
    {

        private val scene = makeScene(sceneId = sceneId)

        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `should throw error given scene does not contain setting`() = runBlocking {
            val action = suspend { replaceSettingInScene(request, output()) }
            // then
            val error = assertThrows<SceneDoesNotUseLocation> { action() }
            error.sceneId.mustEqual(sceneId)
            error.locationId.mustEqual(locationId)
        }

        @Nested
        inner class `Given Scene used Location`
        {

            init {
                sceneRepository.givenScene(scene.withLocationLinked(locationId, "").scene)
            }

            @Test
            fun `should throw error given replacement location doesn't exist`() = runBlocking {
                val action = suspend { replaceSettingInScene(request, output()) }
                // then
                val error = assertThrows<LocationDoesNotExist> { action() }
                error.locationId.mustEqual(idOfReplacementLocation.uuid)
            }

            @Nested
            inner class `Given Replacement Location Exists`
            {

                private val replacementLocation = makeLocation(idOfReplacementLocation)

                init {
                    locationRepository.givenLocation(replacementLocation)
                }

                @Test
                fun `should throw error given location ids are identical`() = runBlocking {
                    // given
                    locationRepository.givenLocation(makeLocation(locationId))

                    val request = ReplaceSettingInScene.RequestModel(sceneId, locationId, locationId)
                    val action = suspend { replaceSettingInScene(request, output()) }
                    // then
                    val error = assertThrows<SceneSettingCannotBeReplacedBySameLocation> { action() }
                    error.sceneId.mustEqual(sceneId)
                    error.locationId.mustEqual(locationId)
                }

                @Test
                fun `should replace location in scene`() = runBlocking<Unit> {
                    replaceSettingInScene(request, output())
                    // then
                    updatedScene!!.contains(locationId).mustEqual(false)
                    updatedScene!!.contains(idOfReplacementLocation).mustEqual(true)
                }

                @Test
                fun `should host scene in replacement location`() = runBlocking<Unit> {
                    replaceSettingInScene(request, output())
                    // then
                    val updatedReplacementLocation = updatedLocations.single { it.id == idOfReplacementLocation }
                    updatedReplacementLocation.hostedScenes.containsEntityWithId(sceneId).mustEqual(true)
                    updatedReplacementLocation.hostedScenes.getEntityById(sceneId)!!.sceneName.mustEqual(scene.name.value)
                }

                @Test
                fun `should output removed location event with added location event`() = runBlocking<Unit> {
                    replaceSettingInScene(request, output())
                    // then
                    locationRemovedFromScene!!.mustEqual(
                        LocationRemovedFromScene(sceneId, locationId, LocationUsedInScene(sceneId, replacementLocation.id, replacementLocation.name.value))
                    )
                }

                @Test
                fun `should output hosted scene event`() = runBlocking<Unit> {
                    replaceSettingInScene(request, output())
                    // then
                    sceneHostedAtLocation!!.mustEqual(
                        SceneHostedAtLocation(replacementLocation.id, scene.id, scene.name.value)
                    )
                }

                @Nested
                inner class `Given Current Location Still Exists`
                {

                    init {
                        locationRepository.givenLocation(makeLocation(locationId).withSceneHosted(scene.id, scene.name.value).location)
                    }

                    @Test
                    fun `should remove hosted scene from location`() = runBlocking<Unit> {
                        replaceSettingInScene(request, output())
                        // then
                        val updatedReplacementLocation = updatedLocations.single { it.id == locationId }
                        updatedReplacementLocation.hostedScenes.containsEntityWithId(sceneId).mustEqual(false)
                    }

                    @Test
                    fun `should output hosted scene removed event`() = runBlocking<Unit> {
                        replaceSettingInScene(request, output())
                        // then
                        hostedSceneRemoved!!.mustEqual(
                            HostedSceneRemoved(locationId, scene.id)
                        )
                    }

                }

            }

        }

    }

}