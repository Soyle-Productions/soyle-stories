package com.soyle.stories.theme

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.soyle.stories.character.makeCharacter
import com.soyle.stories.common.shouldBe
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

/**
 * Created by Brendan
 * Date: 2/5/2020
 * Time: 3:46 PM
 */
class ThemeTest {


	@Test
	fun `themes are noted`() {
		takeNoteOfTheme()
	}

	@Test
	fun `themes have a central moral question`() {
		val theme = takeNoteOfTheme()
		theme.centralMoralQuestion
	}

	@Test
	fun `the central moral question can be changed`() {
		val centralMoralQuestion = "What does it all mean?"
		val (theme) = takeNoteOfTheme()
			.changeCentralMoralQuestion(centralMoralQuestion) as Either.Right
		assertEquals(centralMoralQuestion, theme.centralMoralQuestion)
	}

	@Test
	fun `themes have a list of included characters`() {
		val theme = takeNoteOfTheme()
		theme.characters
	}

	@Test
	fun `character can be included in a theme`() {
		val theme = takeNoteOfTheme().withCharacterIncluded(newCharacter.id, newCharacter.name, newCharacter.media)
		assert(theme.containsCharacter(newCharacter.id))
	}

	@Test
	fun `a character cannot be included in a theme more than once`() {
		val themeWithCharacter = makeTheme().withCharacterIncluded(newCharacter.id, newCharacter.name, newCharacter.media)
		val error = assertThrows<CharacterAlreadyIncludedInTheme> {
			themeWithCharacter.withCharacterIncluded(newCharacter.id, newCharacter.name, newCharacter.media)
		}
		assertEquals(themeWithCharacter.id.uuid, error.themeId)
		assertEquals(newCharacter.id.uuid, error.characterId)
	}

	@Test
	fun `character can be removed from a theme`() {
		val (theme) = takeNoteOfTheme()
			.withCharacterIncluded(newCharacter.id, newCharacter.name, newCharacter.media)
			.removeCharacter(newCharacter.id) as Either.Right
		assert(! theme.containsCharacter(newCharacter.id))
	}

	@Test
	fun `cannot remove character not in theme`() {
		val (error) = takeNoteOfTheme()
			.removeCharacter(newCharacter.id) as Either.Left
		assert(error is CharacterNotInTheme)
	}

	@Nested
	inner class ForPairsOfCharacters {

		val characterA = makeCharacter(
            Character.Id(UUID.randomUUID()), Project.Id(), "Name"
        )
		val characterB = makeCharacter(
            Character.Id(UUID.randomUUID()), Project.Id(), "Name"
        )

		val themeWithCharacterA = (takeNoteOfTheme()
			.withCharacterIncluded(characterA.id, characterA.name, characterA.media).right() as Either.Right).b

		val newSimilarities = "We're similar"

		@Test
		fun `pairs of characters have similarities`() {
			themeWithCharacterA
				.withCharacterIncluded(characterB.id, characterB.name, characterB.media).right()
				.flatMap { it.getSimilarities(characterA.id, characterB.id) } as Either.Right
		}

		@Test
		fun `characters not in theme are not similar`() {
			val (error) = themeWithCharacterA
				.getSimilarities(characterA.id, characterB.id) as Either.Left

			assert(error is CharacterNotInTheme)
		}

		@Test
		fun `can change similarities`() {
			val (similarities) = themeWithCharacterA
				.withCharacterIncluded(characterB.id, characterB.name, characterB.media).right()
				.flatMap { it.changeSimilarities(characterA.id, characterB.id, newSimilarities) }
				.flatMap { it.getSimilarities(characterA.id, characterB.id) } as Either.Right
			assertEquals(newSimilarities, similarities)
		}

		@Test
		fun `character must be in theme to change similarities`() {
			val (error) = themeWithCharacterA
				.changeSimilarities(characterA.id, characterB.id, newSimilarities) as Either.Left
			assert(error is CharacterNotInTheme)
		}

	}

}