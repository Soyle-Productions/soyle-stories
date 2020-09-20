/**
 * Created by Brendan
 * Date: 3/5/2020
 * Time: 2:04 PM
 */
package com.soyle.stories.theme.usecases

import com.soyle.stories.character.makeCharacter
import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.characterInTheme.MinorCharacter
import com.soyle.stories.theme.*
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacter
import com.soyle.stories.theme.usecases.demoteMajorCharacter.DemoteMajorCharacterUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

class DemoteCharacterTest {

    /*

    provided data:
    - themeId
    - characterId

    Happy path:
    - get theme by id
    - get major character from theme with characterId
    - demote major character within theme
    - update theme

    output:
    - themeId
    - characterId

    alternative paths:
    - theme with id does not exist: failure
    - character not in theme: failure
    - character not a major character: failure

     */

    private fun theme() = UUID.randomUUID()
    private fun character() = UUID.randomUUID()
    private fun minorCharacter(givenId: UUID = UUID.randomUUID()) = givenId to false
    private fun majorCharacter(givenId: UUID = UUID.randomUUID()) = givenId to true

    val themeId: UUID = theme()
    val characterId: UUID = character()

    private fun given(
        themeIds: List<UUID>,
        includedCharacters: Map<UUID, List<Pair<UUID, Boolean>>> = emptyMap()
    ): PreppedUseCase {
        return PreppedUseCase(themeIds, includedCharacters)
    }

    @Test
    fun `no theme exists _ output failure`() {
        listOf(
            given(emptyList()),
            given(List(5) { theme() })
        ).forEach {
            it.whenExecuted().assert {
                output as ThemeDoesNotExist
                assertEquals(themeId, output.themeId)

                assertNeverPersisted()
            }
        }
    }

    @Test
    fun `character not in theme _ output failure`() {
        listOf(
            given(List(1) { themeId }, includedCharacters = mapOf(themeId to emptyList())),
            given(List(1) { themeId }, includedCharacters = mapOf(themeId to List(5) { minorCharacter() }))
        ).forEach {
            it.whenExecuted().assert {
                output as CharacterNotInTheme
                assertEquals(themeId, output.themeId)
                assertEquals(characterId, output.characterId)

                assertNeverPersisted()
            }
        }
    }

    @Test
    fun `character in theme as minor character _ output failure`() {
        listOf(
            given(List(1) { themeId }, includedCharacters = mapOf(themeId to List(1) { minorCharacter(characterId) })),
            given(
                List(1) { themeId },
                includedCharacters = mapOf(themeId to List(4) { minorCharacter() } + minorCharacter(characterId))),
            given(
                List(1) { themeId },
                includedCharacters = mapOf(themeId to List(4) { majorCharacter() } + minorCharacter(characterId)))
        ).forEach {
            it.whenExecuted().assert {
                output as CharacterIsNotMajorCharacterInTheme
                assertEquals(themeId, output.themeId)
                assertEquals(characterId, output.characterId)

                assertNeverPersisted()
            }
        }
    }

    @Test
    fun `character in theme as major character`() {
        listOf(
            given(
                List(1) { themeId },
                includedCharacters = mapOf(themeId to List(4) { majorCharacter() } + majorCharacter(characterId)))
        ).forEach {
            it.whenExecuted().assert {
                output as DemoteMajorCharacter.ResponseModel
                assertEquals(themeId, output.themeId)
                assertEquals(characterId, output.characterId)
                assertOutputContainsAllSections()

                assertNoNewThemesCreated()
                val updatedTheme = updatedThemes.single() as Theme
                updatedTheme.getMinorCharacterById(Character.Id(characterId)) as MinorCharacter

                assertCharacterArcDeleted()
            }
        }
    }

    @Test
    fun `only major character remaining in theme`() {
        listOf(
            given(List(1) { themeId }, includedCharacters = mapOf(themeId to List(1) { majorCharacter(characterId) })),
            given(
                List(1) { themeId },
                includedCharacters = mapOf(themeId to List(4) { minorCharacter() } + majorCharacter(characterId)))
        ).forEach {
            it.whenExecuted().assert {
                output as DemoteMajorCharacter.ResponseModel
                assertTrue(output.themeRemoved)
            }
        }
    }

    inner class PreppedUseCase(themeIds: List<UUID>, includedCharacters: Map<UUID, List<Pair<UUID, Boolean>>>) {

        private val initialThemes = themeIds.map { uuid ->
            val charactersToInclude = includedCharacters[uuid]?.map {
                makeCharacter(
                    Character.Id(it.first),
                    Project.Id(),
                    "Bob"
                ) to it.second
            }
            val theme = makeTheme(Theme.Id(uuid))
            if (charactersToInclude.isNullOrEmpty()) return@map theme
            charactersToInclude.fold(theme) { currentTheme, (character, promote) ->
                val themeWithCharacter =
                    currentTheme.withCharacterIncluded(character.id, character.name, character.media)
                if (promote) themeWithCharacter.withCharacterPromoted(character.id)
                else themeWithCharacter

            }
        }

        fun whenExecuted(): DemoteCharacterAssertions {
            val output = object : DemoteMajorCharacter.OutputPort {
                var result: Any? = null
                override fun receiveDemoteMajorCharacterFailure(failure: Exception) {
                    result = failure
                }

                override fun receiveDemoteMajorCharacterResponse(response: DemoteMajorCharacter.ResponseModel) {
                    result = response
                }
            }
            val initialCharacterArcs = initialThemes.flatMap { theme ->
                theme.characters.map {
                    CharacterArc.planNewCharacterArc(
                        it.id,
                        theme.id,
                        theme.name
                    )
                }
            }
            var removedCharacterArc: Any? = null
            val removedSections = mutableListOf<CharacterArcSection>()
            val updatedThemes = mutableListOf<Theme>()
            val deletedThemes = mutableListOf<Theme>()

            val context = setupContext(
                initialThemes = initialThemes,
                initialCharacterArcs = initialCharacterArcs,
                removeCharacterArc = { arc ->
                    removedCharacterArc = arc.themeId.uuid to arc.characterId.uuid
                },
                updateCharacterArc = { arc ->
                    val original = initialCharacterArcs.find { it.id == arc.id }!!
                    val removedIds = original.arcSections.map { it.id }.toSet() - (arc.arcSections.map { it.id }.toSet())
                    removedSections.addAll(original.arcSections.filter { it.id in removedIds })
                },
                updateTheme = {
                    updatedThemes.add(it)
                },
                deleteTheme = {
                    deletedThemes.add(it)
                }
            )

            runBlocking {
                DemoteMajorCharacterUseCase(context).invoke(themeId, characterId, output)
            }
            return DemoteCharacterAssertions(
                themeId,
                characterId,
                output.result,
                initialCharacterArcs.find { it.themeId.uuid == themeId },
                emptyList(),
                updatedThemes,
                deletedThemes,
                removedSections,
                removedCharacterArc
            )
        }
    }

    class DemoteCharacterAssertions(
        val themeId: UUID,
        val characterId: UUID,
        val output: Any?,
        val initialCharacterArc: CharacterArc?,
        val addedThemes: List<Any?>,
        val updatedThemes: List<Any?>,
        val removedThemes: List<Any?>,
        val removedSections: List<Any?>,
        val removedCharacterArc: Any?
    ) {

        val sectionIdsThatShouldBeRemoved = initialCharacterArc
            ?.arcSections
            ?.map { it.id }
            ?.toSet()

        fun assert(block: DemoteCharacterAssertions.() -> Unit) {
            block()
        }

        fun assertNeverPersisted() {
            assertNoNewThemesCreated()
            assert(updatedThemes.isEmpty()) { "Theme was updated $updatedThemes" }
        }

        fun assertNoNewThemesCreated() {
            assert(addedThemes.isEmpty()) { "Theme was added $addedThemes" }
        }

        fun assertOutputContainsAllSections() {
            output as DemoteMajorCharacter.ResponseModel
            assertEquals(
                sectionIdsThatShouldBeRemoved?.size ?: 0,
                output.removedCharacterArcSections.size
            )
        }

        fun assertNonThematicSectionsDeleted() {
            assert(removedSections.isNotEmpty())
            assert(removedSections.all { it is CharacterArcSection })
            @Suppress("UNCHECKED_CAST")
            removedSections as List<CharacterArcSection>
            assertEquals(sectionIdsThatShouldBeRemoved, removedSections.map { it.template.id }.toSet())
        }

        fun assertCharacterArcDeleted() {
            removedCharacterArc as Pair<*, *>
            assertEquals(themeId, removedCharacterArc.first)
            assertEquals(characterId, removedCharacterArc.second)
        }

        fun assertThemeDeleted() {
            assertFalse(removedThemes.isEmpty()) { "Removed themes cannot be empty" }
        }
    }

}