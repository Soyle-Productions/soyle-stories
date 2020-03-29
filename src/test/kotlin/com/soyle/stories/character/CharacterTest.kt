package com.soyle.stories.character

import arrow.core.Either
import arrow.core.flatMap
import com.soyle.stories.entities.Character
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

	@Test
	fun `characters are built`() {
		Character.buildNewCharacter(projectId, "Bob") as Either.Right
	}

	@Test
	fun `characters can be renamed`() {
		val (character) = Character.buildNewCharacter(projectId, "Bob")
			.flatMap { it.rename("Frank") } as Either.Right
		assertEquals("Frank", character.name)
	}

	@Nested
	inner class `Character names cannot be blank` {

		val blankName = "  \r  \n  \r\n  "

		@Test
		fun `creation should return exception`() {
			val (error) = Character.buildNewCharacter(projectId, blankName) as Either.Left
			assert(error is CharacterNameCannotBeBlank)
		}

		@Test
		fun `rename should return exception`() {
			val (error) = Character.buildNewCharacter(projectId, "Bob")
				.flatMap { it.rename(blankName) } as Either.Left
			assert(error is CharacterNameCannotBeBlank)
		}

	}
}