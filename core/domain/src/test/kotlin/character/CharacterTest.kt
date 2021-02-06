package com.soyle.stories.domain.character

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class CharacterTest {

	val projectId = Project.Id()

	@Test
	fun `characters are built`() {
		Character.buildNewCharacter(projectId, NonBlankString.create("Bob")!!)
	}

	@Test
	fun `characters can be renamed`() {
		val character = Character.buildNewCharacter(projectId, NonBlankString.create("Bob")!!)
		  .withName(NonBlankString.create("Frank")!!)
		assertEquals("Frank", character.name.value)
	}
}