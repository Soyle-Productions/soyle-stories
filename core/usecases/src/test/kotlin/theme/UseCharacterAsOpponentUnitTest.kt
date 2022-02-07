package com.soyle.stories.usecase.theme

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.shouldBe
import com.soyle.stories.domain.theme.*
import com.soyle.stories.domain.theme.characterInTheme.StoryFunction
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.useCharacterAsOpponent.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UseCharacterAsOpponentUnitTest {

    private val theme = makeTheme()
    private val projectId = theme.projectId
    private val perspectiveCharacter = makeCharacter(projectId = projectId)
    private val opponent = makeCharacter(projectId = projectId)

    private val perspectiveCharacterId = perspectiveCharacter.id
    private val themeId = theme.id

    private var updatedTheme: Theme? = null

    private var result: Any? = null


    private val themeRepository = ThemeRepositoryDouble(onUpdateTheme = ::updatedTheme::set)
    private val characterRepository = CharacterRepositoryDouble()
    private val useCharacterAsOpponent = UseCharacterAsOpponentUseCase(themeRepository, characterRepository)

    @Nested
    inner class `List Available Characters to Use as Opponent` {

        private val useCase: ListAvailableCharactersToUseAsOpponents = useCharacterAsOpponent
        private val output = object : ListAvailableCharactersToUseAsOpponents.OutputPort {
            override suspend fun receiveAvailableCharactersToUseAsOpponents(response: AvailableCharactersToUseAsOpponents) {
                result = response
            }
        }

        @Test
        fun `theme doesn't exist`() {
            val error = degenerateTest<ThemeDoesNotExist> {
                listAvailableCharactersToUseAsOpponents()
            }
            error.themeId.mustEqual(themeId.uuid)
        }

        @Test
        fun `perspective character is not in theme`() {
            themeRepository.givenTheme(theme)

            val error = degenerateTest<CharacterNotInTheme> {
                listAvailableCharactersToUseAsOpponents()
            }
            error.themeId.mustEqual(themeId.uuid)
            error.characterId.mustEqual(perspectiveCharacterId.uuid)
        }

        @Test
        fun `perspective character is only minor character`() {
            themeRepository.givenTheme(
                theme.withCharacterIncluded(perspectiveCharacter)
            )

            val error = degenerateTest<CharacterIsNotMajorCharacterInTheme> {
                listAvailableCharactersToUseAsOpponents()
            }
            error.themeId.mustEqual(themeId.uuid)
            error.characterId.mustEqual(perspectiveCharacterId.uuid)
        }

        @Nested
        inner class `No Other Characters in Theme` {

            init {
                themeRepository.givenTheme(
                    theme.withCharacterIncluded(perspectiveCharacter).withCharacterPromoted(perspectiveCharacter.id)
                )
            }

            @Test
            fun `output should be empty`() {
                listAvailableCharactersToUseAsOpponents()

                val result = result as AvailableCharactersToUseAsOpponents
                assertEquals(themeId.uuid, result.themeId)
                assertEquals(perspectiveCharacterId.uuid, result.perspectiveCharacterId)
                assertTrue(result.isEmpty())
            }

        }

        @Nested
        inner class `Other Characters in Theme` {

            private val otherIncludedCharacters = List(4) { makeCharacter() }

            init {
                themeRepository.givenTheme(
                    theme.withCharacterIncluded(perspectiveCharacter).withCharacterPromoted(perspectiveCharacter.id).let {
                        otherIncludedCharacters.fold(it) { nextTheme, character ->
                            nextTheme.withCharacterIncluded(character.id, character.displayName.value, character.media)
                        }
                    }
                )
            }

            @Test
            fun `should output all other characters`() {
                listAvailableCharactersToUseAsOpponents()

                val result = result as AvailableCharactersToUseAsOpponents
                result.size.mustEqual(4)
                result.map { it.characterId }.toSet()
                    .mustEqual(otherIncludedCharacters.map { it.id.uuid }.toSet())
            }

            @Test
            fun `should output names for each character`() {
                listAvailableCharactersToUseAsOpponents()

                val result = result as AvailableCharactersToUseAsOpponents
                result.forEach { availableCharacter ->
                    val backingCharacter =
                        otherIncludedCharacters.find { it.id.uuid == availableCharacter.characterId }!!
                    availableCharacter.characterName.mustEqual(backingCharacter.displayName.value)
                }
            }

            @Test
            fun `each included character should be included in theme`() {
                listAvailableCharactersToUseAsOpponents()

                val result = result as AvailableCharactersToUseAsOpponents
                result.forEach { availableCharacter ->
                    availableCharacter.includedInTheme.mustEqual(true)
                }
            }

            @Test
            fun `should only output characters that are not yet opponents`() {
                themeRepository.givenTheme(
                    themeRepository.themes.getValue(theme.id)
                        .withCharacterAsStoryFunctionForMajorCharacter(
                            otherIncludedCharacters[0].id,
                            StoryFunction.Antagonist,
                            perspectiveCharacter.id
                        )
                        .withCharacterAsStoryFunctionForMajorCharacter(
                            otherIncludedCharacters[1].id,
                            StoryFunction.MainAntagonist,
                            perspectiveCharacter.id
                        )
                )

                listAvailableCharactersToUseAsOpponents()

                val result = result as AvailableCharactersToUseAsOpponents
                result.size.mustEqual(2) { if (result.size > 2) "Too many characters in output" else "Not enough characters in output" }
                result.map { it.characterId }.toSet().contains(otherIncludedCharacters.first().id.uuid).mustEqual(false)
            }

        }

        @Nested
        inner class `Characters not in theme` {

            private val otherCharacters = List(5) { makeCharacter(projectId = projectId) }

            init {
                themeRepository.givenTheme(
                    theme.withCharacterIncluded(perspectiveCharacter).withCharacterPromoted(perspectiveCharacter.id)
                )
                otherCharacters.forEach(characterRepository::givenCharacter)
            }

            @Test
            fun `should output all non-included characters`() {
                listAvailableCharactersToUseAsOpponents()

                val result = result as AvailableCharactersToUseAsOpponents
                result.size.mustEqual(otherCharacters.size)
                result.map { it.characterId }.toSet()
                    .mustEqual(otherCharacters.map { it.id.uuid }.toSet())
            }

            @Test
            fun `should output names for each character`() {
                listAvailableCharactersToUseAsOpponents()

                val result = result as AvailableCharactersToUseAsOpponents
                result.forEach { availableCharacter ->
                    val backingCharacter = otherCharacters.find { it.id.uuid == availableCharacter.characterId }!!
                    availableCharacter.characterName.mustEqual(backingCharacter.displayName.value)
                }
            }

            @Test
            fun `all non-included characters should be excluded from theme`() {
                listAvailableCharactersToUseAsOpponents()

                val result = result as AvailableCharactersToUseAsOpponents
                result.forEach { availableCharacter ->
                    availableCharacter.includedInTheme.mustEqual(false)
                }
            }

        }

        private fun listAvailableCharactersToUseAsOpponents() {
            runBlocking {
                useCase.invoke(themeId.uuid, perspectiveCharacterId.uuid, output)
            }
        }

    }

    @Nested
    inner class `Use Character as Opponent` {

        private val useCase: UseCharacterAsOpponent = useCharacterAsOpponent
        private val output = object : UseCharacterAsOpponent.OutputPort {
            override suspend fun characterIsOpponent(response: UseCharacterAsOpponent.ResponseModel) {
                result = response
            }
        }

        @Nested
        inner class Degenerates {

            @Test
            fun `theme does not exist`() {
                val error = degenerateTest<ThemeDoesNotExist> { useCharacterAsOpponent() }

                error.themeId.mustEqual(themeId.uuid)
            }

            @Test
            fun `perspective character not in theme`() {
                themeRepository.givenTheme(theme)

                val error = degenerateTest<CharacterNotInTheme> { useCharacterAsOpponent() }
                error.themeId.mustEqual(themeId.uuid)
                error.characterId.mustEqual(perspectiveCharacterId.uuid)
            }

            @Test
            fun `perspective character has no perspective in theme`() {
                themeRepository.givenTheme(
                    theme.withCharacterIncluded(perspectiveCharacter)
                )

                val error = degenerateTest<CharacterIsNotMajorCharacterInTheme> { useCharacterAsOpponent() }

                error.themeId.mustEqual(themeId.uuid)
                error.characterId.mustEqual(perspectiveCharacterId.uuid)
            }

            @Test
            fun `opponent character not in theme and doesn't exist`() {
                themeRepository.givenTheme(
                    theme.withCharacterIncluded(perspectiveCharacter).withCharacterPromoted(perspectiveCharacter.id)
                )

                val error = degenerateTest<CharacterDoesNotExist> { useCharacterAsOpponent() }

                error.characterId.mustEqual(opponent.id)
            }

        }

        @Nested
        inner class `Opponent not in Theme` {

            init {
                themeRepository.givenTheme(
                    theme
                        .withCharacterIncluded(perspectiveCharacter)
                        .withCharacterPromoted(perspectiveCharacter.id)
                )
                characterRepository.givenCharacter(opponent)
            }

            @Test
            fun `should report character used as opponent`() {
                useCharacterAsOpponent()

                updatedTheme!! shouldBe themeWithCharacterAsOpponent()
                val result = result as UseCharacterAsOpponent.ResponseModel
                result.characterAsOpponent shouldBe opponent()
            }

            @Test
            fun `should report character included in theme`() {
                useCharacterAsOpponent()

                updatedTheme!! shouldBe themeWithCharacter(opponent)
                val result = result as UseCharacterAsOpponent.ResponseModel
                result.includedCharacter!! shouldBe includedCharacterInTheme(
                    opponent,
                    themeRepository.themes.getValue(themeId)
                )
            }

        }

        @Nested
        inner class `No Previous Values` {

            init {
                themeRepository.givenTheme(
                    theme
                        .withCharacterIncluded(perspectiveCharacter)
                        .withCharacterPromoted(perspectiveCharacter.id)
                        .withCharacterIncluded(opponent)
                )
            }

            @Test
            fun `should not include character`() {
                useCharacterAsOpponent()

                val result = result as UseCharacterAsOpponent.ResponseModel
                assertNull(result.includedCharacter)
            }

            @Test
            fun `should report character used as opponent`() {
                useCharacterAsOpponent()

                updatedTheme!! shouldBe themeWithCharacterAsOpponent()
                val result = result as UseCharacterAsOpponent.ResponseModel
                result.characterAsOpponent shouldBe opponent()
            }

        }

        private fun useCharacterAsOpponent() = runBlocking {
            useCase.invoke(
                UseCharacterAsOpponent.RequestModel(themeId.uuid, perspectiveCharacter.id.uuid, opponent.id.uuid),
                output
            )
        }

    }

    @Nested
    inner class `Use Character as Main Opponent` {

        val useCase: UseCharacterAsMainOpponent = useCharacterAsOpponent
        val output = object : UseCharacterAsMainOpponent.OutputPort {
            override suspend fun characterUsedAsMainOpponent(response: UseCharacterAsMainOpponent.ResponseModel) {
                result = response
            }
        }

        @Nested
        inner class Degenerates {

            @Test
            fun `theme doesn't exist`() {
                val error = degenerateTest<ThemeDoesNotExist> { useCharacterAsMainOpponent() }
                error.themeId.mustEqual(themeId.uuid)
            }

            @Test
            fun `perspective character not in theme`() {
                themeRepository.givenTheme(theme)

                val error = degenerateTest<CharacterNotInTheme> { useCharacterAsMainOpponent() }
                error.themeId.mustEqual(themeId.uuid)
                error.characterId.mustEqual(perspectiveCharacterId.uuid)
            }

            @Test
            fun `perspective character not major character`() {
                themeRepository.givenTheme(
                    theme
                        .withCharacterIncluded(perspectiveCharacter)
                )

                val error = degenerateTest<CharacterIsNotMajorCharacterInTheme> { useCharacterAsMainOpponent() }
                error.themeId.mustEqual(themeId.uuid)
                error.characterId.mustEqual(perspectiveCharacterId.uuid)
            }

            @Test
            fun `opponent character not in theme`() {
                themeRepository.givenTheme(
                    theme
                        .withCharacterIncluded(perspectiveCharacter)
                        .withCharacterPromoted(perspectiveCharacter.id)
                )

                val error = degenerateTest<CharacterNotInTheme> { useCharacterAsMainOpponent() }
                error.themeId.mustEqual(themeId.uuid)
                error.characterId.mustEqual(opponent.id.uuid)
            }

            @Test
            fun `character already main opponent`() {
                themeRepository.givenTheme(
                    theme
                        .withCharacterIncluded(perspectiveCharacter)
                        .withCharacterPromoted(perspectiveCharacter.id)
                        .withCharacterIncluded(opponent)
                        .withCharacterAsStoryFunctionForMajorCharacter(
                            opponent.id, StoryFunction.MainAntagonist, perspectiveCharacter.id
                        )
                )

                val error = degenerateTest<StoryFunctionAlreadyApplied> { useCharacterAsMainOpponent() }

                error.appliedCharacterId.mustEqual(opponent.id.uuid)
                error.perspectiveCharacterId.mustEqual(perspectiveCharacter.id.uuid)
                error.storyFunction.mustEqual(StoryFunction.MainAntagonist)
            }

        }

        @Nested
        inner class `Happy Path` {

            init {
                themeRepository.givenTheme(
                    theme
                        .withCharacterIncluded(perspectiveCharacter)
                        .withCharacterPromoted(perspectiveCharacter.id)
                        .withCharacterIncluded(opponent)
                )
            }

            @Test
            fun `should update theme`() {
                useCharacterAsMainOpponent()

                updatedTheme!! shouldBe themeWithCharacterAsMainOpponentTo(opponent, perspectiveCharacter)
            }

            @Test
            fun `should report new main opponent`() {
                useCharacterAsMainOpponent()

                (result as UseCharacterAsMainOpponent.ResponseModel).mainOpponent shouldBe opponent(
                    opponent.id.uuid,
                    opponent.displayName.value,
                    perspectiveCharacterId.uuid,
                    themeId.uuid,
                    true
                )
            }

            @Test
            fun `should not report previous main opponent`() {
                useCharacterAsMainOpponent()

                val result = result as UseCharacterAsMainOpponent.ResponseModel
                assertNull(result.previousMainOpponent)
            }

        }

        @Nested
        inner class `Another Character is Already the Main Opponent` {

            private val otherCharacter = makeCharacter(projectId = projectId)

            init {
                themeRepository.givenTheme(
                    theme
                        .withCharacterIncluded(perspectiveCharacter)
                        .withCharacterPromoted(perspectiveCharacter.id)
                        .withCharacterIncluded(opponent)
                        .withCharacterIncluded(otherCharacter)
                        .withCharacterAsStoryFunctionForMajorCharacter(
                            otherCharacter.id,
                            StoryFunction.MainAntagonist,
                            perspectiveCharacter.id
                        )
                )
            }


            @Test
            fun `should demote other character to opponent`() {
                useCharacterAsMainOpponent()

                updatedTheme!!
                    .getMajorCharacterById(perspectiveCharacter.id)!!
                    .getStoryFunctionsForCharacter(otherCharacter.id)
                    .mustEqual(StoryFunction.Antagonist)
            }

            @Test
            fun `should report previous main opponent`() {
                useCharacterAsMainOpponent()

                val previousOpponent = (result as UseCharacterAsMainOpponent.ResponseModel).previousMainOpponent!!
                previousOpponent.characterId.mustEqual(otherCharacter.id.uuid)
                previousOpponent.characterName.mustEqual(otherCharacter.displayName.value)
                previousOpponent.opponentOfCharacterId.mustEqual(perspectiveCharacter.id.uuid)
                previousOpponent.themeId.mustEqual(theme.id.uuid)
            }

        }

        private fun useCharacterAsMainOpponent() {
            runBlocking {
                useCase.invoke(
                    UseCharacterAsMainOpponent.RequestModel(
                        themeId.uuid,
                        perspectiveCharacterId.uuid,
                        opponent.id.uuid
                    ),
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
                        "Only ${opponentCharacter.displayName} should be the main antagonist.  Instead, ${it.name} was found to also " +
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


    private fun givenThemeExists() {
        themeRepository.themes[themeId] = makeTheme(themeId, projectId = projectId)
    }

    private fun givenCharacterInTheme(character: Character, isMajorCharacter: Boolean = false) {
        themeRepository.themes[themeId] = themeRepository.themes[themeId]!!
            .withCharacterIncluded(character.id, character.displayName.value, character.media).let {
                if (isMajorCharacter) it.withCharacterPromoted(character.id)
                else it
            }
    }

    private fun givenCharacterIsMainOpponentTo(character: Character, perspectiveCharacter: Character) {
        if (!themeRepository.themes.getValue(themeId).containsCharacter(character.id)) givenCharacterInTheme(character)
        themeRepository.themes[themeId] = themeRepository.themes.getValue(themeId)
            .withCharacterAsStoryFunctionForMajorCharacter(
                character.id, StoryFunction.MainAntagonist, perspectiveCharacter.id
            )
    }


    private fun themeWithCharacter(expectedCharacter: Character) = fun(actual: Theme) {
        val includedCharacter = actual.characters.single { it.id == expectedCharacter.id }
        assertEquals(expectedCharacter.displayName.value, includedCharacter.name)
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
        opponent.displayName.value,
        perspectiveCharacter.id.uuid,
        themeId.uuid,
        false
    )

    private inline fun <reified T : Throwable> degenerateTest(noinline method: () -> Unit): T {
        val t = assertThrows<T> { method() }
        assertNull(result)
        assertNull(updatedTheme)
        return t
    }

}