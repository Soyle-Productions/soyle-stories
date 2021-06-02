package com.soyle.stories.usecase.location

import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.locationName
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.prose.*
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.singleLine
import com.soyle.stories.domain.validation.SingleNonBlankLine
import com.soyle.stories.domain.validation.entitySetOf
import com.soyle.stories.usecase.location.renameLocation.RenameLocation
import com.soyle.stories.usecase.location.renameLocation.RenameLocationUseCase
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RenameLocationUnitTest {

    private val location = makeLocation()
    private val inputName = locationName()

    private var updatedLocation: Location? = null
    private val updatedProse: MutableList<Prose> = mutableListOf()
    private val updatedScenes: MutableList<Scene> = mutableListOf()
    private var result: RenameLocation.ResponseModel? = null

    private val locationRepository = LocationRepositoryDouble(onUpdateLocation = ::updatedLocation::set)
    private val proseRepository = ProseRepositoryDouble(onReplaceProse = updatedProse::add)
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = updatedScenes::add)

    @Test
    fun `when location does not exist, should throw error`() {
        val error = assertThrows<LocationDoesNotExist> {
            renameLocation()
        }
        error.locationId.mustEqual(location.id.uuid)
    }

    @Nested
    inner class `When Location Exists` {

        init {
            locationRepository.givenLocation(location)
        }

        @Test
        fun `should update location`() {
            renameLocation()
            updatedLocation!!.name.mustEqual(inputName)
        }

        @Test
        fun `should output event`() {
            renameLocation()
            result!!.locationRenamed.let {
                it.locationId.mustEqual(location.id)
                it.newName.mustEqual(inputName.value)
            }
        }

        @Test
        fun `should not update prose`() {
            renameLocation()
            updatedProse.isEmpty().mustEqual(true)
        }

        @Nested
        inner class `When Input Name is the Same as Current Name` {

            @Test
            fun `should not update location`() {
                renameLocation(location.name)
                assertNull(updatedLocation)
                assertTrue(updatedProse.isEmpty())
                assertTrue(updatedScenes.isEmpty())
            }

            @Test
            fun `should not output event`() {
                renameLocation(location.name)
                assertNull(result)
            }

        }

        @Nested
        inner class `When Mentioned in Prose` {

            private val prose = List(3) {
                makeProse(
                    content = listOf(
                        ProseContent("",
                            location.id.mentioned() to singleLine(location.name.value)
                        )
                    )
                )
            }

            init {
                prose.onEach(proseRepository::givenProse)
            }

            @Test
            fun `should update prose`() {
                renameLocation()
                updatedProse.mapTo(HashSet(3)) { it.id }
                    .mustEqual(prose.mapTo(HashSet(3)) { it.id })
                updatedProse.forEach {
                    it.text.contains(location.name.value).mustEqual(false)
                    it.text.contains(inputName.value).mustEqual(true)
                }
            }

            @Test
            fun `should output prose mention text replaced events`() {
                renameLocation()
                result!!.mentionTextReplaced.mapTo(HashSet(3)) { it.proseId }
                    .mustEqual(prose.mapTo(HashSet(3)) { it.id })
            }

        }

        @Nested
        inner class `When Used in Scenes`
        {
            private val scenes = List(5) { makeScene(settings = entitySetOf(SceneSettingLocation(location))) }
            init {
                scenes.forEach(sceneRepository::givenScene)
            }

            @Test
            fun `should update scenes`() {
                renameLocation()
                updatedScenes.map { it.id }.toSet().mustEqual(scenes.map { it.id }.toSet())
                updatedScenes.forEach {
                    it.settings.getEntityById(location.id)!!.locationName.mustEqual(inputName.value)
                }
            }

            @Test
            fun `should output scene setting location renamed events`() {
                renameLocation()
                result!!.sceneSettingLocationsRenamed.map { it.sceneId }.toSet().mustEqual(scenes.map { it.id }.toSet())
                result!!.sceneSettingLocationsRenamed.onEach {
                    it.sceneSettingLocation.locationName.mustEqual(inputName.value)
                }
            }

        }

    }

    private fun renameLocation(withName: SingleNonBlankLine = inputName) {
        val useCase: RenameLocation = RenameLocationUseCase(locationRepository, proseRepository, sceneRepository)
        runBlocking {
            useCase.invoke(location.id, withName, object : RenameLocation.OutputPort {
                override suspend fun receiveRenameLocationResponse(response: RenameLocation.ResponseModel) {
                    result = response
                }
            })
        }
    }
}
