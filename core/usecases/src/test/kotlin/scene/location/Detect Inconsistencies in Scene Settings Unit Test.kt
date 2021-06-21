package com.soyle.stories.usecase.scene.location

import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import com.soyle.stories.usecase.scene.SceneDoesNotExist
import com.soyle.stories.usecase.scene.inconsistencies.SceneInconsistencies
import com.soyle.stories.usecase.scene.inconsistencies.SceneSettingLocationInconsistencies
import com.soyle.stories.usecase.scene.location.detectInconsistencies.DetectInconsistenciesInSceneSettings
import com.soyle.stories.usecase.scene.location.detectInconsistencies.DetectInconsistenciesInSceneSettingsUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Detect Inconsistencies in Scene Settings Unit Test` {

    private val scene = makeScene()

    private val sceneRepository = SceneRepositoryDouble()
    private val locationRepository = LocationRepositoryDouble()

    private var report: SceneInconsistencies? = null
    private fun output() = DetectInconsistenciesInSceneSettings.OutputPort {
        report = it
    }

    private val detectInconsistencies: DetectInconsistenciesInSceneSettings =
        DetectInconsistenciesInSceneSettingsUseCase(sceneRepository, locationRepository)

    @Test
    fun `should throw error given scene does not exist`() = runBlocking {
        val error = assertThrows<SceneDoesNotExist> {
            detectInconsistencies(scene.id, output())
        }
        error.sceneId.mustEqual(scene.id.uuid)
    }

    @Nested
    inner class `Given Scene has no Scene Settings` {

        init {
            sceneRepository.givenScene(scene)
        }

        @Test
        fun `should output empty scene inconsistency report for scene setting`() = runBlocking {
            detectInconsistencies(scene.id, output())

            report!!.sceneId.mustEqual(scene.id)
            assertTrue((report!!.single() as SceneInconsistencies.SceneInconsistency.SceneSettingInconsistency).isEmpty())
        }

    }

    @Nested
    inner class `Given Scene has Scene Settings with existing Locations` {

        private val backingLocations = List(6) { makeLocation() }

        init {
            backingLocations.fold(scene) { a, b -> a.withLocationLinked(b).scene }
                .let(sceneRepository::givenScene)
            backingLocations.forEach(locationRepository::givenLocation)
        }

        @Test
        fun `should output no inconsistencies for each location in scene`() = runBlocking {
            detectInconsistencies(scene.id, output())

            report!!.sceneId.mustEqual(scene.id)
            with(report!!.single() as SceneInconsistencies.SceneInconsistency.SceneSettingInconsistency) {
                size.mustEqual(6)
                forEach { it.sceneId.mustEqual(scene.id) }
                map { it.locationId }.toSet().mustEqual(backingLocations.map { it.id }.toSet())
                forEach { assertTrue(it.isEmpty()) }
            }
        }

        @Nested
        inner class `Given Some of the Locations have been removed from the Story` {

            private val removedLocations = backingLocations.shuffled().take(3)

            init {
                runBlocking {
                    removedLocations.forEach { locationRepository.removeLocation(it) }
                }
            }

            @Test
            fun `should output removed from story inconsistency for removed locations`() = runBlocking {
                val removedLocationsById = removedLocations.associateBy { it.id }

                detectInconsistencies(scene.id, output())

                report!!.sceneId.mustEqual(scene.id)
                with(report!!.single() as SceneInconsistencies.SceneInconsistency.SceneSettingInconsistency) {
                    size.mustEqual(6)
                    forEach { it.sceneId.mustEqual(scene.id) }
                    map { it.locationId }.toSet().mustEqual(backingLocations.map { it.id }.toSet())
                    forEach { inconsistencies ->
                        val wasRemoved = inconsistencies.locationId in removedLocationsById
                        if (wasRemoved) inconsistencies.single()
                            .mustEqual(SceneSettingLocationInconsistencies.SceneSettingLocationInconsistency.LocationRemovedFromStory)
                        else assertTrue(inconsistencies.isEmpty())
                    }
                }
            }

        }

    }

}