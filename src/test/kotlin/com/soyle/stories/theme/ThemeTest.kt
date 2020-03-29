package com.soyle.stories.theme

import arrow.core.Either
import arrow.core.flatMap
import com.soyle.stories.entities.Character
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
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
		val (theme) = takeNoteOfTheme().includeCharacter(newCharacter) as Either.Right
		assert(theme.containsCharacter(newCharacter.id))
	}

	@Test
	fun `a character cannot be included in a theme more than once`() {
		val (error) = takeNoteOfTheme()
			.includeCharacter(newCharacter)
			.flatMap { it.includeCharacter(newCharacter) } as Either.Left
		assert(error is CharacterAlreadyIncludedInTheme)
	}

	@Test
	fun `character can be removed from a theme`() {
		val (theme) = takeNoteOfTheme()
			.includeCharacter(newCharacter)
			.flatMap { it.removeCharacter(newCharacter.id) } as Either.Right
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

		val characterA = Character(
            Character.Id(UUID.randomUUID()), UUID.randomUUID(), "Name"
        )
		val characterB = Character(
            Character.Id(UUID.randomUUID()), UUID.randomUUID(), "Name"
        )

		val themeWithCharacterA = (takeNoteOfTheme()
			.includeCharacter(characterA) as Either.Right).b

		val newSimilarities = "We're similar"

		@Test
		fun `pairs of characters have similarities`() {
			themeWithCharacterA
				.includeCharacter(characterB)
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
				.includeCharacter(characterB)
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