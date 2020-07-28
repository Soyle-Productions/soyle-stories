package com.soyle.stories.theme.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.theme.*
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents.AvailableCharactersToUseAsOpponents
import com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents.ListAvailableCharactersToUseAsOpponents
import com.soyle.stories.theme.usecases.listAvailableCharactersToUseAsOpponents.ListAvailableCharactersToUseAsOpponentsUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ListAvailableCharactersToUseAsOpponentsUnitTest {

    private val themeId = Theme.Id()
    private val perspectiveCharacterId = Character.Id()

    private var availableCharacters: AvailableCharactersToUseAsOpponents? = null

    private inline fun <reified T : Throwable> degenerateTest(): T {
        val t = assertThrows<T> { listAvailableCharactersToUseAsOpponents() }
        assertNull(availableCharacters)
        return t
    }

    @Test
    fun `theme doesn't exist`() {
        degenerateTest<ThemeDoesNotExist>() shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `perspective character is not in theme`() {
        givenThemeExists()
        degenerateTest<CharacterNotInTheme>() shouldBe characterNotInTheme(themeId.uuid, perspectiveCharacterId.uuid)
    }

    @Test
    fun `perspective character is only minor character`() {
        givenThemeExists()
        givenCharacterInTheme(perspectiveCharacterId)
        degenerateTest<CharacterIsNotMajorCharacterInTheme>() shouldBe
                characterIsNotMajorCharacterInTheme(themeId.uuid, perspectiveCharacterId.uuid)
    }

    @Test
    fun `no other characters in theme`() {
        givenThemeExists()
        givenCharacterInTheme(perspectiveCharacterId, isMajorCharacter = true)
        listAvailableCharactersToUseAsOpponents()
        availableCharacters!! shouldBe ::empty
    }

    @Test
    fun `other characters in theme`() {
        givenThemeExists()
        givenCharacterInTheme(perspectiveCharacterId, isMajorCharacter = true)
        givenOtherCharactersInTheme(count = 4)
        listAvailableCharactersToUseAsOpponents()
        availableCharacters!! shouldBe ::listWithAllCharactersInThemeExceptPerspectiveCharacter
    }

    private val themeRepository = ThemeRepositoryDouble()

    private fun givenThemeExists() {
        themeRepository.themes[themeId] = makeTheme(themeId)
    }
    private fun givenCharacterInTheme(characterId: Character.Id, isMajorCharacter: Boolean = false)
    {
        themeRepository.themes[themeId] = themeRepository.themes.getValue(themeId)
            .withCharacterIncluded(characterId, str(), null).let {
                if (isMajorCharacter) it.withCharacterPromoted(characterId)
                else it
            }
    }
    private fun givenOtherCharactersInTheme(count: Int)
    {
        themeRepository.themes[themeId] = (1..count).fold(themeRepository.themes.getValue(themeId)) { theme, _ ->
            val baseCharacter = makeCharacter()
            theme.withCharacterIncluded(baseCharacter.id, baseCharacter.name, baseCharacter.media)
        }
    }

    private fun listAvailableCharactersToUseAsOpponents()
    {
        val useCase: ListAvailableCharactersToUseAsOpponents = ListAvailableCharactersToUseAsOpponentsUseCase(themeRepository)
        val output = object : ListAvailableCharactersToUseAsOpponents.OutputPort {
            override suspend fun receiveAvailableCharactersToUseAsOpponents(response: AvailableCharactersToUseAsOpponents) {
                availableCharacters = response
            }
        }
        runBlocking {
            useCase.invoke(themeId.uuid, perspectiveCharacterId.uuid, output)
        }
    }

    private fun empty(response: AvailableCharactersToUseAsOpponents)
    {
        assertEquals(themeId.uuid, response.themeId)
        assertEquals(perspectiveCharacterId.uuid, response.perspectiveCharacterId)
        assertTrue(response.isEmpty())
    }

    private fun listWithAllCharactersInThemeExceptPerspectiveCharacter(response: AvailableCharactersToUseAsOpponents) {
        assertEquals(themeId.uuid, response.themeId)
        assertEquals(perspectiveCharacterId.uuid, response.perspectiveCharacterId)
        val theme = themeRepository.themes.getValue(themeId)
        val expectedBackingCharacters = theme.characters.filterNot { it.id == perspectiveCharacterId }.associateBy { it.id.uuid }
        assertEquals(expectedBackingCharacters.size, response.size)
        response.forEach {
            val backingCharacter = expectedBackingCharacters.getValue(it.characterId)
            assertEquals(backingCharacter.name, it.characterName)
        }
    }

}