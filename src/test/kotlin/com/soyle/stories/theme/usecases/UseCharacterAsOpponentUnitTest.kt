package com.soyle.stories.theme.usecases

import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.StoryFunction
import com.soyle.stories.storyevent.characterDoesNotExist
import com.soyle.stories.theme.*
import com.soyle.stories.doubles.CharacterRepositoryDouble
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.useCharacterAsOpponent.CharacterUsedAsOpponent
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
    private var opponentCharacter: CharacterUsedAsOpponent? = null
    private var includedCharacter: CharacterIncludedInTheme? = null

    @Nested
    inner class Degenerates {

        @AfterEach
        fun `check no output`() {
            assertNull(opponentCharacter)
            assertNull(includedCharacter)
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
        fun `opponent character not in theme and doesn't exist`() {
            givenThemeExists()
            givenCharacterInTheme(perspectiveCharacter, isMajorCharacter = true)
            degenerateTest<CharacterDoesNotExist>() shouldBe characterDoesNotExist(opponent.id.uuid)
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
        assertNull(includedCharacter)
    }

    @Test
    fun `opponent is not in theme`() {
        givenThemeExists()
        givenCharacterInTheme(perspectiveCharacter, isMajorCharacter = true)
        givencharacterExists(opponent)
        useCharacterAsOpponent()
        updatedTheme!! shouldBe themeWithCharacter(opponent)
        updatedTheme!! shouldBe themeWithCharacterAsOpponent()
        includedCharacter!! shouldBe includedCharacterInTheme(opponent, themeRepository.themes.getValue(themeId))
        opponentCharacter!! shouldBe opponent()
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = {
        updatedTheme = it
    })
    private val characterRepository = CharacterRepositoryDouble()

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

    private fun givencharacterExists(character: Character) {
        characterRepository.characters[character.id] = character
    }

    private val useCase: UseCharacterAsOpponent = UseCharacterAsOpponentUseCase(themeRepository, characterRepository)
    private val output = object : UseCharacterAsOpponent.OutputPort {
        override suspend fun characterIsOpponent(response: UseCharacterAsOpponent.ResponseModel) {
            opponentCharacter = response.characterAsOpponent
            includedCharacter = response.includedCharacter
        }
    }

    private fun useCharacterAsOpponent() = runBlocking {
        useCase.invoke(
            UseCharacterAsOpponent.RequestModel(themeId.uuid, perspectiveCharacter.id.uuid, opponent.id.uuid),
            output
        )
    }

    private fun themeWithCharacter(expectedCharacter: Character) = fun (actual: Theme) {
        val includedCharacter = actual.characters.single { it.id == expectedCharacter.id }
        assertEquals(expectedCharacter.name, includedCharacter.name)
    }

    private fun themeWithCharacterAsOpponent() = fun(actual: Any?) {
        actual as Theme
        assertEquals(themeId, actual.id)
        assertEquals(
            StoryFunction.Antagonist,
            actual.getMajorCharacterById(perspectiveCharacter.id)!!.getStoryFunctionsForCharacter(opponent.id)
        )
    }

    private fun opponent() = opponent(
        opponent.id.uuid,
        opponent.name,
        perspectiveCharacter.id.uuid,
        themeId.uuid,
        false
    )

}