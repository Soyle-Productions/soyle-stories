package com.soyle.stories.theme

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.soyle.stories.character.characterName
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.entities.*
import com.soyle.stories.entities.theme.characterInTheme.CharacterPerspective
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.entities.theme.characterInTheme.StoryFunction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 4:38 PM
 */
class CharacterPerspectiveTest {

    val otherCharacters = List(5) { Character.Id(UUID.randomUUID()) }
        .map { makeCharacter(it, Project.Id(), characterName()) }

    tailrec fun Theme.includeCharacters(characters: List<Character>): Either<ThemeException, Theme> {
        if (characters.isEmpty()) return this.right()
        val character = characters.first()
        val result = withCharacterIncluded(character.id, character.name.value, character.media).right()
        if (result !is Either.Right) return result
        return result.b.includeCharacters(characters.drop(1))
    }

    @Test
    fun `can apply story functions to characters`() {
        val (theme) = promoteCharacter()
            .flatMap { it.includeCharacters(otherCharacters) }
                as Either.Right

        val characterInTheme = theme.getMajorCharacterById(newCharacter.id)
        characterInTheme as MajorCharacter

        val updatedCharacter = theme
            .withCharacterAsStoryFunctionForMajorCharacter(
                otherCharacters.first().id,
                StoryFunction.Antagonist,
                characterInTheme.id
            ).getMajorCharacterById(newCharacter.id)
        updatedCharacter as MajorCharacter

        assert(
            updatedCharacter.hasStoryFunctionForTargetCharacter(
                StoryFunction.Antagonist,
                otherCharacters.first().id
            )
        )
    }

    @Test
    fun `cannot apply story functions to characters not in theme`() {
        val (theme) = promoteCharacter()
            .flatMap { it.includeCharacters(otherCharacters) }
                as Either.Right

        listOf(
            theme.getMajorCharacterById(newCharacter.id) as MajorCharacter to makeCharacter(
                Character.Id(UUID.randomUUID()),
                Project.Id(),
                characterName()
            ),
            Character.Id(UUID.randomUUID()).let { id ->
                MajorCharacter(
                    id,
                    "Name",
                    "",
                    "",
                    "",
                    CharacterPerspective(
                        mapOf(),
                        mapOf()
                    ),
                    ""
                )
            } to otherCharacters.first()
        ).forEach { (characterInTheme, targetCharacter) ->
            assertThrows<CharacterNotInTheme> {
                theme.withCharacterAsStoryFunctionForMajorCharacter(
                    targetCharacter.id,
                    StoryFunction.Antagonist,
                    characterInTheme.id
                )
            }
        }
    }

    @Test
    fun `cannot apply same story function more than once`() {
        val (theme) = promoteCharacter()
            .flatMap { it.includeCharacters(otherCharacters) }
                as Either.Right

        assertThrows<StoryFunctionAlreadyApplied> {
            theme.withCharacterAsStoryFunctionForMajorCharacter(
                otherCharacters.first().id,
                StoryFunction.Antagonist,
                newCharacter.id
            ).withCharacterAsStoryFunctionForMajorCharacter(
                otherCharacters.first().id,
                StoryFunction.Antagonist,
                newCharacter.id
            )
        }
    }

    @Test
    fun `antagonistic characters attack the perspective character`() {
        val (characterInTheme) = promoteCharacter()
            .flatMap { it.includeCharacters(otherCharacters) }
            .map {
                val characterInTheme = it.getMajorCharacterById(newCharacter.id) as MajorCharacter
                it.withCharacterAsStoryFunctionForMajorCharacter(
                    otherCharacters.first().id,
                    StoryFunction.Antagonist,
                    characterInTheme.id
                )
            }.map { it.getMajorCharacterById(newCharacter.id) }
                as Either.Right
        characterInTheme as MajorCharacter

        characterInTheme.getAttacksByCharacter(otherCharacters.first().id)!!
    }

    @Test
    fun `can update attacks of antagonistic characters`() {
        val newAttack = "I attack"

        val (theme) = promoteCharacter()
            .flatMap { it.includeCharacters(otherCharacters) }
                as Either.Right

        val characterInTheme = theme.getMajorCharacterById(newCharacter.id)
        characterInTheme as MajorCharacter

        val updatedCharacter = theme
            .withCharacterAsStoryFunctionForMajorCharacter(
                otherCharacters.first().id,
                StoryFunction.Antagonist,
                characterInTheme.id
            )
            .let {
                val updatedCharacter = it.getMajorCharacterById(newCharacter.id) as MajorCharacter
                it.withCharacterAttackingMajorCharacter(otherCharacters.first().id, newAttack, updatedCharacter.id)
                    .getMajorCharacterById(newCharacter.id)
            }
        updatedCharacter as MajorCharacter

        assertEquals(newAttack, updatedCharacter.getAttacksByCharacter(otherCharacters.first().id))

    }

    @Test
    fun `cannot  update attacks of characters not in theme`() {
        val (theme) = promoteCharacter()
            .flatMap { it.includeCharacters(otherCharacters) }
                as Either.Right

        listOf(
            theme.getMajorCharacterById(newCharacter.id) as MajorCharacter to makeCharacter(
                Character.Id(UUID.randomUUID()),
                Project.Id(),
                characterName()
            ),
            Character.Id(UUID.randomUUID()).let { id ->
                MajorCharacter(
                    id,
                    "Name",
                    "",
                    "",
                    "",
                    CharacterPerspective(
                        mapOf(),
                        mapOf()
                    ),
                    ""
                )
            } to otherCharacters.first()
        ).forEach { (characterInTheme, targetCharacter) ->
            assertThrows<CharacterNotInTheme> {
                theme.withCharacterAttackingMajorCharacter(targetCharacter.id, "", characterInTheme.id)
            }
        }
    }
}