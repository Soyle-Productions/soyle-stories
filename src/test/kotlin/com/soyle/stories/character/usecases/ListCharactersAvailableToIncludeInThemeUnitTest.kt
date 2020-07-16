package com.soyle.stories.character.usecases

import com.soyle.stories.character.usecases.listCharactersAvailableToIncludeInTheme.CharactersAvailableToIncludeInTheme
import com.soyle.stories.character.usecases.listCharactersAvailableToIncludeInTheme.ListCharactersAvailableToIncludeInTheme
import com.soyle.stories.character.usecases.listCharactersAvailableToIncludeInTheme.ListCharactersAvailableToIncludeInThemeUseCase
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.character.doubles.CharacterRepositoryDouble
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.theme.MinorCharacter
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.themeDoesNotExist
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.assertThrows
import java.util.*

class ListCharactersAvailableToIncludeInThemeUnitTest {

    private val projectId = Project.Id()
    private val themeId = Theme.Id()

    private var availableCharacters: CharactersAvailableToIncludeInTheme? = null

    @Test
    fun `theme doesn't exist`() {
        assertThrows<ThemeDoesNotExist> {
            listCharactersAvailableToIncludeInTheme()
        } shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    inner class `Theme Exists` {

        private val expectedCharacters: Map<Character.Id, Character>

        init {
            givenTheme()
            givenCharacters(List(4) { Character.Id() })
            expectedCharacters = characterRepository.characters
            listCharactersAvailableToIncludeInTheme()
            availableCharacters shouldBe charactersAvailableToIncludeInTheme {
                assertEquals(themeId.uuid, it.themeId) { "Unexpected theme id for CharactersAvailableToIncludeInTheme" }
            }
        }

        @Test
        fun `check all characters in result`() {
            availableCharacters shouldBe charactersAvailableToIncludeInTheme {
                assertEquals(
                    expectedCharacters.keys,
                    it.map { Character.Id(it.characterId) }.toSet()
                )
            }
        }

        @Test
        fun `check characters have correct names`() {
            availableCharacters shouldBe charactersAvailableToIncludeInTheme {
                it.forEach {
                    assertEquals(expectedCharacters[Character.Id(it.characterId)]!!.name, it.characterName)
                }
            }
        }
    }

    @Test
    fun `filter out already included characters`() {
        givenCharacters(List(6) { Character.Id() })
        val includedCharacters = characterRepository.characters.values.take(2)
        givenTheme(withCharactersIncluded = includedCharacters)
        val expectedCharacters = characterRepository.characters - includedCharacters.map { it.id }
        listCharactersAvailableToIncludeInTheme()
        availableCharacters shouldBe charactersAvailableToIncludeInTheme {
            assertEquals(themeId.uuid, it.themeId) { "Unexpected theme id for CharactersAvailableToIncludeInTheme" }
            assertEquals(
                expectedCharacters.keys,
                it.map { Character.Id(it.characterId) }.toSet()
            )
        }
    }

    private val themeRepository = ThemeRepositoryDouble()
    private val characterRepository = CharacterRepositoryDouble()

    private fun givenTheme(withCharactersIncluded: List<Character> = listOf()) {
        themeRepository.themes[themeId] = makeTheme(themeId, projectId = projectId, includedCharacters = withCharactersIncluded.associate {
            it.id to MinorCharacter(it.id, it.name, "", "", listOf())
        })
    }

    private fun givenCharacters(ids: List<Character.Id>) {
        ids.forEach {
            characterRepository.characters[it] = makeCharacter(it, projectId, "Character ${UUID.randomUUID().toString().take(2)}")
        }
    }

    private fun listCharactersAvailableToIncludeInTheme() {
        val useCase: ListCharactersAvailableToIncludeInTheme =
            ListCharactersAvailableToIncludeInThemeUseCase(themeRepository, characterRepository)
        val output = object : ListCharactersAvailableToIncludeInTheme.OutputPort {
            override suspend fun availableCharactersToIncludeInThemeListed(response: CharactersAvailableToIncludeInTheme) {
                availableCharacters = response
            }
        }
        runBlocking {
            useCase.invoke(themeId.uuid, output)
        }
    }

}

fun charactersAvailableToIncludeInTheme(assertions: (CharactersAvailableToIncludeInTheme) -> Unit) = fun(actual: Any?) {
    actual as CharactersAvailableToIncludeInTheme
    assertions(actual)
}