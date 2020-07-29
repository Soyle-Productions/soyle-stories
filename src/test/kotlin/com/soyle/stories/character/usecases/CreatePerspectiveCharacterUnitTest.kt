package com.soyle.stories.character.usecases

import com.soyle.stories.character.usecases.buildNewCharacter.CreatedCharacter
import com.soyle.stories.character.usecases.createPerspectiveCharacter.CreatePerspectiveCharacter
import com.soyle.stories.character.usecases.createPerspectiveCharacter.CreatePerspectiveCharacterUseCase
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.entities.theme.MinorCharacter
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.themeDoesNotExist
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
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
    private val characterName = "Character ${str()}"

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
        expectedName: String
    ) = fun(character: Character) {
        assertEquals(expectedProjectId, character.projectId)
        assertEquals(expectedName, character.name)
    }

    private fun themeWithCharacter(character: Character, asMajorCharacter: Boolean = false) = fun(theme: Theme) {
        val includedCharacter =
            theme.getIncludedCharacterById(character.id) ?: error("theme does not contain expected character")
        assertEquals(character.name, includedCharacter.name)
        if (asMajorCharacter && includedCharacter !is MajorCharacter) error("expected character to be a major character")
        else if (!asMajorCharacter && includedCharacter !is MinorCharacter) error("expected character to be a minor character")
    }

    private fun createdCharacterBasedOn(character: Character) = fun(createdCharacter: CreatedCharacter) {
        assertEquals(character.id.uuid, createdCharacter.characterId)
        assertEquals(character.name, createdCharacter.characterName)
        assertEquals(character.media?.uuid, createdCharacter.mediaId)
    }

    private fun majorCharacterInThemeBasedOn(theme: Theme, character: Character) =
        fun(includedCharacter: CharacterIncludedInTheme) {
            assertEquals(theme.id.uuid, includedCharacter.themeId)
            assertEquals(theme.name, includedCharacter.themeName)
            assertEquals(character.id.uuid, includedCharacter.characterId)
            assertEquals(character.name, includedCharacter.characterName)
            assertTrue(includedCharacter.isMajorCharacter)
        }

}