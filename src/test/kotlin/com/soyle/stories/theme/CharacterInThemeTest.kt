package com.soyle.stories.theme

import arrow.core.Either
import arrow.core.flatMap
import com.soyle.stories.entities.theme.characterInTheme.MajorCharacter
import com.soyle.stories.entities.theme.characterInTheme.MinorCharacter
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 3:42 PM
 */
class CharacterInThemeTest {

	@Test
	fun `characters in theme have an archetype`() {
		themeWithCharacter.getIncludedCharacterById(newCharacter.id)!!.archetype
	}

	@Test
	fun `can change archetype`() {
		val characterInTheme = themeWithCharacter.getIncludedCharacterById(newCharacter.id)!!
		val (theme) = themeWithCharacter
			.changeArchetype(characterInTheme, newArchetype) as Either.Right
		val archetype = theme.getIncludedCharacterById(newCharacter.id)!!.archetype
		assertEquals(newArchetype, archetype)
	}

	@Test
	fun `cannot change archetype of character not in theme`() {
		val characterInTheme = themeWithCharacter.getIncludedCharacterById(newCharacter.id)!!
		val (error) = themeWithoutCharacter
			.changeArchetype(characterInTheme, newArchetype) as Either.Left
		assert(error is CharacterNotInTheme)
	}

	@Test
	fun `characters in theme have a variation on the central moral`() {
		themeWithCharacter.getIncludedCharacterById(newCharacter.id)!!.variationOnMoral
	}

	@Test
	fun `can change variation on the central moral`() {
		val characterInTheme = themeWithCharacter.getIncludedCharacterById(newCharacter.id)!!
		val (theme) = themeWithCharacter
			.changeVariationOnMoral(characterInTheme, newVariationOnMoral) as Either.Right
		val variationOnMoral = theme.getIncludedCharacterById(newCharacter.id)!!.variationOnMoral
		assertEquals(newVariationOnMoral, variationOnMoral)
	}

	@Test
	fun `cannot change variation on the central moral of character not in theme`() {
		val characterInTheme = themeWithCharacter.getIncludedCharacterById(newCharacter.id)!!
		val (error) = themeWithoutCharacter
			.changeVariationOnMoral(characterInTheme, newVariationOnMoral) as Either.Left
		assert(error is CharacterNotInTheme)
	}

	@Test
	fun `can promote minor characters`() {
		val (theme) = promoteCharacter() as Either.Right
		assert(theme.getMajorCharacterById(newCharacter.id) != null)
	}

	@Test
	fun `cannot promote characters not in theme`() {
		val characterInTheme = themeWithCharacter.getMinorCharacterById(newCharacter.id)!!
		assertThrows<CharacterNotInTheme> {
			themeWithoutCharacter.withCharacterPromoted(characterInTheme.id)
		}
	}

	@Test
	fun `can demote major characters`() {
		val (theme) = promoteCharacter().flatMap {
			it.demoteCharacter(it.getMajorCharacterById(newCharacter.id)!!)
		} as Either.Right
		assert(theme.getMinorCharacterById(newCharacter.id) is MinorCharacter)
	}

	@Test
	fun `cannot demote characters not in theme`() {
		val (characterInTheme) = promoteCharacter()
			.map { it.getMajorCharacterById(newCharacter.id) } as Either.Right
		characterInTheme as MajorCharacter
		val (error) = themeWithoutCharacter
			.demoteCharacter(characterInTheme) as Either.Left
		assert(error is CharacterNotInTheme)
	}
}