package com.soyle.stories.theme.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.shouldBe
import com.soyle.stories.doubles.ThemeRepositoryDouble
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.StoryFunction
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.useCharacterAsMainOpponent.UseCharacterAsMainOpponent
import com.soyle.stories.theme.usecases.useCharacterAsMainOpponent.UseCharacterAsMainOpponentUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UseCharacterAsMainOpponentUnitTest {

    // preconditions
    private val theme = makeTheme()
    private val perspectiveCharacter = makeCharacter()
    private val opponentCharacter = makeCharacter()

    // input parameters
    private val themeId = theme.id.uuid
    private val perspectiveCharacterId = perspectiveCharacter.id.uuid
    private val opponentCharacterId = opponentCharacter.id.uuid

    // effects
    private var updatedTheme: Theme? = null

    // output
    private var responseModel: UseCharacterAsMainOpponent.ResponseModel? = null

    @Nested
    inner class Degenerates {

        private inline fun <reified T : Throwable> degenerate(): T {
            val t = assertThrows<T> {
                useCharacterAsMainOpponent()
            }
            assertNull(updatedTheme)
            assertNull(responseModel)
            return t
        }

        @Test
        fun `theme doesn't exist`() {
            degenerate<ThemeDoesNotExist>() shouldBe themeDoesNotExist(themeId)
        }

        @Test
        fun `perspective character not in theme`() {
            givenTheme()
            degenerate<CharacterNotInTheme>() shouldBe characterNotInTheme(themeId, perspectiveCharacterId)
        }

        @Test
        fun `perspective character not major character`() {
            givenTheme()
            givenCharacterInTheme(perspectiveCharacter)
            degenerate<CharacterIsNotMajorCharacterInTheme>() shouldBe
                    characterIsNotMajorCharacterInTheme(themeId, perspectiveCharacterId)
        }

        @Test
        fun `opponent character not in theme`() {
            givenTheme()
            givenCharacterInTheme(perspectiveCharacter, asMajorCharacter = true)
            degenerate<CharacterNotInTheme>() shouldBe characterNotInTheme(themeId, opponentCharacterId)
        }

        @Test
        fun `character already main opponent`() {
            givenTheme()
            givenCharacterInTheme(perspectiveCharacter, asMajorCharacter = true)
            givenCharacterInTheme(opponentCharacter)
            givenCharacterIsMainOpponentTo(opponentCharacter, perspectiveCharacter)
            degenerate<StoryFunctionAlreadyApplied>()
        }

    }

    @Test
    fun `happy path`() {
        givenTheme()
        givenCharacterInTheme(perspectiveCharacter, asMajorCharacter = true)
        givenCharacterInTheme(opponentCharacter)
        useCharacterAsMainOpponent()
        updatedTheme!! shouldBe themeWithCharacterAsMainOpponentTo(opponentCharacter, perspectiveCharacter)
        responseModel!!.let {
            it.mainOpponent shouldBe opponent(
                opponentCharacterId,
                opponentCharacter.name,
                perspectiveCharacterId,
                themeId,
                true
            )
            assertNull(it.previousMainOpponent)
        }
    }

    @Test
    fun `other character already main opponent`() {
        val otherCharacter = makeCharacter()
        givenTheme()
        givenCharacterInTheme(perspectiveCharacter, asMajorCharacter = true)
        givenCharacterInTheme(opponentCharacter)
        givenCharacterIsMainOpponentTo(otherCharacter, perspectiveCharacter)
        useCharacterAsMainOpponent()
        updatedTheme!! shouldBe themeWithCharacterAsMainOpponentTo(opponentCharacter, perspectiveCharacter)
        updatedTheme!! shouldBe themeWithCharacterAsOpponentTo(otherCharacter, perspectiveCharacter)
        responseModel!!.let {
            it.mainOpponent shouldBe opponent(
                opponentCharacterId,
                opponentCharacter.name,
                perspectiveCharacterId,
                themeId,
                true
            )
            it.previousMainOpponent!! shouldBe opponent(
                otherCharacter.id.uuid,
                otherCharacter.name,
                perspectiveCharacterId,
                themeId,
                false
            )
        }
    }

    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = {
        updatedTheme = it
    })

    private fun givenTheme() {
        themeRepository.themes[theme.id] = theme
    }

    private fun givenCharacterInTheme(character: Character, asMajorCharacter: Boolean = false) {
        themeRepository.themes[theme.id] = themeRepository.themes.getValue(theme.id)
            .withCharacterIncluded(character.id, character.name, character.media)
            .let {
                if (asMajorCharacter) it.withCharacterPromoted(character.id)
                else it
            }
    }

    private fun givenCharacterIsMainOpponentTo(character: Character, perspectiveCharacter: Character) {
        if (!themeRepository.themes.getValue(theme.id).containsCharacter(character.id)) givenCharacterInTheme(character)
        themeRepository.themes[theme.id] = themeRepository.themes.getValue(theme.id)
            .withCharacterAsStoryFunctionForMajorCharacter(
                character.id, StoryFunction.MainAntagonist, perspectiveCharacter.id
            )
    }

    private fun useCharacterAsMainOpponent() {
        val useCase: UseCharacterAsMainOpponent = UseCharacterAsMainOpponentUseCase(themeRepository)
        val output = object : UseCharacterAsMainOpponent.OutputPort {
            override suspend fun characterUsedAsMainOpponent(response: UseCharacterAsMainOpponent.ResponseModel) {
                responseModel = response
            }
        }
        runBlocking {
            useCase.invoke(
                UseCharacterAsMainOpponent.RequestModel(themeId, perspectiveCharacterId, opponentCharacterId),
                output
            )
        }
    }

    private fun themeWithCharacterAsMainOpponentTo(opponentCharacter: Character, perspectiveCharacter: Character) =
        fun(theme: Theme) {
            val majorCharacter = theme.getMajorCharacterById(perspectiveCharacter.id)!!
            val storyFunction = majorCharacter.getStoryFunctionsForCharacter(opponentCharacter.id)!!
            assertEquals(StoryFunction.MainAntagonist, storyFunction)
            theme.characters.asSequence().filterNot { it.id == opponentCharacter.id }.forEach {
                assertNotEquals(StoryFunction.MainAntagonist, majorCharacter.getStoryFunctionsForCharacter(it.id)) {
                    "Only ${opponentCharacter.name} should be the main antagonist.  Instead, ${it.name} was found to also " +
                            "be a main antagonist"
                }
            }
        }

    private fun themeWithCharacterAsOpponentTo(opponentCharacter: Character, perspectiveCharacter: Character) =
        fun(theme: Theme) {
            val majorCharacter = theme.getMajorCharacterById(perspectiveCharacter.id)!!
            val storyFunction = majorCharacter.getStoryFunctionsForCharacter(opponentCharacter.id)!!
            assertEquals(StoryFunction.Antagonist, storyFunction)
        }

}