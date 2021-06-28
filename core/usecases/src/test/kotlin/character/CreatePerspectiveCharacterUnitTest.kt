package com.soyle.stories.usecase.character

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.str
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.domain.theme.characterInTheme.MinorCharacter
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.character.buildNewCharacter.CreatedCharacter
import com.soyle.stories.usecase.character.createPerspectiveCharacter.CreatePerspectiveCharacter
import com.soyle.stories.usecase.character.createPerspectiveCharacter.CreatePerspectiveCharacterUseCase
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.ThemeDoesNotExist
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.themeDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CreatePerspectiveCharacterUnitTest {

    // preconditions
    private val theme = makeTheme()

    // input data
    private val themeUUID = theme.id.uuid
    private val characterName = NonBlankString.create("Character ${str()}")!!

    // persisted data
    private var createdCharacter: Character? = null
    private var updatedTheme: Theme? = null

    // output data
    private var responseModel: CreatePerspectiveCharacter.ResponseModel? = null

    @Test
    fun `theme doesn't exist`() {
        assertThrows<ThemeDoesNotExist> {
            createPerspectiveCharacter()
        } shouldBe themeDoesNotExist(themeUUID)
    }

    @Test
    fun `theme exists`() {
        givenTheme()
        createPerspectiveCharacter()
        createdCharacter!! shouldBe character(theme.projectId, characterName)
        updatedTheme!! shouldBe themeWithCharacter(createdCharacter!!, asMajorCharacter = true)
        responseModel!!.createdCharacter shouldBe createdCharacterBasedOn(createdCharacter!!)
        responseModel!!.characterIncludedInTheme shouldBe majorCharacterInThemeBasedOn(
            updatedTheme!!,
            createdCharacter!!
        )
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = {
        updatedTheme = it
    })
    private val characterRepository = CharacterRepositoryDouble(onAddNewCharacter = {
        createdCharacter = it
    })

    private fun givenTheme() {
        themeRepository.themes[theme.id] = theme
    }

    private fun createPerspectiveCharacter() {
        val useCase: CreatePerspectiveCharacter =
            CreatePerspectiveCharacterUseCase(themeRepository, characterRepository)
        val output = object : CreatePerspectiveCharacter.OutputPort {
            override suspend fun createdPerspectiveCharacter(response: CreatePerspectiveCharacter.ResponseModel) {
                responseModel = response
            }
        }
        runBlocking {
            useCase.invoke(themeUUID, characterName, output)
        }
    }

    private fun character(
        expectedProjectId: Project.Id,
        expectedName: NonBlankString
    ) = fun(character: Character) {
        assertEquals(expectedProjectId, character.projectId)
        assertEquals(expectedName, character.name)
    }

    private fun themeWithCharacter(character: Character, asMajorCharacter: Boolean = false) = fun(theme: Theme) {
        val includedCharacter =
            theme.getIncludedCharacterById(character.id) ?: error("theme does not contain expected character")
        assertEquals(character.name.value, includedCharacter.name)
        if (asMajorCharacter && includedCharacter !is MajorCharacter) error("expected character to be a major character")
        else if (!asMajorCharacter && includedCharacter !is MinorCharacter) error("expected character to be a minor character")
    }

    private fun createdCharacterBasedOn(character: Character) = fun(createdCharacter: CreatedCharacter) {
        assertEquals(character.id.uuid, createdCharacter.characterId)
        assertEquals(character.name.value, createdCharacter.characterName)
        assertEquals(character.media?.uuid, createdCharacter.mediaId)
    }

    private fun majorCharacterInThemeBasedOn(theme: Theme, character: Character) =
        fun(includedCharacter: CharacterIncludedInTheme) {
            assertEquals(theme.id.uuid, includedCharacter.themeId)
            assertEquals(theme.name, includedCharacter.themeName)
            assertEquals(character.id.uuid, includedCharacter.characterId)
            assertEquals(character.name.value, includedCharacter.characterName)
            assertTrue(includedCharacter.isMajorCharacter)
        }

}