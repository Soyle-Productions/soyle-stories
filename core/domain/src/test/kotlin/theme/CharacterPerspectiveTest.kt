package com.soyle.stories.domain.theme

import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.theme.characterInTheme.MajorCharacter
import com.soyle.stories.domain.theme.characterInTheme.StoryFunction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class CharacterPerspectiveTest {

    private val theme = makeTheme().run {
        val character = makeCharacter()
        withCharacterIncluded(character)
            .withCharacterPromoted(character.id)
    }
    private val majorCharacterInTheme = theme.characters.first() as MajorCharacter

    @Test
    fun `cannot apply story functions to characters not in theme`() {
        assertThrows<CharacterNotInTheme> {
            theme.withCharacterAsStoryFunctionForMajorCharacter(
                makeCharacter().id,
                StoryFunction.Ally,
                majorCharacterInTheme.id
            )
        }
    }

    @Test
    fun `can apply story functions to characters`() {
        val otherCharacter = makeCharacter()
        val updatedCharacter = theme.withCharacterIncluded(otherCharacter)
            .withCharacterAsStoryFunctionForMajorCharacter(
                otherCharacter.id,
                StoryFunction.Antagonist,
                majorCharacterInTheme.id
            )
            .getMajorCharacterByIdOrError(majorCharacterInTheme.id)
        assert(
            updatedCharacter.hasStoryFunctionForTargetCharacter(
                StoryFunction.Antagonist,
                otherCharacter.id
            )
        )
    }

    @Test
    fun `cannot apply same story function more than once`() {
        val otherCharacter = makeCharacter()
        val themeWithStoryFunctionApplied = theme.withCharacterIncluded(otherCharacter)
            .withCharacterAsStoryFunctionForMajorCharacter(
                otherCharacter.id,
                StoryFunction.Antagonist,
                majorCharacterInTheme.id
            )
        assertThrows<StoryFunctionAlreadyApplied> {
            themeWithStoryFunctionApplied.withCharacterAsStoryFunctionForMajorCharacter(
                otherCharacter.id,
                StoryFunction.Antagonist,
                majorCharacterInTheme.id
            )
        }
    }

    @Test
    fun `antagonistic characters attack the perspective character`() {
        val otherCharacter = makeCharacter()
        val updatedCharacter = theme.withCharacterIncluded(otherCharacter)
            .withCharacterAsStoryFunctionForMajorCharacter(
                otherCharacter.id,
                StoryFunction.Antagonist,
                majorCharacterInTheme.id
            )
            .getMajorCharacterByIdOrError(majorCharacterInTheme.id)
        updatedCharacter.getAttacksByCharacter(otherCharacter.id)!!
    }

    @Test
    fun `can update attacks of antagonistic characters`() {
        val newAttack = "I attack"
        val otherCharacter = makeCharacter()
        val updatedCharacter = theme.withCharacterIncluded(otherCharacter)
            .withCharacterAsStoryFunctionForMajorCharacter(
                otherCharacter.id,
                StoryFunction.Antagonist,
                majorCharacterInTheme.id
            )
            .withCharacterAttackingMajorCharacter(
                otherCharacter.id,
                newAttack,
                majorCharacterInTheme.id
            )
            .getMajorCharacterByIdOrError(majorCharacterInTheme.id)
        assertEquals(newAttack, updatedCharacter.getAttacksByCharacter(otherCharacter.id))

    }

    @Test
    fun `cannot update attacks of characters not in theme`() {
        val newAttack = "I attack"
        val otherCharacter = makeCharacter()
        val themeWithAntagonist = theme.withCharacterIncluded(otherCharacter)
            .withCharacterAsStoryFunctionForMajorCharacter(
                otherCharacter.id,
                StoryFunction.Antagonist,
                majorCharacterInTheme.id
            )
        assertThrows<CharacterNotInTheme> {
            themeWithAntagonist.withCharacterAttackingMajorCharacter(
                    makeCharacter().id,
                    newAttack,
                    majorCharacterInTheme.id
                )
        }
    }
}