package com.soyle.stories.domain.theme

import arrow.core.Either
import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.theme.characterInTheme.MinorCharacter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CharacterInThemeTest {

    private val character = makeCharacter()
    private val themeWithoutCharacter = makeTheme()
    private val themeWithCharacter = makeTheme().withCharacterIncluded(character)

    @Test
    fun `characters in theme have an archetype`() {
        themeWithCharacter
            .getIncludedCharacterByIdOrError(character.id).archetype
    }

    @Nested
    inner class `Character Archetype` {
        val newArchetype = "Artist"

        @Test
        fun `can change archetype`() {
            val characterInTheme = themeWithCharacter.getIncludedCharacterById(character.id)!!
            val (theme) = themeWithCharacter
                .changeArchetype(characterInTheme, newArchetype) as Either.Right
            val archetype = theme.getIncludedCharacterById(character.id)!!.archetype
            assertEquals(newArchetype, archetype)
        }

        @Test
        fun `cannot change archetype of character not in theme`() {
            val characterInTheme = themeWithCharacter.getIncludedCharacterById(character.id)!!
            val (error) = themeWithoutCharacter
                .changeArchetype(characterInTheme, newArchetype) as Either.Left
            assert(error is CharacterNotInTheme)
        }

    }

    @Nested
    inner class `characters in theme have a variation on the central moral` {
        init {
            themeWithCharacter.getIncludedCharacterById(character.id)!!.variationOnMoral
        }

        private val newVariationOnMoral = "When you look at it this way..."

        @Test
        fun `can change variation on the central moral`() {
            val characterInTheme = themeWithCharacter.getIncludedCharacterById(character.id)!!
            val (theme) = themeWithCharacter
                .changeVariationOnMoral(characterInTheme, newVariationOnMoral) as Either.Right
            val variationOnMoral = theme.getIncludedCharacterById(character.id)!!.variationOnMoral
            assertEquals(newVariationOnMoral, variationOnMoral)
        }

        @Test
        fun `cannot change variation on the central moral of character not in theme`() {
            val characterInTheme = themeWithCharacter.getIncludedCharacterById(character.id)!!
            val (error) = themeWithoutCharacter
                .changeVariationOnMoral(characterInTheme, newVariationOnMoral) as Either.Left
            assert(error is CharacterNotInTheme)
        }
    }

    @Nested
    inner class `Promote Character` {

        @Test
        fun `can promote minor characters`() {
            val theme = themeWithCharacter
                .withCharacterPromoted(character.id)
            assert(theme.getMajorCharacterById(character.id) != null)
        }

        @Test
        fun `cannot promote characters not in theme`() {
            assertThrows<CharacterNotInTheme> {
                themeWithoutCharacter.withCharacterPromoted(Character.Id())
            }
        }
    }

    @Nested
    inner class `Demote Character` {

        private val themeWithMajorCharacter = themeWithCharacter
            .withCharacterPromoted(character.id)

        @Test
        fun `can demote major characters`() {
            val (theme) = themeWithMajorCharacter.demoteCharacter(
                themeWithMajorCharacter.getMajorCharacterByIdOrError(
                    character.id
                )
            ) as Either.Right
            assert(theme.getMinorCharacterById(character.id) is MinorCharacter)
        }

        @Test
        fun `cannot demote characters not in theme`() {
            val majorCharacterInTheme = themeWithMajorCharacter.getMajorCharacterByIdOrError(character.id)
            val (error) = themeWithoutCharacter
                .demoteCharacter(majorCharacterInTheme) as Either.Left
            assert(error is CharacterNotInTheme)
        }

    }
}