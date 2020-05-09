package com.soyle.stories.characterarc.usecases

import com.soyle.stories.characterarc.CharacterArcException
import com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
import com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSection
import com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionUseCase
import com.soyle.stories.entities.*
import com.soyle.stories.theme.repositories.CharacterArcSectionRepository
import com.soyle.stories.theme.setupContext
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class UnlinkLocationFromCharacterArcSectionUnitTest {

	val characterArcSectionId = UUID.randomUUID()
	val locationId = UUID.randomUUID()

	private var updatedCharacterArcSection: CharacterArcSection? = null
	var result: Any? = null

	@Test
	fun `character arc section doesn't exist`() {
		given(NoCharacterArcSections, NoLinkedLocations)
		whenUseCaseExecuted()
		val result = result as CharacterArcSectionDoesNotExist
		assertEquals(characterArcSectionId, result.characterArcSectionId)
	}

	@Test
	fun `character arc section has no linked location`() {
		given(characterArcSectionWithIdOf(characterArcSectionId), NoLinkedLocations)
		whenUseCaseExecuted()
		assertIsValidResponseModel(result)
		assertNull(updatedCharacterArcSection, "Character arc section should not be saved if no changes have been made")
	}

	@Test
	fun `character arc section has a linked location`() {
		given(characterArcSectionWithIdOf(characterArcSectionId), linkedToLocationWithIdOf(locationId))
		whenUseCaseExecuted()
		assertIsValidResponseModel(result)
		val updatedCharacterArcSection = updatedCharacterArcSection!!
		assertNull(updatedCharacterArcSection.linkedLocation)
	}

	private val NoCharacterArcSections: List<UUID> = emptyList()
	private val NoLinkedLocations: List<UUID> = emptyList()
	private fun characterArcSectionWithIdOf(id: UUID) = listOf(id)
	private fun linkedToLocationWithIdOf(id: UUID) = listOf(id)

	private fun given(characterArcSectionIds: List<UUID>, linkedLocationIds: List<UUID>) {
		val context = setupContext(initialCharacterArcSections = characterArcSectionIds.map {
			CharacterArcSection(CharacterArcSection.Id(it), Character.Id(UUID.randomUUID()), Theme.Id(UUID.randomUUID()), CharacterArcTemplateSection(CharacterArcTemplateSection.Id(UUID.randomUUID()), ""), linkedLocationIds.firstOrNull()?.let(Location::Id), "")
		}, updateCharacterArcSection = {
			updatedCharacterArcSection = it
		})
		characterArcSectionRepository = context.characterArcSectionRepository
	}

	private lateinit var characterArcSectionRepository: CharacterArcSectionRepository

	private fun whenUseCaseExecuted() {
		val useCase: UnlinkLocationFromCharacterArcSection = UnlinkLocationFromCharacterArcSectionUseCase(characterArcSectionRepository)
		runBlocking {
			useCase.invoke(characterArcSectionId, object : UnlinkLocationFromCharacterArcSection.OutputPort {
				override fun receiveUnlinkLocationFromCharacterArcSectionFailure(failure: CharacterArcException) {
					result = failure
				}

				override fun receiveUnlinkLocationFromCharacterArcSectionResponse(response: UnlinkLocationFromCharacterArcSection.ResponseModel) {
					result = response
				}
			})
		}
	}

	private fun assertIsValidResponseModel(actual: Any?)
	{
		actual as UnlinkLocationFromCharacterArcSection.ResponseModel
		assertEquals(characterArcSectionId, actual.characterArcSectionId)
	}

}