package com.soyle.stories.usecase.character.arc.section

import com.soyle.stories.domain.character.*
import com.soyle.stories.domain.location.Location
import com.soyle.stories.domain.str
import com.soyle.stories.usecase.character.CharacterArcRepository
import com.soyle.stories.usecase.character.CharacterArcSectionDoesNotExist
import com.soyle.stories.usecase.character.arc.section.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSection
import com.soyle.stories.usecase.character.arc.section.unlinkLocationFromCharacterArcSection.UnlinkLocationFromCharacterArcSectionUseCase
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
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
        val updatedCharacterArcSection =
            updatedCharacterArc!!.arcSections.find { it.id.uuid == characterArcSectionId }!!
        assertNull(updatedCharacterArcSection.linkedLocation)
    }

    private val NoCharacterArcSections: List<UUID> = emptyList()
    private val NoLinkedLocations: List<UUID> = emptyList()
    private fun characterArcSectionWithIdOf(id: UUID) = listOf(id)
    private fun linkedToLocationWithIdOf(id: UUID) = listOf(id)

    private fun given(characterArcSectionIds: List<UUID>, linkedLocationIds: List<UUID>) {
        val arcSections = characterArcSectionIds.map {
            makeCharacterArcSection(
                id = CharacterArcSection.Id(it),
                template = template("Template ${str()}", false),
                linkedLocation = linkedLocationIds.firstOrNull()?.let(
                    Location::Id
                )
            )
        }
        val templateSections = arcSections.map { it.template }
        characterArcRepository = CharacterArcRepositoryDouble(
            onUpdateCharacterArc = ::updatedCharacterArc::set
        ).apply {
            arcSections.forEach {
                givenCharacterArc(
                    CharacterArc.planNewCharacterArc(
                        it.characterId,
                        it.themeId,
                        "",
                        CharacterArcTemplate(templateSections)
                    ).withArcSection(it)
                )
            }
        }
    }

    private lateinit var characterArcRepository: CharacterArcRepository

    private fun whenUseCaseExecuted() {
        val useCase: UnlinkLocationFromCharacterArcSection =
            UnlinkLocationFromCharacterArcSectionUseCase(characterArcRepository)
        runBlocking {
            useCase.invoke(characterArcSectionId, object : UnlinkLocationFromCharacterArcSection.OutputPort {
                override fun receiveUnlinkLocationFromCharacterArcSectionFailure(failure: Exception) {
                    result = failure
                }

                override fun receiveUnlinkLocationFromCharacterArcSectionResponse(response: UnlinkLocationFromCharacterArcSection.ResponseModel) {
                    result = response
                }
            })
        }
    }

    private fun assertIsValidResponseModel(actual: Any?) {
        actual as UnlinkLocationFromCharacterArcSection.ResponseModel
        assertEquals(characterArcSectionId, actual.characterArcSectionId)
    }

}