package com.soyle.stories.usecase.character.arc.section

import com.soyle.stories.domain.character.makeCharacterArcSection
import com.soyle.stories.domain.str
import com.soyle.stories.domain.character.template
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.CharacterArcSection
import com.soyle.stories.domain.character.CharacterArcTemplate
import com.soyle.stories.domain.location.Location
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import com.soyle.stories.usecase.location.LocationDoesNotExist
import com.soyle.stories.domain.location.makeLocation
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterArcSectionDoesNotExist
import com.soyle.stories.usecase.location.LocationRepository
import com.soyle.stories.usecase.character.arc.section.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSection
import com.soyle.stories.usecase.character.arc.section.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionUseCase
import com.soyle.stories.usecase.repositories.LocationRepositoryDouble
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
            makeLocation(id = Location.Id(it))
        }
        val arcSections = characterArcSectionIds.map {
            makeCharacterArcSection(id = CharacterArcSection.Id(it), template = template("Template ${str()}", false), linkedLocation = locations.firstOrNull()?.takeIf { isLinked }?.id)
        }
        val sectionTemplates = arcSections.map { it.template }
        characterArcRepository = CharacterArcRepositoryDouble(
            onUpdateCharacterArc = ::updatedCharacterArc::set
        ).apply {
            arcSections.forEach {
                givenCharacterArc(CharacterArc.planNewCharacterArc(it.characterId, it.themeId, "", CharacterArcTemplate(sectionTemplates)).withArcSection(it))
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