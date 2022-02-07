package com.soyle.stories.usecase.character.name

import com.soyle.stories.domain.character.*
import com.soyle.stories.domain.character.name.events.CharacterNameRemoved
import com.soyle.stories.domain.character.exceptions.CharacterException
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.name.remove.RemoveCharacterNameVariant
import com.soyle.stories.usecase.character.name.remove.RemoveCharacterNameVariantUseCase
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNull

class `Remove Character Name Variant Unit Test` {

    private val character = makeCharacter()
    private val variant = characterName()

    private var updatedCharacter: Character? = null

    private val characterRepository = CharacterRepositoryDouble(onUpdateCharacter = ::updatedCharacter::set)

    private var result: RemoveCharacterNameVariant.ResponseModel? = null

    @Test
    fun `character doesn't exist - should return error`() {
        val error = removeCharacterNameVariant()
        error!!.characterId.mustEqual(character.id)
        assertNull(result)
        assertNull(updatedCharacter)
    }

    @Test
    fun `character doesn't have requested name variant - should return error`() {
        characterRepository.givenCharacter(character)
        val error = removeCharacterNameVariant()
        error.mustEqual(CharacterDoesNotHaveNameVariant(character.id, variant.value))
        assertNull(result)
        assertNull(updatedCharacter)
    }

    @Nested
    inner class `Given Removal is Successful` {

        init {
            characterRepository.givenCharacter(character.withName(variant).character)
        }

        @Test
        fun `should update character`() {
            removeCharacterNameVariant()
            updatedCharacter!!.names.contains(variant).mustEqual(false)
        }

        @Test
        fun `should output removed variant event`() {
            removeCharacterNameVariant()
            result!!.characterNameRemoved
                .mustEqual(CharacterNameRemoved(character.id, variant.value))
        }

        @Test
        fun `should not return a failure message`() {
            val failure = removeCharacterNameVariant()
            Assertions.assertNull(failure)
        }

    }

    private fun removeCharacterNameVariant(variant: NonBlankString = this.variant): CharacterException? {
        val useCase: RemoveCharacterNameVariant = RemoveCharacterNameVariantUseCase(characterRepository)
        return runBlocking {
            useCase.invoke(character.id, variant) {
                result = it
            }
        }
    }

}