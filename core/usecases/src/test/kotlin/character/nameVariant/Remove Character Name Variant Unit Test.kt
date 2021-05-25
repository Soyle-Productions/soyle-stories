package com.soyle.stories.usecase.character.nameVariant

import com.soyle.stories.domain.character.*
import com.soyle.stories.domain.character.events.CharacterNameVariantRemoved
import com.soyle.stories.domain.character.events.CharacterNameVariantRenamed
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.nameVariant.remove.RemoveCharacterNameVariant
import com.soyle.stories.usecase.character.nameVariant.remove.RemoveCharacterNameVariantUseCase
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
    fun `character doesn't exist - should throw error`() {
        val error = assertThrows<CharacterDoesNotExist> {
            removeCharacterNameVariant()
        }
        error.characterId.mustEqual(character.id.uuid)
        assertNull(result)
        assertNull(updatedCharacter)
    }

    @Test
    fun `character doesn't have requested name variant - should throw error`() {
        characterRepository.givenCharacter(character)
        val error = assertThrows<CharacterDoesNotHaveNameVariant> {
            removeCharacterNameVariant()
        }
        error.mustEqual(CharacterDoesNotHaveNameVariant(character.id, variant.value))
        assertNull(result)
        assertNull(updatedCharacter)
    }

    @Nested
    inner class `Given Removal is Successful` {

        init {
            characterRepository.givenCharacter(character.withNameVariant(variant).character)
        }

        @Test
        fun `should update character`() {
            removeCharacterNameVariant()
            updatedCharacter!!.otherNames.contains(variant).mustEqual(false)
        }

        @Test
        fun `should output removed variant event`() {
            removeCharacterNameVariant()
            result!!.characterNameVariantRemoved
                .mustEqual(CharacterNameVariantRemoved(character.id, variant))
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