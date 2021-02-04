package com.soyle.stories.characterarc.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.characterarc.CharacterArcDoesNotExist
import com.soyle.stories.characterarc.CharacterArcSectionAlreadyInPosition
import com.soyle.stories.characterarc.CharacterArcSectionDoesNotExist
import com.soyle.stories.characterarc.CharacterArcSectionNotInMoralArgument
import com.soyle.stories.characterarc.usecases.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgument
import com.soyle.stories.characterarc.usecases.moveCharacterArcSectionInMoralArgument.MoveCharacterArcSectionInMoralArgumentUseCase
import com.soyle.stories.common.mustEqual
import com.soyle.stories.common.str
import com.soyle.stories.common.template
import com.soyle.stories.doubles.CharacterArcRepositoryDouble
import com.soyle.stories.entities.CharacterArc
import com.soyle.stories.entities.CharacterArcTemplate
import com.soyle.stories.theme.makeTheme
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.util.*

class `Move Character Arc Section in Moral Argument UnitTest` {

    private val theme = makeTheme()
    private val character = makeCharacter()
    private val characterArc = CharacterArc.planNewCharacterArc(character.id, theme.id, theme.name, CharacterArcTemplate(List (4) {
        template("Template ${str()}", moral = true)
    } + template("Template ${str()}")))
    private val arcSection = characterArc.moralArgument().arcSections.first()

    private var updatedCharacterArc: CharacterArc? = null
    private var result: Any? = null

    private val characterArcRepository = CharacterArcRepositoryDouble(onUpdateCharacterArc = ::updatedCharacterArc::set)
    private val useCase: MoveCharacterArcSectionInMoralArgument = MoveCharacterArcSectionInMoralArgumentUseCase(characterArcRepository)
    private val output = object : MoveCharacterArcSectionInMoralArgument.OutputPort {
        override suspend fun receiveMoveCharacterArcSectionInMoralArgumentResponse(response: MoveCharacterArcSectionInMoralArgument.ResponseModel) {
            result = response
        }
    }

    @Nested
    inner class `Degenerate Cases` {

        @Test
        fun `character arc does not exist`() {
            val error = assertThrows<CharacterArcDoesNotExist> {
                moveCharacterArcSectionInMoralArgument()
            }

            error.themeId.mustEqual(theme.id.uuid)
            error.characterId.mustEqual(character.id.uuid)
        }

        @Test
        fun `arc section not in character arc`() {
            characterArcRepository.givenCharacterArc(characterArc)

            val inputId = UUID.randomUUID()
            val error = assertThrows<CharacterArcSectionDoesNotExist> {
                moveCharacterArcSectionInMoralArgument(inputId)
            }

            error.characterArcSectionId.mustEqual(inputId)
        }

        @Test
        fun `arc section is not in moral argument`() {
            characterArcRepository.givenCharacterArc(characterArc)

            val inputId = characterArc.arcSections.find { ! it.template.isMoral }!!.id.uuid
            val error = assertThrows<CharacterArcSectionNotInMoralArgument> {
                moveCharacterArcSectionInMoralArgument(inputId)
            }

            error.characterArcSectionId.mustEqual(inputId) { "CharacterArcSectionNotInMoralArgument.characterArcSectionId has incorrect id" }
            error.arcId.mustEqual(characterArc.id.uuid) { "CharacterArcSectionNotInMoralArgument.arcId has incorrect id" }
            error.characterId.mustEqual(character.id.uuid) { "CharacterArcSectionNotInMoralArgument.characterId has incorrect id" }
            error.themeId.mustEqual(theme.id.uuid) { "CharacterArcSectionNotInMoralArgument.themeId has incorrect id" }
        }

        @Test
        fun `index is out of bounds of moral argument`() {
            characterArcRepository.givenCharacterArc(characterArc)

            listOf(-1, characterArc.moralArgument().arcSections.size).forEach {
                assertThrows<IndexOutOfBoundsException> {
                    moveCharacterArcSectionInMoralArgument(index = it)
                }
            }
        }

        @Test
        fun `index is the same`() {
            characterArcRepository.givenCharacterArc(characterArc)

            val inputIndex = characterArc.indexInMoralArgument(arcSection.id)!!
            val error = assertThrows<CharacterArcSectionAlreadyInPosition> {
                moveCharacterArcSectionInMoralArgument(index = inputIndex)
            }

            error.characterArcSectionId.mustEqual(arcSection.id.uuid)
            error.index.mustEqual(inputIndex)
        }

        @AfterEach
        fun `should not complete use case`() {
            assertNull(updatedCharacterArc) { "should not update character arc" }
            assertNull(result) { "should not output result" }
        }

    }

    @Nested
    inner class `Move Forward` {

        private val targetIndex = 3

        init {
            characterArcRepository.givenCharacterArc(characterArc)
        }

        @Test
        fun `Should move to new index`() {
            moveCharacterArcSectionInMoralArgument(index = targetIndex)

            updatedCharacterArc!!.indexInMoralArgument(arcSection.id).mustEqual(targetIndex)
        }

        @Test
        fun `Sections after original position should have been moved`() {
            moveCharacterArcSectionInMoralArgument(index = targetIndex)

            updatedCharacterArc!!.run {
                (1 until moralArgument().arcSections.size).forEach {
                    indexInMoralArgument(characterArc.moralArgument().arcSections[it].id).mustEqual(it-1)
                }
            }
        }

        @Test
        fun `should output all moved sections`() {
            moveCharacterArcSectionInMoralArgument(index = targetIndex)

            val result = result as MoveCharacterArcSectionInMoralArgument.ResponseModel
            characterArc.moralArgument().arcSections.forEachIndexed { originalIndex, arcSection ->
                val expectedEvent = result.find { it.arcSectionId == arcSection.id.uuid }!!
                expectedEvent.originalIndex.mustEqual(originalIndex)
                if (arcSection.id == this@`Move Character Arc Section in Moral Argument UnitTest`.arcSection.id) {
                    expectedEvent.newIndex.mustEqual(targetIndex)
                } else {
                    expectedEvent.newIndex.mustEqual(originalIndex -1)
                }
            }
        }

        @Test
        fun `only effected sections should be output`() {
            moveCharacterArcSectionInMoralArgument(index = 2)

            val result = result as MoveCharacterArcSectionInMoralArgument.ResponseModel
            result.size.mustEqual(3)
        }

        @Test
        fun `output should have theme, character, and arc`() {
            moveCharacterArcSectionInMoralArgument(index = 2)

            val result = result as MoveCharacterArcSectionInMoralArgument.ResponseModel
            result.themeId.mustEqual(theme.id.uuid)
            result.characterId.mustEqual(character.id.uuid)
            result.characterArcId.mustEqual(characterArc.id.uuid)
        }

    }

    @Nested
    inner class `Move backward` {

        init {
            characterArcRepository.givenCharacterArc(characterArc)
        }

        @Test
        fun `Should move to new index`() {
            val sectionToMove = characterArc.moralArgument().arcSections.last()
            moveCharacterArcSectionInMoralArgument(arcSectionId = sectionToMove.id.uuid, index = 0)

            updatedCharacterArc!!.indexInMoralArgument(sectionToMove.id).mustEqual(0)
        }

        @Test
        fun `Sections before new position should have been moved`() {
            val sectionToMove = characterArc.moralArgument().arcSections.last()
            moveCharacterArcSectionInMoralArgument(arcSectionId = sectionToMove.id.uuid, index = 0)

            updatedCharacterArc!!.run {
                (0 until moralArgument().arcSections.size - 1).forEach {
                    indexInMoralArgument(characterArc.moralArgument().arcSections[it].id).mustEqual(it+1)
                }
            }
        }

        @Test
        fun `should output all moved sections`() {
            val originalSections = characterArc.moralArgument().arcSections
            val sectionToMove = originalSections.last()
            moveCharacterArcSectionInMoralArgument(arcSectionId = sectionToMove.id.uuid, index = 0)

            val result = result as MoveCharacterArcSectionInMoralArgument.ResponseModel
            originalSections.forEachIndexed { originalIndex, arcSection ->
                val expectedEvent = result.find { it.arcSectionId == arcSection.id.uuid }!!
                expectedEvent.originalIndex.mustEqual(originalIndex)
                if (arcSection.id == sectionToMove.id) {
                    expectedEvent.newIndex.mustEqual(0)
                } else {
                    expectedEvent.newIndex.mustEqual(originalIndex +1)
                }
            }
        }

        @Test
        fun `only effected sections should be output`() {
            val originalSections = characterArc.moralArgument().arcSections
            val sectionToMove = originalSections.last()
            moveCharacterArcSectionInMoralArgument(arcSectionId = sectionToMove.id.uuid, index = 1)

            val result = result as MoveCharacterArcSectionInMoralArgument.ResponseModel
            result.size.mustEqual(3)
        }

        @Test
        fun `output should have theme, character, and arc`() {
            val sectionToMove = characterArc.moralArgument().arcSections.last()
            moveCharacterArcSectionInMoralArgument(arcSectionId = sectionToMove.id.uuid, index = 0)

            val result = result as MoveCharacterArcSectionInMoralArgument.ResponseModel
            result.themeId.mustEqual(theme.id.uuid)
            result.characterId.mustEqual(character.id.uuid)
            result.characterArcId.mustEqual(characterArc.id.uuid)
        }

    }

    private fun moveCharacterArcSectionInMoralArgument(arcSectionId: UUID = arcSection.id.uuid, index: Int = 0) {
        val request = MoveCharacterArcSectionInMoralArgument.RequestModel(theme.id.uuid, character.id.uuid, arcSectionId, index)
        runBlocking {
            useCase.invoke(request, output)
        }
    }

}