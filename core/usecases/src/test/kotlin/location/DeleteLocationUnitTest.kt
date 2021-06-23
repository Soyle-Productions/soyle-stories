package com.soyle.stories.usecase.location

import com.soyle.stories.domain.character.*
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.scene.SceneSettingLocation
import com.soyle.stories.domain.scene.makeScene
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.validation.entitySetOf
import com.soyle.stories.usecase.location.deleteLocation.DeleteLocation
import com.soyle.stories.usecase.location.deleteLocation.DeleteLocationUseCase
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
import com.soyle.stories.usecase.repositories.SceneRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class DeleteLocationUnitTest {

    private val location = makeLocation()

    private val updatedCharacterArcs = mutableListOf<CharacterArc>()
    private var deletedLocation: Location? = null
    private var updatedScenes = mutableListOf<Scene>()
    private var result: DeleteLocation.ResponseModel? = null

    private val locationRepository = LocationRepositoryDouble(onRemoveLocation = ::deletedLocation::set)
    private val sceneRepository = SceneRepositoryDouble(onUpdateScene = updatedScenes::add)
    private val characterArcSectionRepository =
        CharacterArcRepositoryDouble(onUpdateCharacterArc = updatedCharacterArcs::add)

    @Test
    fun `location does not exist`() {
        val error = assertThrows<LocationDoesNotExist> {
            deleteLocation()
        }
        error.locationId.mustEqual(location.id.uuid)
    }

    @Test
    fun `existing location is deleted`() {
        locationRepository.givenLocation(location)
        deleteLocation()
        val deletedLocation = deletedLocation!!
        deletedLocation.id.mustEqual(location.id)
        val result = result as DeleteLocation.ResponseModel
        result.deletedLocation.location.mustEqual(location.id)
    }

    @Test
    fun `update linked character arc sections`() {
        locationRepository.givenLocation(location)
        val arcs = List(3) {
            CharacterArc.planNewCharacterArc(Character.Id(), Theme.Id(), "")
                .withArcSection(makeCharacterArcSection(linkedLocation = location.id, template = Drive))
                .withArcSection(
                    makeCharacterArcSection(
                        linkedLocation = location.id,
                        template = AttackByAlly
                    )
                )
                .withArcSection(makeCharacterArcSection(template = MoralDecision))
        }
        val arcSections = arcs.flatMap { it.arcSections }.filter { it.linkedLocation == location.id }
        arcs.forEach(characterArcSectionRepository::givenCharacterArc)
        deleteLocation()
        val updatedCharacterArcSectionsById = arcSections.associateBy { it.id }
        arcSections.forEach { updatedCharacterArcSectionsById.getValue(it.id) }
        updatedCharacterArcs.forEach {
            assertTrue(it.arcSections.none { it.linkedLocation == location.id })
        }
        val result = result as DeleteLocation.ResponseModel
        result.updatedArcSections.mustEqual(arcSections.map { it.id.uuid }.toSet())
    }

    @Nested
    inner class `Given used in scene` {

        private val scenes = List(5) { makeScene(settings = entitySetOf(SceneSettingLocation(location))) }

        init {
            locationRepository.givenLocation(location)
            scenes.forEach(sceneRepository::givenScene)
        }

        @Test
        fun `should not remove location setting from scene`() {
            deleteLocation()
            assertTrue(updatedScenes.isEmpty())
        }

        @Test
        fun `should not output location setting removed from scene event`() {
            deleteLocation()
            assertTrue(result!!.locationRemovedFromScenes.isEmpty())
        }

    }

    private fun deleteLocation() {
        val useCase: DeleteLocation =
            DeleteLocationUseCase(locationRepository, characterArcSectionRepository, sceneRepository)
        runBlocking {
            useCase.invoke(location.id.uuid, object : DeleteLocation.OutputPort {
                override suspend fun receiveDeleteLocationResponse(response: DeleteLocation.ResponseModel) {
                    result = response
                }
            })
        }
    }

}