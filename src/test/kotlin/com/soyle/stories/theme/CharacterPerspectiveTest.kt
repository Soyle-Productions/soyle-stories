package com.soyle.stories.theme

import arrow.core.Either
import arrow.core.flatMap
import arrow.core.right
import com.soyle.stories.entities.Character
import com.soyle.stories.entities.Project
import com.soyle.stories.entities.Theme
import com.soyle.stories.entities.theme.CharacterPerspective
import com.soyle.stories.entities.theme.MajorCharacter
import com.soyle.stories.entities.theme.MinorCharacter
import com.soyle.stories.entities.theme.StoryFunction
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Created by Brendan
 * Date: 2/22/2020
 * Time: 4:38 PM
 */
class CharacterPerspectiveTest {

	val otherCharacters = List(5) { Character.Id(UUID.randomUUID()) }
		.map { Character(it, Project.Id(), it.uuid.toString()) }

	tailrec fun Theme.includeCharacters(characters: List<Character>): Either<ThemeException, Theme> {
		if (characters.isEmpty()) return this.right()
		val result = includeCharacter(characters.first())
		if (result !is Either.Right) return result
		return result.b.includeCharacters(characters.drop(1))
	}

	@Nested
	inner class `Each character in the theme serves a story function` {

		fun assertAllCharactersHaveStoryFunctions(theme: Theme) {
			val characterInTheme = theme.getMajorCharacterById(newCharacter.id)
			characterInTheme as MajorCharacter

			otherCharacters.forEach {
				assertNotNull(characterInTheme.getStoryFunctionsForCharacter(it.id))
			}
		}

		@Test
		fun `when included, all should have functions`() {
			val (theme) = promoteCharacter()
				.flatMap { it.includeCharacters(otherCharacters) } as Either.Right
			assertAllCharactersHaveStoryFunctions(theme)
		}

		@Test
		fun `when promoted, all should have functions`() {
			val (theme) = themeWithCharacter.includeCharacters(otherCharacters)
				.flatMap { it.promoteCharacter(it.getMinorCharacterById(newCharacter.id) as MinorCharacter) }
			  as Either.Right
			assertAllCharactersHaveStoryFunctions(theme)
		}

	}

	@Test
	fun `can apply story functions to characters`() {
		val (theme) = promoteCharacter()
			.flatMap { it.includeCharacters(otherCharacters) }
		  as Either.Right

		val characterInTheme = theme.getMajorCharacterById(newCharacter.id)
		characterInTheme as MajorCharacter

		val (updatedCharacter) = theme
			.applyStoryFunction(characterInTheme, otherCharacters.first().id, StoryFunction.Antagonist)
			.map { it.getMajorCharacterById(newCharacter.id) } as Either.Right
		updatedCharacter as MajorCharacter

		assert(updatedCharacter.hasStoryFunctionForTargetCharacter(StoryFunction.Antagonist, otherCharacters.first().id))
	}

	@Test
	fun `cannot apply story functions to characters not in theme`() {
		val (theme) = promoteCharacter()
			.flatMap { it.includeCharacters(otherCharacters) }
		  as Either.Right

		listOf(
			theme.getMajorCharacterById(newCharacter.id) as MajorCharacter to Character(
                Character.Id(UUID.randomUUID()),
                 Project.Id(),
                "Name"
            ),
			MajorCharacter(
				Character.Id(UUID.randomUUID()),
				"Name",
				"",
				"",
				listOf(),
				CharacterPerspective(mapOf(), mapOf())
			) to otherCharacters.first()
		).forEach { (characterInTheme, targetCharacter) ->
			val (error) = theme.applyStoryFunction(characterInTheme, targetCharacter.id, StoryFunction.Antagonist) as Either.Left
			assert(error is CharacterNotInTheme)
		}
	}

	@Test
	fun `cannot apply same story function more than once`() {
		val (theme) = promoteCharacter()
			.flatMap { it.includeCharacters(otherCharacters) }
		  as Either.Right

		val characterInTheme = theme.getMajorCharacterById(newCharacter.id)
		characterInTheme as MajorCharacter

		val (error) = theme
			.applyStoryFunction(characterInTheme, otherCharacters.first().id, StoryFunction.Antagonist)
			.flatMap {
				val updatedCharacter = it.getMajorCharacterById(newCharacter.id) as MajorCharacter
				it.applyStoryFunction(updatedCharacter, otherCharacters.first().id, StoryFunction.Antagonist)
			}
		  as Either.Left

		assert(error is StoryFunctionAlreadyApplied)
	}

	@Test
	fun `antagonistic characters attack the perspective character`() {
		val (characterInTheme) = promoteCharacter()
			.flatMap { it.includeCharacters(otherCharacters) }
			.flatMap {
				val characterInTheme = it.getMajorCharacterById(newCharacter.id) as MajorCharacter
				it.applyStoryFunction(characterInTheme, otherCharacters.first().id, StoryFunction.Antagonist)
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

		val (updatedCharacter) = theme
			.applyStoryFunction(characterInTheme, otherCharacters.first().id, StoryFunction.Antagonist)
			.flatMap {
				val updatedCharacter = it.getMajorCharacterById(newCharacter.id) as MajorCharacter
				it.changeAttack(updatedCharacter, otherCharacters.first().id, newAttack)
			}
			.map { it.getMajorCharacterById(newCharacter.id) } as Either.Right
		updatedCharacter as MajorCharacter

		assertEquals(newAttack, updatedCharacter.getAttacksByCharacter(otherCharacters.first().id))

	}

	@Test
	fun `cannot  update attacks of characters not in theme`() {
		val (theme) = promoteCharacter()
			.flatMap { it.includeCharacters(otherCharacters) }
		  as Either.Right

		listOf(
			theme.getMajorCharacterById(newCharacter.id) as MajorCharacter to Character(
                Character.Id(UUID.randomUUID()),
			  Project.Id(),
                "Name"
            ),
			MajorCharacter(
				Character.Id(UUID.randomUUID()),
				"Name",
				"",
				"",
				listOf(),
				CharacterPerspective(mapOf(), mapOf())
			) to otherCharacters.first()
		).forEach { (characterInTheme, targetCharacter) ->
			val (error) = theme.changeAttack(characterInTheme, targetCharacter.id, "") as Either.Left
			assert(error is CharacterNotInTheme)
		}
	}
}