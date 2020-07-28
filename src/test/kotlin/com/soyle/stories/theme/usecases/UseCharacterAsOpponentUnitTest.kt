package com.soyle.stories.theme.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.shouldBe
import com.soyle.stories.common.str
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.StoryFunction
import com.soyle.stories.theme.*
import com.soyle.stories.theme.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.repositories.ThemeRepository
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.OpponentCharacter
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.UseCharacterAsOpponent
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.UseCharacterAsOpponentUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UseCharacterAsOpponentUnitTest {

    private val themeId = Theme.Id()
    private val perspectiveCharacter = makeCharacter()
    private val opponent = makeCharacter()

    private var updatedTheme: Theme? = null
    private var opponentCharacter: OpponentCharacter? = null

    @Nested
    inner class Degenerates {

        @AfterEach
        fun `check no output`() {
            assertNull(opponentCharacter)
            assertNull(updatedTheme)
        }

        private inline fun <reified T : Throwable> degenerateTest() = assertThrows<T> { useCharacterAsOpponent() }

        @Test
        fun `theme does not exist`() {
            degenerateTest<ThemeDoesNotExist>() shouldBe themeDoesNotExist(themeId.uuid)
        }

        @Test
        fun `perspective character not in theme`() {
            givenThemeExists()
            degenerateTest<CharacterNotInTheme>() shouldBe
                    characterNotInTheme(themeId.uuid, perspectiveCharacter.id.uuid)
        }

        @Test
        fun `perspective character has no perspective in theme`() {
            givenThemeExists()
            givenCharacterInTheme(perspectiveCharacter)
            degenerateTest<CharacterIsNotMajorCharacterInTheme>() shouldBe
                    characterIsNotMajorCharacterInTheme(themeId.uuid, perspectiveCharacter.id.uuid)
        }

        @Test
        fun `opponent is not in theme`() {
            givenThemeExists()
            givenCharacterInTheme(perspectiveCharacter, isMajorCharacter = true)
            degenerateTest<CharacterNotInTheme>() shouldBe
                    characterNotInTheme(themeId.uuid, opponent.id.uuid)
        }

    }

    @Test
    fun `no previous values`() {
        givenThemeExists()
        givenCharacterInTheme(perspectiveCharacter, isMajorCharacter = true)
        givenCharacterInTheme(opponent)
        useCharacterAsOpponent()
        updatedTheme!! shouldBe themeWithCharacterAsOpponent()
        opponentCharacter!! shouldBe opponent()
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = {
        updatedTheme = it
    })

    private fun givenThemeExists() {
        themeRepository.themes[themeId] = makeTheme(themeId)
    }

    private fun givenCharacterInTheme(character: Character, isMajorCharacter: Boolean = false) {
        themeRepository.themes[themeId] = themeRepository.themes[themeId]!!
            .withCharacterIncluded(character.id, character.name, character.media).let {
                if (isMajorCharacter) it.withCharacterPromoted(character.id)
                else it
            }
    }

    private val useCase: UseCharacterAsOpponent = UseCharacterAsOpponentUseCase(themeRepository)
    private val output = object : UseCharacterAsOpponent.OutputPort {
        override suspend fun characterIsOpponent(response: OpponentCharacter) {
            opponentCharacter = response
        }
    }

    private fun useCharacterAsOpponent() = runBlocking {
        useCase.invoke(
            UseCharacterAsOpponent.RequestModel(themeId.uuid, perspectiveCharacter.id.uuid, opponent.id.uuid),
            output
        )
    }

    private fun themeWithCharacterAsOpponent() = fun(actual: Any?) {
        actual as Theme
        assertEquals(themeId, actual.id)
        assertEquals(
            StoryFunction.Antagonist,
            actual.getMajorCharacterById(perspectiveCharacter.id)!!.getStoryFunctionsForCharacter(opponent.id)
        )
    }

    private fun opponent() = fun(actual: Any?) {
        actual as OpponentCharacter
        assertEquals(opponent.id.uuid, actual.characterId)
        assertEquals(opponent.name, actual.characterName)
        assertEquals(perspectiveCharacter.id.uuid, actual.opponentOfCharacterId)
    }

}