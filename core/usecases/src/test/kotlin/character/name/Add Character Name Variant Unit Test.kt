package com.soyle.stories.usecase.character.name

import com.soyle.stories.domain.character.*
import com.soyle.stories.domain.character.name.exceptions.CharacterNameDuplicateOperationException
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.name.create.AddCharacterNameVariant
import com.soyle.stories.usecase.character.name.create.AddCharacterNameVariantUseCase
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Add Character Name Variant Unit Test` {

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
        error.characterId.mustEqual(character.id)
    }

    @Nested
    inner class `Given Character Exists` {
        init {
            characterRepository.givenCharacter(character)
        }

        @Test
        fun `name variant is same as display name`() {
            assertThrows<CharacterNameDuplicateOperationException> {
                addCharacterNameVariant(character.displayName)
            }
            assertNull(updatedCharacter)
            assertNull(result)
        }

        @Test
        fun `duplicate name variant`() {
            val input = nonBlankStr("Frank")
            characterRepository.givenCharacter(character.withName(input).character)
            assertThrows<CharacterNameDuplicateOperationException> {
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
                updatedCharacter!!.names.contains(input).mustEqual(true)
            }

            @Test
            fun `should output name variant added event`() {
                val input = nonBlankStr("Frank")
                addCharacterNameVariant(input)
                result!!.characterNameAdded.run {
                    name.mustEqual(input.value)
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