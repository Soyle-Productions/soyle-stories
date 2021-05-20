package com.soyle.stories.usecase.character.nameVariant

import com.soyle.stories.domain.character.*
import com.soyle.stories.domain.character.events.CharacterNameVariantRenamed
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.nameVariant.rename.RenameCharacterNameVariant
import com.soyle.stories.usecase.character.nameVariant.rename.RenameCharacterNameVariantUseCase
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNull

class `Rename Character Name Variant Unit Test` {

    private val character = makeCharacter()
    private val originalVariant = characterName()
    private val replacementVariant = characterName()

    private var updatedCharacter: Character? = null
    private var result: RenameCharacterNameVariant.ResponseModel? = null

    private val characterRepository = CharacterRepositoryDouble(onUpdateCharacter = ::updatedCharacter::set)

    @Test
    fun `character doesn't exist - should throw error`() {
        val error = assertThrows<CharacterDoesNotExist> {
            renameCharacterNameVariant()
        }
        error.characterId.mustEqual(character.id.uuid)
    }

    @Test
    fun `character doesn't have requested name variant - should throw error`() {
        characterRepository.givenCharacter(character)
        val error = assertThrows<CharacterDoesNotHaveNameVariant> {
            renameCharacterNameVariant()
        }
        error.mustEqual(CharacterDoesNotHaveNameVariant(character.id, originalVariant.value))
    }

    @Nested
    inner class `Given Rename is Successful` {

        init {
            characterRepository.givenCharacter(character.withNameVariant(originalVariant).character)
        }

        @Test
        fun `should update character`() {
            renameCharacterNameVariant()
            updatedCharacter!!.otherNames.contains(originalVariant).mustEqual(false)
            updatedCharacter!!.otherNames.contains(replacementVariant).mustEqual(true)
        }

        @Test
        fun `should output renamed variant event`() {
            renameCharacterNameVariant()
            result!!.characterNameVariantRenamed
                .mustEqual(CharacterNameVariantRenamed(character.id, originalVariant, replacementVariant))
        }

        @Test
        fun `should not return a failure message`() {
            val failure = renameCharacterNameVariant()
            assertNull(failure)
        }

    }

    @Nested
    inner class `Given Rename is Unsuccessful`
    {

        init {
            characterRepository.givenCharacter(character.withNameVariant(originalVariant).character)
        }

        @AfterEach
        fun `should not produce output`() {
            assertNull(result)
        }

        @AfterEach
        fun `should not update character`() {
            assertNull(updatedCharacter)
        }

        @Test
        fun `same name provided`() {
            val failure = renameCharacterNameVariant(originalVariant)
            // fails silently.
            assertNull(failure)
        }

        @Test
        fun `replacement already exists as other name`() {
            characterRepository.givenCharacter(
                character.withNameVariant(originalVariant)
                    .character.withNameVariant(replacementVariant).character)

            val failure = renameCharacterNameVariant(replacementVariant)
            failure.mustEqual(CharacterNameVariantCannotEqualOtherVariant(character.id, replacementVariant.value))
        }

        @Test
        fun `replacement is same as display name`() {
            val failure = renameCharacterNameVariant(character.name)
            failure.mustEqual(CharacterNameVariantCannotEqualDisplayName(character.id, character.name.value))
        }

    }

    private fun renameCharacterNameVariant(replacement: NonBlankString = replacementVariant) = runBlocking {
        val useCase: RenameCharacterNameVariant = RenameCharacterNameVariantUseCase(characterRepository)
        val request = RenameCharacterNameVariant.RequestModel(character.id, originalVariant, replacement)
        useCase.invoke(request) {
            result = it
        }
    }

}