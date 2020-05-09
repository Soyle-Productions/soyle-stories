package com.soyle.stories.characterarc.usecases

import com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
import com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSection
import com.soyle.stories.characterarc.usecases.linkLocationToCharacterArcSection.LinkLocationToCharacterArcSectionUseCase
import com.soyle.stories.entities.*
import com.soyle.stories.location.LocationDoesNotExist
import com.soyle.stories.location.doubles.LocationRepositoryDouble
import com.soyle.stories.location.repositories.LocationRepository
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.theme.setupContext
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class LinkLocationToCharacterArcSectionUnitTest {

	val characterArcSectionId = UUID.randomUUID()
	val locationId = UUID.randomUUID()

	private lateinit var characterArcSectionRepository: CharacterArcSectionRepository
	private lateinit var locationRepository: LocationRepository

	private var updatedCharacterArcSection: CharacterArcSection? = null
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
		val updatedCharacterArcSection = updatedCharacterArcSection as CharacterArcSection
		assertEquals(locationId, updatedCharacterArcSection.linkedLocation!!.uuid)
	}

	@Test
	fun `same location already linked`() {
		given(characterArcSectionIds = listOf(characterArcSectionId), locationIds = listOf(locationId), isLinked = true)
		whenUseCaseExecuted()
		assertIsValidResponseModel(result)
		assertNull(updatedCharacterArcSection)
	}

	private fun givenNoCharacterArcSections() = given(emptyList(), emptyList())
	private fun given(characterArcSectionIds: List<UUID>, locationIds: List<UUID>, isLinked: Boolean = false) {
		val locations = locationIds.map {
			Location(Location.Id(it), Project.Id(UUID.randomUUID()), "")
		}
		val context = setupContext(initialCharacterArcSections = characterArcSectionIds.map {
			CharacterArcSection(CharacterArcSection.Id(it), Character.Id(UUID.randomUUID()), Theme.Id(UUID.randomUUID()), CharacterArcTemplateSection(CharacterArcTemplateSection.Id(UUID.randomUUID()), ""), locations.firstOrNull()?.takeIf { isLinked }?.id, "")
		}, updateCharacterArcSection = {
			updatedCharacterArcSection = it
		})
		characterArcSectionRepository = context.characterArcSectionRepository
		locationRepository = LocationRepositoryDouble(initialLocations = locations)
	}

	private fun whenUseCaseExecuted() {
		val useCase: LinkLocationToCharacterArcSection = LinkLocationToCharacterArcSectionUseCase(characterArcSectionRepository, locationRepository)
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