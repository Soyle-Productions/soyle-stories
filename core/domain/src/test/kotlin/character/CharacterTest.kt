package com.soyle.stories.domain.character

import com.soyle.stories.domain.mustEqual
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

	@Test
	fun `can create a new name variant`() {
		val character = makeCharacter()
		val update = character.withNameVariant(NonBlankString.create("Frank")!!)
		update as CharacterUpdate.Updated
		update.event.characterId.mustEqual(character.id)
		update.event.newVariant.mustEqual("Frank")
		update.character.otherNames.find { it.value == "Frank" }!!
	}

	@Test
	fun `can create a second name variant`() {
		val character = makeCharacter()
		val update = character.withNameVariant(NonBlankString.create("Frank")!!)
			.character.withNameVariant(NonBlankString.create("Fred")!!)
		update.character.otherNames.contains(NonBlankString.create("Frank")!!).mustEqual(true)
		update.character.otherNames.contains(NonBlankString.create("Fred")!!).mustEqual(true)
	}

	@Test
	fun `name variant cannot be the same as the display name`() {
		val character = makeCharacter(name = NonBlankString.create("Frank")!!)
		val update = character.withNameVariant(NonBlankString.create("Frank")!!)
		update as CharacterUpdate.WithoutChange
		update.reason.mustEqual(CharacterNameVariantCannotEqualDisplayName(character.id, "Frank"))
	}

	@Test
	fun `name variant cannot be the same another name variant`() {
		val character = makeCharacter()
		val update = character.withNameVariant(NonBlankString.create("Frank")!!)
			.character.withNameVariant(NonBlankString.create("Frank")!!)
		update as CharacterUpdate.WithoutChange
		update.reason.mustEqual(CharacterNameVariantCannotEqualOtherVariant(character.id, "Frank"))
	}
}