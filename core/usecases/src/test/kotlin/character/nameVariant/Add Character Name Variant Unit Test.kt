package com.soyle.stories.usecase.character.nameVariant

import com.soyle.stories.domain.character.*
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.nameVariant.create.AddCharacterNameVariant
import com.soyle.stories.usecase.character.nameVariant.create.AddCharacterNameVariantUseCase
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Add Character Name Variant Unit Test` {

    /*
    use case should call character.withNameVariant
        - this produces an event if successful
        - or just the character if unsuccessful

    have to retrieve the character from a repository, then update it
     */

    private val character = makeCharacter()

    private var updatedCharacter: Character? = null

    private val characterRepository = CharacterRepositoryDouble(onUpdateCharacter = ::updatedCharacter::set)

    private var result: AddCharacterNameVariant.ResponseModel? = null

    // simplest test.  Character not in repository.  Should throw error
    @Test
    fun `character doesn't exist`() {
        val error = assertThrows<CharacterDoesNotExist> {
            addCharacterNameVariant()
        }
        error.characterId.mustEqual(character.id.uuid)
    }

    @Nested
    inner class `Given Character Exists` {
        init {
            characterRepository.givenCharacter(character)
        }

        @Test
        fun `name variant is same as display name`() {
            assertThrows<CharacterNameVariantCannotEqualDisplayName> {
                addCharacterNameVariant(character.name)
            }
            assertNull(updatedCharacter)
            assertNull(result)
        }

        @Test
        fun `duplicate name variant`() {
            val input = nonBlankStr("Frank")
            characterRepository.givenCharacter(character.withNameVariant(input).character)
            assertThrows<CharacterNameVariantCannotEqualOtherVariant> {
                addCharacterNameVariant(input)
            }
            assertNull(updatedCharacter)
            assertNull(result)
        }

        @Nested
        inner class `Unique Name Provided` {

            @Test
            fun `should update character with new name`() {
                val input = nonBlankStr("Frank")
                addCharacterNameVariant(input)
                updatedCharacter!!.otherNames.contains(input).mustEqual(true)
            }

            @Test
            fun `should output name variant added event`() {
                val input = nonBlankStr("Frank")
                addCharacterNameVariant(input)
                result!!.characterNameVariantAdded.run {
                    newVariant.mustEqual(input.value)
                    characterId.mustEqual(character.id)
                }
            }

        }

    }

    private fun addCharacterNameVariant(variant: NonBlankString = nonBlankStr()) = runBlocking {
        val useCase: AddCharacterNameVariant = AddCharacterNameVariantUseCase(characterRepository)
        useCase.invoke(character.id, variant, object : AddCharacterNameVariant.OutputPort {
            override suspend fun addedCharacterNameVariant(response: AddCharacterNameVariant.ResponseModel) {
                result = response
            }
        })
    }

}