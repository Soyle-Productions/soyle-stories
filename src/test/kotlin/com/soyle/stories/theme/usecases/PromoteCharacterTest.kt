/**
 * Created by Brendan
 * Date: 3/4/2020
 * Time: 4:51 PM
 */
package com.soyle.stories.theme.usecases

import arrow.core.Either
import arrow.core.flatMap
import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.entities.theme.MinorCharacter
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacter
import com.soyle.stories.theme.usecases.promoteMinorCharacter.PromoteMinorCharacterUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class PromoteCharacterTest {

    /*

    provided data:
    - theme id
    - character id
    - character arc name (optional)

    happy path:
    - get theme by id
    - get minor character in theme with character id
    - convert minor character to major character
    - create character arc with provided name or same name as first major character's arc name
    - save theme
    - save character arc

    output data:
    - theme id
    - character id
    - arc name

    alternative paths:
    - theme does not exist: output failure
    - character not in theme: output failure
    - character already promoted: output failure
    - character arc already exists for theme id and character id: output failure

     */

    private fun given(
        themes: List<ThemeRepresentation>,
        characterArcs: List<ArcRepresentation> = emptyList()
    ): PreparedUseCase {
        return PreparedUseCase(themes, characterArcs)
    }

    private fun given(theme: ThemeRepresentation) = given(listOf(theme))

    @Test
    fun `non-existent theme`() {
        given(noThemes)
            .whenExecutedWith(AnyThemeId, AnyCharacterId, NoProvidedName)
            .shouldFail<ThemeDoesNotExist>(NeverPersisted)
    }

    @Test
    fun `character not in theme`() {
        given(themeWith(noCharacters))
            .whenExecutedWith(FirstThemeId, AnyCharacterId, NoProvidedName)
            .shouldFail<CharacterNotInTheme>(NeverPersisted)
    }

    @Test
    fun `character already promoted`() {
        given((5).themesWith { (4).majorCharacters() })
            .whenExecutedWith(FirstThemeId, FirstCharacterInFirstThemeId, NoProvidedName)
            .shouldFail<CharacterIsAlreadyMajorCharacterInTheme>(NeverPersisted)
    }

    @Nested
    inner class `character in theme not promoted and without existing arc` {

        val themes = (3).themesWith { (3).minorCharacters() and (1).majorCharacters() }
        val nameOfArcs = "Arc name"
        val given = given(
            themes,
            themes.map { characterArc(it.uuid, it.includedCharacters.last().first, nameOfArcs) }
        )

        @Test
        fun `executed without name`() {
            given.whenExecutedWith(FirstThemeId, FirstCharacterInFirstThemeId, NoProvidedName)
                .shouldPass(
                    PersistAnyTheme,
                    expectedPersistedArc = {
                        it as CharacterArc
                        assertEquals(nameOfArcs, it.name)
                    },
                    expectedPersistedArcSections = {
                        assert(it.isNotEmpty())
                        assert(it.all { it is CharacterArcSection })
                        @Suppress("UNCHECKED_CAST")
                        it as List<CharacterArcSection>

                    }
                )
        }

        @Test
        fun `executed with name`() {
            val providedName = "Provided Arc Name"
            given.whenExecutedWith(FirstThemeId, FirstCharacterInFirstThemeId, providedName)
                .shouldPass(
                    PersistAnyTheme,
                    expectedPersistedArc = {
                        it as CharacterArc
                        assertEquals(providedName, it.name)
                    },
                    expectedPersistedArcSections = {

                    }
                )
        }

    }

    private fun Int.themesWith(collectCharacters: () -> List<Pair<UUID, Boolean>>): List<ThemeRepresentation> {
        return List(this) { themeWith(collectCharacters()) }
    }

    private fun Int.minorCharacters(): List<Pair<UUID, Boolean>> = List(this) { UUID.randomUUID() to false }
    private fun Int.majorCharacters(): List<Pair<UUID, Boolean>> = List(this) { UUID.randomUUID() to true }
    private infix fun List<Pair<UUID, Boolean>>.and(elements: List<Pair<UUID, Boolean>>): List<Pair<UUID, Boolean>> =
        this.plus(elements)

    private fun characterArc(themeId: UUID, characterId: UUID, name: String = ""): ArcRepresentation =
        ArcRepresentation(themeId, characterId, name)

    val noThemes
        get() = emptyList<ThemeRepresentation>()

    val noCharacters
        get() = emptyList<Pair<UUID, Boolean>>()

    val NeverPersisted
        get() = null

    val PersistAnyTheme: (Any?) -> Unit
        get() = {}

    val AnyThemeId: (List<ThemeRepresentation>) -> UUID
        get() = { UUID.randomUUID() }
    val FirstThemeId: (List<ThemeRepresentation>) -> UUID
        get() = { it.first().uuid }
    val AnyCharacterId: (List<ThemeRepresentation>) -> UUID
        get() = { UUID.randomUUID() }
    val FirstCharacterInFirstThemeId: (List<ThemeRepresentation>) -> UUID
        get() = { it.first().includedCharacters.first().first }
    val NoProvidedName: String?
        get() = null

    private fun themeWith(includedCharacters: List<Pair<UUID, Boolean>>) = ThemeRepresentation(includedCharacters)

    class ThemeRepresentation(val includedCharacters: List<Pair<UUID, Boolean>>) {
        val uuid = UUID.randomUUID()
    }

    class ArcRepresentation(val themeId: UUID, val characterId: UUID, val name: String)

    inner class PreparedUseCase(private val themeReps: List<ThemeRepresentation>, arcReps: List<ArcRepresentation>) {

        val themes = themeReps.map { rep ->
            val charactersToInclude = rep.includedCharacters.map {
                (Character(
                    Character.Id(
                        it.first
                    ), Project.Id(), "Bob"
                ) to it.second)
            }
            val theme = makeTheme(Theme.Id(rep.uuid))
            if (charactersToInclude.isNotEmpty()) {
                charactersToInclude.fold(theme) { currentTheme, (character, promoted) ->
                    (currentTheme.includeCharacter(character).let {
                        if (!promoted) it
                        else it.flatMap { it.promoteCharacter(it.getMinorCharacterById(character.id) as MinorCharacter) }
                    } as Either.Right).b
                }
            } else theme
        }
        val characterArcs = arcReps.map {
            CharacterArc(
                Character.Id(it.characterId),
                CharacterArcTemplate.default(),
                Theme.Id(it.themeId),
                it.name
            )
        }

        fun whenExecutedWith(
            themeId: (List<ThemeRepresentation>) -> UUID,
            characterId: (List<ThemeRepresentation>) -> UUID,
            name: String?
        ): PromoteMinorCharacterAssertions {
            val request = PromoteMinorCharacter.RequestModel(
                themeId = themeId(themeReps),
                characterId = characterId(themeReps),
                characterArcName = name
            )
            var result: Any? = null
            var updatedTheme: Any? = null
            var createdCharacterArc: Any? = null
            var createdArcSections: List<Any?> = listOf()
            val output = object : PromoteMinorCharacter.OutputPort {
                override fun receivePromoteMinorCharacterFailure(failure: ThemeException) {
                    result = failure
                }

                override fun receivePromoteMinorCharacterResponse(response: PromoteMinorCharacter.ResponseModel) {
                    result = response
                }
            }
            val context = setupContext(
                initialThemes = themes,
                initialCharacterArcs = characterArcs,
                addNewCharacterArc = {
                    createdCharacterArc = it
                },
                updateTheme = {
                    updatedTheme = it
                },
                addNewCharacterArcSections = {
                    createdArcSections = it
                }
            )
            val useCase: PromoteMinorCharacter = PromoteMinorCharacterUseCase(
                context.themeRepository,
                context.characterArcRepository,
                context.characterArcSectionRepository
            )
            runBlocking { useCase.invoke(request, output) }
            return PromoteMinorCharacterAssertions(
                result,
                request.themeId,
                request.characterId,
                updatedTheme,
                createdCharacterArc,
                createdArcSections
            )
        }

    }

    inner class PromoteMinorCharacterAssertions(
        private val result: Any?,
        private val themeId: UUID,
        private val characterId: UUID,
        private val updatedTheme: Any?,
        private val createdCharacterArc: Any?,
        private val createdArcSections: List<Any?>
    ) {

        inline fun <reified T : ThemeException> shouldFail(expectedPersistence: Any?) =
            shouldFail(T::class.java, expectedPersistence)

        fun <T : ThemeException> shouldFail(tClass: Class<T>, expectedPersistence: Any?) {
            val result = result as ThemeException
            assert(tClass.isInstance(result)) { "Result is not of expected type ${tClass.name}: <${result::class.java.name}>" }
            assertEquals(themeId, result.themeId)
            assertEquals(expectedPersistence, updatedTheme)
        }

        fun shouldPass(expectedPersistedTheme: (Any?) -> Unit, expectedPersistedArc: (Any?) -> Unit, expectedPersistedArcSections: (List<Any?>) -> Unit) {
            val result = result as PromoteMinorCharacter.ResponseModel
            assertEquals(themeId, result.themeId)
            assertEquals(characterId, result.characterId)
            assertEquals((createdCharacterArc as? CharacterArc)?.name, result.characterArcName)
            val updatedTheme = updatedTheme as Theme
            assertEquals(themeId, updatedTheme.id.uuid)
            val updatedCharacter = updatedTheme.getMajorCharacterById(Character.Id(characterId)) as MajorCharacter
            expectedPersistedTheme(updatedTheme)
            expectedPersistedArc(createdCharacterArc)
            expectedPersistedArcSections(createdArcSections)
        }

    }
}