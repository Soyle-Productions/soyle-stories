package com.soyle.stories.domain.theme

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
		val theme = makeTheme()
			.withCharacterIncluded(newCharacter.id, newCharacter.name.value, newCharacter.media)
			.withoutCharacter(newCharacter.id)
		assert(! theme.containsCharacter(newCharacter.id))
	}

	@Test
	fun `cannot remove character not in theme`() {
		val newCharacter = makeCharacter()
		assertThrows<CharacterNotInTheme> {
			makeTheme().withoutCharacter(newCharacter.id)
		}
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
				.getSimilaritiesBetweenCharacters(characterA.id, characterB.id)
		}

		@Test
		fun `characters not in theme are not similar`() {
			assertThrows<CharacterNotInTheme> {
				themeWithCharacterA.getSimilaritiesBetweenCharacters(characterA.id, characterB.id)
			}
		}

		@Test
		fun `can change similarities`() {
			val similarities = themeWithCharacterA
				.withCharacterIncluded(characterB.id, characterB.name.value, characterB.media)
				.withCharactersSimilarToEachOther(characterA.id, characterB.id, newSimilarities)
				.getSimilaritiesBetweenCharacters(characterA.id, characterB.id)
			assertEquals(newSimilarities, similarities)
		}

		@Test
		fun `character must be in theme to change similarities`() {
			assertThrows<CharacterNotInTheme> {
				themeWithCharacterA
					.withCharactersSimilarToEachOther(characterA.id, characterB.id, newSimilarities)
			}
		}

	}

}