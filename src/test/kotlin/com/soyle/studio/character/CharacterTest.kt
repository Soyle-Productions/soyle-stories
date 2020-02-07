package com.soyle.studio.character

import arrow.core.Either
import arrow.core.flatMap
import com.soyle.studio.character.events.CharacterBuilt
import com.soyle.studio.character.events.CharacterRenamed
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Created by Brendan
 * Date: 2/6/2020
 * Time: 8:18 PM
 */
class CharacterTest {

	val projectId = UUID.randomUUID()

	@Nested
	inner class CreationTests {

		val characterName = "Bob"

		fun operation() = Character.buildNewCharacter(projectId, characterName)

		@Test
		fun projectIdAndNameShouldBeSame() {
			val (character) = operation() as Either.Right
			assertEquals(projectId, character.projectId)
			assertEquals(characterName, character.name)
		}

		@Test
		fun buildNewCharacterShouldProduceEvent() {
			val (character) = operation() as Either.Right
			assertEquals(CharacterBuilt(projectId, character.id), character.events.single())
		}

		@Test
		fun cannotBuildCharacterWithBlankName() {
			val (error) = Character.buildNewCharacter(projectId, "  \n  \r  \r\n") as Either.Left
			assert(error is CharacterNameCannotBeBlank)
		}

		@Test
		fun cannotExternallyProvideEvents() {
			Character(
				Character.Id(UUID.randomUUID()),
				projectId,
				""
				// events not provided as a different constructor should be called
			)
		}
	}

	@Nested
	inner class RenameTests {

		val initialCharacter = Character.buildNewCharacter(projectId, "Frank")

		@Test
		fun canRenameCharacter() {
			val (character) = initialCharacter
				.flatMap { it.rename("Bob") }
			  as Either.Right

			assertEquals("Bob", character.name)
		}

		@Test
		fun renameShouldProduceEvent() {
			val (character) = initialCharacter
				.flatMap { it.rename("Bob") }
			  as Either.Right

			assert(character.events.contains(CharacterRenamed(character.id, "Bob")))
		}

		@Test
		fun cannotRenameCharacterToBlankName() {
			val (error) = initialCharacter
				.flatMap { it.rename("  \n   \n   \r\n") } as Either.Left
			assert(error is CharacterNameCannotBeBlank)
		}
	}

}