package com.soyle.stories.usecase.character.name

import com.soyle.stories.domain.character.characterName
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.character.name.events.CharacterRenamed
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.prose.events.MentionTextReplaced
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.name.create.AddCharacterNameVariant
import com.soyle.stories.usecase.character.name.create.AddCharacterNameVariantUseCase
import com.soyle.stories.usecase.character.name.list.ListCharacterNameVariants
import com.soyle.stories.usecase.character.name.list.ListCharacterNameVariantsUseCase
import com.soyle.stories.usecase.character.name.remove.RemoveCharacterNameVariant
import com.soyle.stories.usecase.character.name.remove.RemoveCharacterNameVariantUseCase
import com.soyle.stories.usecase.character.name.rename.RenameCharacter
import com.soyle.stories.usecase.character.name.rename.RenameCharacterUseCase
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ProseRepositoryDouble
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
        assertTrue(variants.contains(creationResponse.characterNameAdded.name))
    }

    @Test
    fun `can rename created variants`() {
        val variant = characterName()
        val creationResponse = addCharacterNameVariant(variant)
        val rename = characterName()
        val renameResponse = renameCharacterNameVariant(
            NonBlankString.create(creationResponse.characterNameAdded.name)!!,
            rename
        )
        renameResponse.oldName.mustEqual(variant)
        renameResponse.name.mustEqual(rename)
    }

    @Test
    fun `can delete listed variants`() {
        val variant = characterName()
        addCharacterNameVariant(variant)
        val variants = listCharacterNameVariants()
        val listedVariant = variants.find { it == variant.value }!!
        val removalResponse = removeCharacterNameVariant(nonBlankStr(listedVariant))
        removalResponse.characterNameRemoved.characterId.mustEqual(character.id)
        removalResponse.characterNameRemoved.name.mustEqual(listedVariant)
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

    private fun renameCharacterNameVariant(
        originalVariant: NonBlankString,
        nextVariant: NonBlankString
    ): CharacterRenamed {
        lateinit var result: CharacterRenamed
        val request = RenameCharacter.RequestModel(character.id, originalVariant, nextVariant)
        runBlocking {
            RenameCharacterUseCase(characterRepository, ProseRepositoryDouble()).invoke(request, object: RenameCharacter.OutputPort {
                override suspend fun characterRenamed(characterRenamed: CharacterRenamed) {
                    result = characterRenamed
                }

                override suspend fun mentionTextReplaced(mentionTextReplaced: MentionTextReplaced) {

                }
            })
        }
        return result
    }

    private fun removeCharacterNameVariant(
        variant: NonBlankString
    ): RemoveCharacterNameVariant.ResponseModel {
        lateinit var result: RemoveCharacterNameVariant.ResponseModel
        runBlocking {
            RemoveCharacterNameVariantUseCase(characterRepository).invoke(character.id, variant) {
                result = it
            }
        }
        return result
    }

}