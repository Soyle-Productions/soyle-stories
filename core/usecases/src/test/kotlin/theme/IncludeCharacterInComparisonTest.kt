package com.soyle.stories.usecase.theme

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.CharacterArc
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.theme.CharacterAlreadyIncludedInTheme
import com.soyle.stories.domain.theme.Theme
import com.soyle.stories.domain.theme.makeTheme
import com.soyle.stories.usecase.character.CharacterDoesNotExist
import com.soyle.stories.usecase.repositories.CharacterRepositoryDouble
import com.soyle.stories.usecase.repositories.ThemeRepositoryDouble
import com.soyle.stories.usecase.theme.includeCharacterInComparison.CharacterIncludedInTheme
import com.soyle.stories.usecase.theme.includeCharacterInComparison.IncludeCharacterInComparison
import com.soyle.stories.usecase.theme.includeCharacterInComparison.IncludeCharacterInComparisonUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class IncludeCharacterInComparisonTest {

    private fun given(
        characters: List<Character>,
        themes: List<Theme>,
        updateTheme: (Theme) -> Unit = {},
        updateCharacterArc: (CharacterArc) -> Unit = {}
    ): (UUID, UUID) -> Either<*, CharacterIncludedInTheme> {
        val themeRepository = ThemeRepositoryDouble(onUpdateTheme = updateTheme)
        val characterRepository = CharacterRepositoryDouble()
        themes.forEach { themeRepository.givenTheme(it) }
        characters.forEach { characterRepository.givenCharacter(it) }
        val useCase: IncludeCharacterInComparison = IncludeCharacterInComparisonUseCase(
            characterRepository,
            themeRepository
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
    val character = makeCharacter(
        Character.Id(characterUUID), Project.Id()
    )
    val characterName = character.name

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
                makeTheme(Theme.Id(themeUUID))
                    .withCharacterIncluded(makeCharacter(Character.Id(characterUUID)))
                    .withCharacterPromoted(Character.Id(characterUUID))
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
            assertEquals(characterName.value, result.characterName)
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