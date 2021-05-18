package com.soyle.stories.usecase.character.nameVariant

import com.soyle.stories.domain.character.characterName
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.character.nameVariant.list.ListCharacterNameVariants
import com.soyle.stories.usecase.character.nameVariant.list.ListCharacterNameVariantsUseCase
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `List Character Name Variants Unit Test` {

    private val character = makeCharacter()

    private val characterRepository = CharacterRepositoryDouble()

    private lateinit var result: ListCharacterNameVariants.ResponseModel

    @Test
    fun `character doesn't exist - should throw error`() {
        val error = assertThrows<CharacterDoesNotExist> {
            listCharacterNameVariants()
        }
        error.characterId.mustEqual(character.id.uuid)
    }

    @Nested
    inner class `Given Character Exists`
    {
        init {
            characterRepository.givenCharacter(character)
        }

        @Test
        fun `should output empty list`() {
            listCharacterNameVariants()
            assertTrue(result.isEmpty())
        }

    }

    @Nested
    inner class `Given Character has Name Variants`
    {

        val variants = List(5) { characterName() }

        init {
            variants.fold(character) { a, b -> a.withNameVariant(b).character }
                .let(characterRepository::givenCharacter)
        }

        @Test
        fun `output should contain all name variants`() {
            listCharacterNameVariants()
            result.toSet().mustEqual(variants.map { it.value }.toSet())
        }
    }

    private fun listCharacterNameVariants() {
        val useCase: ListCharacterNameVariants = ListCharacterNameVariantsUseCase(characterRepository)
        runBlocking {
            useCase.invoke(character.id) {
                result = it
            }
        }
    }

}