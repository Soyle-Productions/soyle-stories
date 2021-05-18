package com.soyle.stories.usecase.character.nameVariant

import com.soyle.stories.domain.character.characterName
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.nameVariant.create.AddCharacterNameVariant
import com.soyle.stories.usecase.character.nameVariant.create.AddCharacterNameVariantUseCase
import com.soyle.stories.usecase.character.nameVariant.list.ListCharacterNameVariants
import com.soyle.stories.usecase.character.nameVariant.list.ListCharacterNameVariantsUseCase
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class `Name Variant Management Int Test` {

    private val characterRepository = CharacterRepositoryDouble()
    private val character = makeCharacter().also(characterRepository::givenCharacter)

    @Test
    fun `created name variants are listed`() {
        val variant = characterName()
        val creationResponse = addCharacterNameVariant(variant)
        val variants = listCharacterNameVariants()
        assertTrue(variants.contains(creationResponse.characterNameVariantAdded.newVariant))
    }

    private fun addCharacterNameVariant(variant: NonBlankString): AddCharacterNameVariant.ResponseModel = runBlocking {
        val output = object : AddCharacterNameVariant.OutputPort {
            lateinit var result: AddCharacterNameVariant.ResponseModel
            override suspend fun addedCharacterNameVariant(response: AddCharacterNameVariant.ResponseModel) {
                result = response
            }
        }
        AddCharacterNameVariantUseCase(characterRepository).invoke(character.id, variant, output)
        output.result
    }

    private fun listCharacterNameVariants(): ListCharacterNameVariants.ResponseModel = runBlocking {
        lateinit var result: ListCharacterNameVariants.ResponseModel
        ListCharacterNameVariantsUseCase(characterRepository).invoke(character.id) {
            result = it
        }
        result
    }

}