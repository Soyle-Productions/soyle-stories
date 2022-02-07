package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.theme.listAvailablePerspectiveCharacters.AvailablePerspectiveCharacters
import com.soyle.stories.usecase.theme.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharacters
import com.soyle.stories.usecase.theme.listAvailablePerspectiveCharacters.ListAvailablePerspectiveCharactersUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ListAvailablePerspectiveCharactersUnitTest {

    private val themeId = Theme.Id()

    private var availablePerspectiveCharacters: AvailablePerspectiveCharacters? = null

    @Test
    fun `theme doesn't exist`() {
        assertThrows<ThemeDoesNotExist> {
            listAvailablePerspectiveCharacters()
        } shouldBe themeDoesNotExist(themeId.uuid)
    }

    @Test
    fun `no characters in theme`() {
        givenThemeExists()
        listAvailablePerspectiveCharacters()
        availablePerspectiveCharacters!! shouldBe listOfSize(0)
    }

    @Test
    fun `no major characters in theme`() {
        givenThemeExists(includedCharacterCount = 3)
        listAvailablePerspectiveCharacters()
        availablePerspectiveCharacters!! shouldBe listOfSize(3)
        availablePerspectiveCharacters!! shouldBe listOfMinorCharacters(3)
        availablePerspectiveCharacters!! shouldBe listOfMajorCharacters(0)
    }

    @Test
    fun `major characters in theme`() {
        givenThemeExists(majorCharacterCount = 4)
        listAvailablePerspectiveCharacters()
        availablePerspectiveCharacters!! shouldBe listOfSize(4)
        availablePerspectiveCharacters!! shouldBe listOfMinorCharacters(0)
        availablePerspectiveCharacters!! shouldBe listOfMajorCharacters(4)
    }

    private val themeRepository = ThemeRepositoryDouble()

    private fun givenThemeExists(includedCharacterCount: Int = 0, majorCharacterCount: Int = 0) {
        themeRepository.themes[themeId] = makeTheme(themeId).let {
            (1..includedCharacterCount).fold(it) { a, b ->
                val character = makeCharacter()
                a.withCharacterIncluded(character.id, character.displayName.value, character.media)
            }
        }.let {
            (1..majorCharacterCount).fold(it) { a, b ->
                val character = makeCharacter()
                a.withCharacterIncluded(character.id, character.displayName.value, character.media)
                    .withCharacterPromoted(character.id)
            }
        }
    }

    private fun listAvailablePerspectiveCharacters() {
        val useCase: ListAvailablePerspectiveCharacters = ListAvailablePerspectiveCharactersUseCase(themeRepository)
        val output = object : ListAvailablePerspectiveCharacters.OutputPort {
            override suspend fun receiveAvailablePerspectiveCharacters(response: AvailablePerspectiveCharacters) {
                availablePerspectiveCharacters = response
            }
        }
        runBlocking {
            useCase.invoke(themeId.uuid, output)
        }
    }

    private fun listOfSize(expectedSize: Int): (AvailablePerspectiveCharacters) -> Unit {
        return {
            assertEquals(themeId.uuid, it.themeId)
            assertEquals(expectedSize, it.size)
            val expectedCharacters = themeRepository.themes[themeId]!!.characters.associateBy { it.id.uuid }
            it.forEach {
                val expectedCharacter = expectedCharacters.getValue(it.characterId)
                assertEquals(expectedCharacter.name, it.characterName)
            }
        }
    }

    private fun listOfMinorCharacters(expectedSize: Int) = fun (list: AvailablePerspectiveCharacters) {
        val minorCharacters = list.filterNot { it.isMajorCharacter }
        assertEquals(expectedSize, minorCharacters.size)
    }

    private fun listOfMajorCharacters(expectedSize: Int) = fun (list: AvailablePerspectiveCharacters) {
        val majorCharacters = list.filter { it.isMajorCharacter }
        assertEquals(expectedSize, majorCharacters.size)
    }

}