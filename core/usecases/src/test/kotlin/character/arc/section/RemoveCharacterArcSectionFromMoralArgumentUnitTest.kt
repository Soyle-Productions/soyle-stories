package com.soyle.stories.usecase.character.arc.section

import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.character.makeCharacterArcSection
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.character.CharacterArcSectionDoesNotExist
import com.soyle.stories.usecase.character.arc.section.removeCharacterArcSectionFromMoralArgument.CharacterArcSectionRemoved
import com.soyle.stories.usecase.character.arc.section.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgument
import com.soyle.stories.usecase.character.arc.section.removeCharacterArcSectionFromMoralArgument.RemoveCharacterArcSectionFromMoralArgumentUseCase
import com.soyle.stories.usecase.repositories.CharacterArcRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class RemoveCharacterArcSectionFromMoralArgumentUnitTest {

    // preconditions
    private val theme = makeTheme()
    private val character = makeCharacter()
    private val characterArc = CharacterArc.planNewCharacterArc(character.id, theme.id, theme.name)
    private val sectionTemplate = characterArc.template.sections.find { !it.isRequired && it.isMoral }!!
    private val section =
        makeCharacterArcSection(template = sectionTemplate, themeId = theme.id, characterId = character.id)

    // post-conditions
    private var updatedCharacterArc: CharacterArc? = null

    // output
    private var result: Any? = null

    private val characterArcRepository = CharacterArcRepositoryDouble(onUpdateCharacterArc = ::updatedCharacterArc::set)
    private val useCase: RemoveCharacterArcSectionFromMoralArgument =
        RemoveCharacterArcSectionFromMoralArgumentUseCase(characterArcRepository)

    @Nested
    inner class Degenerates {

        @AfterEach
        fun `should not updated character arc`() {
            assertNull(updatedCharacterArc) { "Should not have updated the character arc" }
        }

        @AfterEach
        fun `should not receive any output`() {
            assertNull(result) { "Should not have received any output" }
        }

        @Test
        fun `character arc doesn't exist`() {
            val error = assertThrows<CharacterArcSectionDoesNotExist> {
                removeCharacterArcSectionFromMoralArgument()
            }

            error.characterArcSectionId.mustEqual(section.id.uuid)
        }

        @Test
        fun `character arc doesn't contain section`() {
            characterArcRepository.givenCharacterArc(characterArc)

            val error = assertThrows<CharacterArcSectionDoesNotExist> {
                removeCharacterArcSectionFromMoralArgument()
            }

            error.characterArcSectionId.mustEqual(section.id.uuid)
        }

    }

    @Nested
    inner class `Happy Path` {

        init {
            characterArc.withArcSection(section)
                .let(characterArcRepository::givenCharacterArc)
        }

        @Test
        fun `should remove section from arc`() {
            removeCharacterArcSectionFromMoralArgument()

            updatedCharacterArc!!.mustEqual(characterArc)
        }

        @Test
        fun `should output removed event`() {
            removeCharacterArcSectionFromMoralArgument()

            val result = result as RemoveCharacterArcSectionFromMoralArgument.ResponseModel
            result.removedSection.mustEqual(
                CharacterArcSectionRemoved(
                    arcSectionId = section.id.uuid,
                    themeId = theme.id.uuid,
                    characterId = character.id.uuid,
                    arcId = characterArc.id.uuid
                )
            )
        }

        @Test
        fun `no reordered events should be output`() {
            removeCharacterArcSectionFromMoralArgument()

            val result = result as RemoveCharacterArcSectionFromMoralArgument.ResponseModel
            assertTrue(result.movedSections.isEmpty())
        }

    }

    @Nested
    inner class `When at the beginning of moral argument` {

        init {
            characterArc.moralArgument().withArcSection(section, 0)
                .let(characterArcRepository::givenCharacterArc)
        }

        @Test
        fun `all following moral sections should output moved events`() {
            removeCharacterArcSectionFromMoralArgument()

            val result = result as RemoveCharacterArcSectionFromMoralArgument.ResponseModel
            val receivedEventIds = result.movedSections.map { it.arcSectionId }.toSet()
            val expectedIds = characterArc.moralArgument().arcSections.map { it.id.uuid }.toSet()
            receivedEventIds.mustEqual(expectedIds)
            result.movedSections.forEach { event ->
                val section = characterArc.arcSections.find { it.id.uuid == event.arcSectionId }!!
                event.originalIndex.mustEqual(characterArc.indexInMoralArgument(section.id)!! + 1)
                event.newIndex.mustEqual(characterArc.indexInMoralArgument(section.id)!!)
            }
        }

    }

    @Nested
    inner class `When in the middle of the moral argument` {

        private val moralArgumentSizeWithoutSection = characterArc.moralArgument().arcSections.size

        init {
            characterArc.moralArgument().withArcSection(section, 2)
                .let(characterArcRepository::givenCharacterArc)
        }

        @Test
        fun `only the sections following this section should output moved events`() {
            removeCharacterArcSectionFromMoralArgument()

            val result = result as RemoveCharacterArcSectionFromMoralArgument.ResponseModel
            val receivedEventIds = result.movedSections.map { it.arcSectionId }.toSet()
            val expectedIds = characterArc.moralArgument().arcSections.takeLast(moralArgumentSizeWithoutSection - 2).map { it.id.uuid }.toSet()
            receivedEventIds.mustEqual(expectedIds)
            result.movedSections.forEach { event ->
                val section = characterArc.arcSections.find { it.id.uuid == event.arcSectionId }!!
                event.originalIndex.mustEqual(characterArc.indexInMoralArgument(section.id)!! + 1)
                event.newIndex.mustEqual(characterArc.indexInMoralArgument(section.id)!!)
            }
        }

    }

    private fun removeCharacterArcSectionFromMoralArgument() {
        val output = object : RemoveCharacterArcSectionFromMoralArgument.OutputPort {
            override suspend fun removedCharacterArcSectionFromMoralArgument(response: RemoveCharacterArcSectionFromMoralArgument.ResponseModel) {
                result = response
            }
        }
        runBlocking {
            useCase.invoke(section.id.uuid, output)
        }
    }

}