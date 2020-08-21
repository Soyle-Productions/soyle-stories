/**
 * Created by Brendan
 * Date: 3/14/2020
 * Time: 10:00 AM
 */
package com.soyle.stories.theme.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.entities.theme.characterInTheme.StoryFunction
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.changeStoryFunction.ChangeStoryFunction
import com.soyle.stories.theme.usecases.changeStoryFunction.ChangeStoryFunctionUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
class ChangeStoryFunctionTest {

    /*

    provided data:
    - themeId
    - perspectiveCharacterId
    - targetCharacterId
    - story function

    Happy Path:
    - get theme by id
    - get major character in theme with perspective character id
    - apply story function to target character for perspective character in theme
    - update theme

    Output data:
    - themeId
    - perspectiveCharacterId
    - targetCharacterId
    - story function

    Alt Paths:
    - theme does not exist: output failure
    - perspective character not in theme: output failure
    - perspective character not major character in theme: output failure
    - target character not in theme: output failure
    - target character already has story function for perspective character in theme: output success, no update

     */

    private fun theme(id: UUID? = null, characters: () -> List<Pair<UUID, String?>> = { NoCharacters }) =
        (id ?: UUID.randomUUID()) to characters()

    private fun character(id: UUID? = null) = minorCharacter(id)
    private fun minorCharacter(id: UUID? = null) = (id ?: UUID.randomUUID()) to (null as String?)
    private fun majorCharacter(id: UUID? = null, storyFunction: () -> String = { "" }) = (id ?: UUID.randomUUID()) to storyFunction()

    private val themeId: UUID = theme().first
    private val perspectiveCharacterId: UUID = character().first
    private val targetCharacterId: UUID = character().first
    private val storyFunction: String = ChangeStoryFunction.StoryFunction.Antagonist.name

    private lateinit var assertions: ChangeStoryFunctionAssertions

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_METHOD)
    inner class `Theme does not exist` {

        @Test
        fun `no themes`() {
            assertions = given(NoThemes).whenExecuted()
        }

        @Test
        fun `some themes`() {
            assertions = given(List(5) { theme() }).whenExecuted()
        }

        @AfterEach
        fun `test assertions`() {
            assertions.assertThat {
                output as ThemeDoesNotExist
                output.themeId.mustEqual(themeId) { "Output themeId does not match themeId" }

                wasNotPersisted()
            }
        }

    }

    @Nested
    inner class `Perspective character not in theme` {

        @Test
        fun `no characters in theme`() {
            assertions = given(List(1) { theme(themeId) { NoCharacters } }).whenExecuted()
        }

        @Test
        fun `character not in theme`() {
            assertions = given(List(1) { theme(themeId) { List(5) { character() } } }).whenExecuted()
        }

        @AfterEach
        fun `check assertions`() {
            assertions.assertThat {
                output as CharacterNotInTheme
                output.themeId.mustEqual(themeId) { "Output themeId does not match themeId" }
                output.characterId.mustEqual(perspectiveCharacterId) { "Output characterId does not match perspectiveCharacterId" }

                wasNotPersisted()
            }
        }

    }

    @Test
    fun `target character not in theme`() {
        given(List(1) { theme(themeId) { List(1) { character(perspectiveCharacterId) } } })
            .whenExecuted()
            .assertThat {
                output as CharacterNotInTheme
                output.themeId.mustEqual(themeId) { "Output themeId does not match themeId" }
                output.characterId.mustEqual(targetCharacterId) { "Output characterId does not match targetCharacterId" }

                wasNotPersisted()
            }
    }

    @Test
    fun `perspective character not a major character`() {
        given(List(1) {
            theme(themeId) {
                listOf(
                    minorCharacter(perspectiveCharacterId),
                    minorCharacter(targetCharacterId)
                )
            }
        })
            .whenExecuted()
            .assertThat {
                output as CharacterIsNotMajorCharacterInTheme
                output.themeId.mustEqual(themeId) { "Output themeId does not match themeId" }
                output.characterId.mustEqual(perspectiveCharacterId) { "Output characterId does not match perspectiveCharacterId" }

                wasNotPersisted()
            }
    }

    @Test
    fun `target character already has story function`() {
        given(List(1) {
            theme(themeId) {
                listOf(
                    majorCharacter(perspectiveCharacterId) { storyFunction },
                    minorCharacter(targetCharacterId)
                )
            }
        })
            .whenExecuted()
            .assertThat {
                output as ChangeStoryFunction.ResponseModel
                output.themeId.mustEqual(themeId) { "Output themeId does not match themeId" }
                output.perspectiveCharacterId.mustEqual(perspectiveCharacterId) { "Output perspectiveCharacterId does not match perspectiveCharacterId" }
                output.targetCharacterId.mustEqual(targetCharacterId) { "Output targetCharacterId does not match targetCharacterId" }
                output.storyFunction.mustEqual(storyFunction) { "Output storyFunction does not match storyFunction" }

                // wasNotPersisted() TODO "whole test needs a rewrite"
            }
    }

    @Test
    fun `target character does not already have story function`() {
        val states = listOf(
            given(List(1) {
                theme(themeId) {
                    listOf(
                        majorCharacter(perspectiveCharacterId) { NoStoryFunction },
                        minorCharacter(targetCharacterId)
                    )
                }
            }),
            given(List(1) {
                theme(themeId) {
                    listOf(
                        majorCharacter(perspectiveCharacterId) { "Ally" },
                        minorCharacter(targetCharacterId)
                    )
                }
            })
        )
        states.assertThatEach {
            output as ChangeStoryFunction.ResponseModel
            output.themeId.mustEqual(themeId) { "Output themeId does not match themeId" }
            output.perspectiveCharacterId.mustEqual(perspectiveCharacterId) { "Output perspectiveCharacterId does not match perspectiveCharacterId" }
            output.targetCharacterId.mustEqual(targetCharacterId) { "Output targetCharacterId does not match targetCharacterId" }
            output.storyFunction.mustEqual(storyFunction) { "Output storyFunction does not match storyFunction" }

            val persistedThemes = persistedData.filter { it.type == "updateTheme" }
            persistedThemes.size.mustEqual(1) { "Should have updated exactly one theme" }
            val persistedTheme = persistedThemes.single().data as Theme
            val persistedPerspectiveCharacter =
                persistedTheme.getMajorCharacterById(Character.Id(perspectiveCharacterId)) as MajorCharacter
            val persistedTargetCharacterStoryFunctions =
                persistedPerspectiveCharacter.getStoryFunctionsForCharacter(
                    Character.Id(targetCharacterId)
                )!!
            persistedTargetCharacterStoryFunctions
                .mustEqual(StoryFunction.valueOf(storyFunction)) { "Story function was not applied." }

        }
    }

    inline val NoThemes
        get() = emptyList<Nothing>()
    inline val NoCharacters
        get() = emptyList<Nothing>()
    inline val NoStoryFunction
        get() = ""

    private fun given(themes: List<Pair<UUID, List<Pair<UUID, String?>>>>): PreparedUseCase =
        PreparedUseCase(themes)

    inner class PreparedUseCase(
        themes: List<Pair<UUID, List<Pair<UUID, String?>>>>
    ) {

        private val context = TestContext(
            initialThemes = themes.map { (themeUUID, characters) ->
                val initialTheme =
                    characters.fold(makeTheme(Theme.Id(themeUUID))) { theme, (characterUUID, storyFunction) ->
                        val character = makeCharacter(Character.Id(characterUUID))
                        theme.withCharacterIncluded(character.id, character.name, character.media)
                            .let {
                                if (storyFunction != null) {
                                    it.withCharacterPromoted(character.id)
                                } else it
                            }
                    }
                val majorCharacterIds = characters.filter { it.second != null && it.second != "" }
                majorCharacterIds.fold(initialTheme) { it, (characterUUID, storyFunction) ->
                    majorCharacterIds.filter { it.first != characterUUID }.fold(it) { theme, (otherCharacterId, _) ->
                        storyFunction!!
                        theme.withCharacterAsStoryFunctionForMajorCharacter(
                            Character.Id(otherCharacterId),
                            StoryFunction.valueOf(storyFunction),
                            Character.Id(characterUUID)
                        )
                    }
                }
            }
        )

        fun whenExecuted(): ChangeStoryFunctionAssertions {
            val output = object : ChangeStoryFunction.OutputPort {
                var result: Any? = null
                override fun receiveChangeStoryFunctionFailure(failure: Exception) {
                    result = failure
                }

                override fun receiveChangeStoryFunctionResponse(response: ChangeStoryFunction.ResponseModel) {
                    result = response
                }
            }

            val useCase: ChangeStoryFunction = ChangeStoryFunctionUseCase(context)

            runBlocking {
                val requestModel = ChangeStoryFunction.RequestModel(
                    themeId,
                    perspectiveCharacterId,
                    targetCharacterId,
                    ChangeStoryFunction.StoryFunction.valueOf(storyFunction)
                )
                useCase.invoke(requestModel, output)
            }

            return ChangeStoryFunctionAssertions(output.result, context.persistedItems)
        }

    }

    private fun List<PreparedUseCase>.assertThatEach(assertions: ChangeStoryFunctionAssertions.() -> Unit) {
        forEachIndexed { index, preparedUseCase ->
            preparedUseCase.whenExecuted().assertThat(index, assertions)
        }
    }

    class ChangeStoryFunctionAssertions(
        val output: Any?,
        val persistedData: List<TestContext.PersistenceLog>
    ) {

        fun assertThat(testNumber: Int? = null, assertions: ChangeStoryFunctionAssertions.() -> Unit) {
            try {
                assertions()
            } catch (t: Throwable) {
                if (testNumber != null) throw Error("Test [$testNumber] failed.  ${t.message}", t)
                else throw t
            }
        }

        fun Any?.mustEqual(value: Any?, message: () -> String = { "" }) = assertEquals(value, this) { message() }

        fun wasNotPersisted() {
            assert(persistedData.isEmpty()) { "No items should have been persisted.  $persistedData" }
        }

    }

}