package com.soyle.stories.characterarc.usecases

import com.soyle.stories.character.makeCharacterArcSection
import com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
import com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSection
import com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionUseCase
import com.soyle.stories.common.str
import com.soyle.stories.common.template
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.theme.repositories.CharacterArcRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class LinkLocationToCharacterArcSectionUnitTest {

    val characterArcSectionId = UUID.randomUUID()
    val locationId = UUID.randomUUID()

    private lateinit var characterArcRepository: CharacterArcRepository
    private lateinit var locationRepository: LocationRepository

    private var updatedCharacterArc: CharacterArc? = null
    private var result: Any? = null

    @Test
    fun `character arc section doesn't exist`() {
        givenNoCharacterArcSections()
        whenUseCaseExecuted()
        val result = result as CharacterArcSectionDoesNotExist
        assertEquals(characterArcSectionId, result.characterArcSectionId)
    }

    @Test
    fun `location doesn't exist`() {
        given(characterArcSectionIds = listOf(characterArcSectionId), locationIds = emptyList())
        whenUseCaseExecuted()
        val result = result as LocationDoesNotExist
        assertEquals(locationId, result.locationId)
    }

    @Test
    fun `location exists`() {
        given(characterArcSectionIds = listOf(characterArcSectionId), locationIds = listOf(locationId))
        whenUseCaseExecuted()
        assertIsValidResponseModel(result)
        val updatedCharacterArcSection = updatedCharacterArc!!.arcSections.find { it.id.uuid == characterArcSectionId }!!
        assertEquals(locationId, updatedCharacterArcSection.linkedLocation!!.uuid)
    }

    @Test
    fun `same location already linked`() {
        given(characterArcSectionIds = listOf(characterArcSectionId), locationIds = listOf(locationId), isLinked = true)
        whenUseCaseExecuted()
        assertIsValidResponseModel(result)
        assertNull(updatedCharacterArc)
    }

    private fun givenNoCharacterArcSections() = given(emptyList(), emptyList())
    private fun given(characterArcSectionIds: List<UUID>, locationIds: List<UUID>, isLinked: Boolean = false) {
        val locations = locationIds.map {
            Location(Location.Id(it), Project.Id(UUID.randomUUID()), "")
        }
        val arcSections = characterArcSectionIds.map {
            makeCharacterArcSection(id = CharacterArcSection.Id(it), template = template("Template ${str()}", false), linkedLocation = locations.firstOrNull()?.takeIf { isLinked }?.id)
        }
        characterArcRepository = CharacterArcRepositoryDouble(
            onUpdateCharacterArc = ::updatedCharacterArc::set
        ).apply {
            arcSections.forEach {
                givenCharacterArc(CharacterArc.planNewCharacterArc(it.characterId, it.themeId, "").withArcSection(it))
            }
        }
        locationRepository = LocationRepositoryDouble(initialLocations = locations)
    }

    private fun whenUseCaseExecuted() {
        val useCase: LinkLocationToCharacterArcSection =
            LinkLocationToCharacterArcSectionUseCase(characterArcRepository, locationRepository)
        runBlocking {
            useCase.invoke(characterArcSectionId, locationId, object : LinkLocationToCharacterArcSection.OutputPort {
                override fun receiveLinkLocationToCharacterArcSectionFailure(failure: Exception) {
                    result = failure
                }

                override fun receiveLinkLocationToCharacterArcSectionResponse(response: LinkLocationToCharacterArcSection.ResponseModel) {
                    result = response
                }
            })
        }
    }

    private fun assertIsValidResponseModel(actual: Any?) {
        actual as LinkLocationToCharacterArcSection.ResponseModel
        assertEquals(characterArcSectionId, actual.characterArcSectionId)
        assertEquals(locationId, actual.locationId)
    }
}