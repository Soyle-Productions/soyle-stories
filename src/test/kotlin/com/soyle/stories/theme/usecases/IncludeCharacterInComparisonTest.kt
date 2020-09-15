package com.soyle.stories.theme.usecases

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.character.CharacterDoesNotExist
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.entities.*
import com.soyle.stories.theme.CharacterAlreadyIncludedInTheme
import com.soyle.stories.theme.ThemeDoesNotExist
import com.soyle.stories.theme.makeTheme
import com.soyle.stories.theme.setupContext
import com.soyle.stories.theme.usecases.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparison
import com.soyle.stories.theme.usecases.includeCharacterInComparison.IncludeCharacterInComparisonUseCase
import com.soyle.stories.translators.asMinorCharacter
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Created by Brendan
 * Date: 2/27/2020
 * Time: 2:24 PM
 */
class IncludeCharacterInComparisonTest {

    private fun given(
        characters: List<Character>,
        themes: List<Theme>,
        updateTheme: (Theme) -> Unit = {},
        updateCharacterArc: (CharacterArc) -> Unit = {}
    ): (UUID, UUID) -> Either<*, CharacterIncludedInTheme> {
        val context = setupContext(
            initialCharacters = characters,
            initialThemes = themes,
            updateTheme = updateTheme,
            updateCharacterArc = updateCharacterArc
        )
        val useCase: IncludeCharacterInComparison = IncludeCharacterInComparisonUseCase(
            context.characterRepository,
            context.themeRepository,
            context.characterArcRepository
        )
        val output = object : IncludeCharacterInComparison.OutputPort {
            var result: Either<Exception, CharacterIncludedInTheme>? = null
            override fun receiveIncludeCharacterInComparisonFailure(failure: Exception) {
                result = failure.left()
            }

            override suspend fun receiveIncludeCharacterInComparisonResponse(response: CharacterIncludedInTheme) {
                result = response.right()
            }
        }
        return { characterId, themeId ->
            runBlocking {
                useCase.invoke(characterId, themeId, output)
            }
            output.result!!
        }
    }

    val characterUUID = UUID.randomUUID()
    val projectUUID = UUID.randomUUID()
    val themeUUID = UUID.randomUUID()
    val characterName = "Bob the Builder"
    val character = makeCharacter(
        Character.Id(characterUUID), Project.Id(), characterName
    )

    @Nested
    inner class GivenCharacterDoesNotExist {

        val useCase = given(emptyList(), emptyList())

        @Test
        fun `should output error`() {
            val (result) = useCase(characterUUID, themeUUID) as Either.Left
            assert(result is CharacterDoesNotExist)
        }

    }

    @Nested
    inner class GivenThemeDoesNotExist {

        val useCase = given(
            characters = listOf(character),
            themes = emptyList()
        )

        @Test
        fun `should output error`() {
            val (result) = useCase(characterUUID, themeUUID) as Either.Left
            assert(result is ThemeDoesNotExist)
        }

    }

    @Nested
    inner class GivenCharacterAlreadyInTheme {

        val useCase = given(
            characters = listOf(character),
            themes = listOf(
                makeTheme(Theme.Id(themeUUID), includedCharacters = mapOf(
                    Character.Id(characterUUID) to character.asMinorCharacter()
                ))
            )
        )

        @Test
        fun `should output error`() {
            val (result) = useCase(characterUUID, themeUUID) as Either.Left
            assert(result is CharacterAlreadyIncludedInTheme)
        }

    }

    @Nested
    inner class OnSuccess {

        val result = (given(
            characters = listOf(character),
            themes = listOf(
                makeTheme(Theme.Id(themeUUID))
            )
        ).invoke(characterUUID, themeUUID) as Either.Right).b

        @Test
        fun `should contain theme id`() {
            assertEquals(themeUUID, result.themeId)
        }

        @Test
        fun `should contain character id`() {
            assertEquals(characterUUID, result.characterId)
            assertEquals(characterName, result.characterName)
        }

        @Test
        fun `should updated theme`() {
            var updatedTheme: Theme? = null
            given(
                characters = listOf(character),
                themes = listOf(
                    makeTheme(Theme.Id(themeUUID))
                ),
                updateTheme = {
                    updatedTheme = it
                }
            ).invoke(characterUUID, themeUUID)
            updatedTheme!!

        }

    }
}