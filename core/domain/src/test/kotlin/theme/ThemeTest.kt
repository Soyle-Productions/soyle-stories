package com.soyle.stories.domain.theme

import arrow.core.Either
import arrow.core.flatMap
import com.soyle.stories.domain.character.makeCharacter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ThemeTest {



	@Test
	fun `themes have a central moral question`() {
		val theme = makeTheme()
		theme.centralMoralProblem
	}

	@Test
	fun `the central moral question can be changed`() {
		val centralMoralQuestion = "What does it all mean?"
		val theme = makeTheme()
			.withMoralProblem(centralMoralQuestion)
		assertEquals(centralMoralQuestion, theme.centralMoralProblem)
	}

	@Test
	fun `themes have a list of included characters`() {
		val theme = makeTheme()
		theme.characters
	}

	@Test
	fun `character can be included in a theme`() {
		val newCharacter = makeCharacter()
		val theme = makeTheme().withCharacterIncluded(newCharacter.id, newCharacter.name.value, newCharacter.media)
		assert(theme.containsCharacter(newCharacter.id))
	}

	@Test
	fun `a character cannot be included in a theme more than once`() {
		val newCharacter = makeCharacter()
		val themeWithCharacter = makeTheme().withCharacterIncluded(newCharacter.id, newCharacter.name.value, newCharacter.media)
		val error = assertThrows<CharacterAlreadyIncludedInTheme> {
			themeWithCharacter.withCharacterIncluded(newCharacter.id, newCharacter.name.value, newCharacter.media)
		}
		assertEquals(themeWithCharacter.id.uuid, error.themeId)
		assertEquals(newCharacter.id.uuid, error.characterId)
	}

	@Test
	fun `character can be removed from a theme`() {
		val newCharacter = makeCharacter()
		val (theme) = makeTheme()
			.withCharacterIncluded(newCharacter.id, newCharacter.name.value, newCharacter.media)
			.removeCharacter(newCharacter.id) as Either.Right
		assert(! theme.containsCharacter(newCharacter.id))
	}

	@Test
	fun `cannot remove character not in theme`() {
		val newCharacter = makeCharacter()
		val (error) = makeTheme()
			.removeCharacter(newCharacter.id) as Either.Left
		assert(error is CharacterNotInTheme)
	}

	@Nested
	inner class ForPairsOfCharacters {

		val characterA = makeCharacter()
		val characterB = makeCharacter()

		val themeWithCharacterA = makeTheme()
			.withCharacterIncluded(characterA.id, characterA.name.value, characterA.media)

		val newSimilarities = "We're similar"

		@Test
		fun `pairs of characters have similarities`() {
			themeWithCharacterA
				.withCharacterIncluded(characterB.id, characterB.name.value, characterB.media)
				.getSimilarities(characterA.id, characterB.id)
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
				.withCharacterIncluded(characterB.id, characterB.name.value, characterB.media)
				.changeSimilarities(characterA.id, characterB.id, newSimilarities)
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