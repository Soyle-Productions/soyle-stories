package com.soyle.stories.characterarc.usecases

import com.soyle.stories.character.makeCharacterArcSection
import com.soyle.stories.characterarc.CharacterArcException
import com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
import com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSection
import com.soyle.stories.characterarc.usecases.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionUseCase
import com.soyle.stories.common.str
import com.soyle.stories.common.template
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.entities.*
import com.soyle.stories.theme.repositories.CharacterArcRepository
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import java.util.*

class UnlinkLocationFromCharacterArcSectionUnitTest {

	val characterArcSectionId = UUID.randomUUID()
	val locationId = UUID.randomUUID()

	private var updatedCharacterArc: CharacterArc? = null
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
		assertNull(updatedCharacterArc, "Character arc should not be saved if no changes have been made")
	}

	@Test
	fun `character arc section has a linked location`() {
		given(characterArcSectionWithIdOf(characterArcSectionId), linkedToLocationWithIdOf(locationId))
		whenUseCaseExecuted()
		assertIsValidResponseModel(result)
		val updatedCharacterArcSection = updatedCharacterArc!!.arcSections.find { it.id.uuid == characterArcSectionId }!!
		assertNull(updatedCharacterArcSection.linkedLocation)
	}

	private val NoCharacterArcSections: List<UUID> = emptyList()
	private val NoLinkedLocations: List<UUID> = emptyList()
	private fun characterArcSectionWithIdOf(id: UUID) = listOf(id)
	private fun linkedToLocationWithIdOf(id: UUID) = listOf(id)

	private fun given(characterArcSectionIds: List<UUID>, linkedLocationIds: List<UUID>) {
		val arcSections = characterArcSectionIds.map {
			makeCharacterArcSection(id = CharacterArcSection.Id(it), template = template("Template ${str()}", false), linkedLocation = linkedLocationIds.firstOrNull()?.let(Location::Id))
		}
		characterArcRepository = CharacterArcRepositoryDouble(
			onUpdateCharacterArc = ::updatedCharacterArc::set
		).apply {
			arcSections.forEach {
				givenCharacterArc(CharacterArc.planNewCharacterArc(it.characterId, it.themeId, "").withArcSection(it))
			}
		}
	}

	private lateinit var characterArcRepository: CharacterArcRepository

	private fun whenUseCaseExecuted() {
		val useCase: UnlinkLocationFromCharacterArcSection = UnlinkLocationFromCharacterArcSectionUseCase(characterArcRepository)
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