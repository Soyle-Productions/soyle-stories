package com.soyle.stories.domain.character

import com.soyle.stories.domain.character.events.CharacterNameVariantRemoved
import com.soyle.stories.domain.character.events.CharacterNameVariantRenamed
import com.soyle.stories.domain.character.events.CharacterRenamed
import com.soyle.stories.domain.character.exceptions.characterAlreadyHasName
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CharacterTest {

    val projectId = Project.Id()

    @Test
    fun `characters are built`() {
        Character.buildNewCharacter(projectId, NonBlankString.create("Bob")!!)
    }

    @Nested
    inner class `Rename Character` {

        @Test
        fun `should produce update when new name provided`() {
            val character = Character.buildNewCharacter(projectId, NonBlankString.create("Bob")!!)

            val update = character.withName(NonBlankString.create("Frank")!!)

            update as CharacterUpdate.Updated
            assertEquals("Frank", update.character.name.value)
            update.event.mustEqual(CharacterRenamed(character.id, "Frank"))
        }

        @Test
        fun `should not produce update when new name is the same`() {
            val character = Character.buildNewCharacter(projectId, NonBlankString.create("Bob")!!)

            val update = character.withName(NonBlankString.create("Bob")!!)

            update as CharacterUpdate.WithoutChange
            assertEquals("Bob", update.character.name.value)
            update.reason.mustEqual(characterAlreadyHasName(character.id, "Bob"))
        }

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

    @Test
    fun `can modify a name variant`() {
        val originalVariant = NonBlankString.create("Frank")!!
        val newVariant = NonBlankString.create("George")!!
        val character = makeCharacter().withNameVariant(originalVariant).character
        val update = character.withNameVariantModified(originalVariant, newVariant)
        update as CharacterUpdate.Updated

        update.event.mustEqual(CharacterNameVariantRenamed(character.id, originalVariant, newVariant))
        update.character.otherNames.single { it == newVariant }
        assertFalse(update.character.otherNames.contains(originalVariant))
    }

    @Test
    fun `cannot modify a name variant that does not exist`() {
        val originalVariant = NonBlankString.create("Frank")!!
        val newVariant = NonBlankString.create("George")!!
        val character = makeCharacter()
        val update = character.withNameVariantModified(originalVariant, newVariant)

        update as CharacterUpdate.WithoutChange
        update.reason.mustEqual(CharacterDoesNotHaveNameVariant(character.id, originalVariant.value))
    }

	@Test
	fun `cannot modify name variant to be same as display name`() {
		val originalVariant = NonBlankString.create("Frank")!!
		val character = makeCharacter().withNameVariant(originalVariant).character
		val update = character.withNameVariantModified(originalVariant, character.name)

		update as CharacterUpdate.WithoutChange
		update.reason.mustEqual(CharacterNameVariantCannotEqualDisplayName(character.id, character.name.value))
	}

	@Test
	fun `cannot modify name variant to be the same as another name variant`() {
		val originalVariant = NonBlankString.create("Frank")!!
		val newVariant = NonBlankString.create("George")!!
		val character = makeCharacter().withNameVariant(originalVariant)
			.character.withNameVariant(newVariant)
			.character
		val update = character.withNameVariantModified(originalVariant, newVariant)

		update as CharacterUpdate.WithoutChange
		update.reason.mustEqual(CharacterNameVariantCannotEqualOtherVariant(character.id, "George"))
	}

	@Test
	fun `modifying name variant without a change should produce no update`() {
		val originalVariant = NonBlankString.create("Frank")!!
		val character = makeCharacter().withNameVariant(originalVariant).character
		val update = character.withNameVariantModified(originalVariant, originalVariant)

		update as CharacterUpdate.WithoutChange
		assertNull(update.reason)
	}

    @Test
    fun `can remove name variant`() {
        val nameVariant = characterName()
        val character = makeCharacter().withNameVariant(nameVariant).character
        val update = character.withoutNameVariant(nameVariant)

        update as CharacterUpdate.Updated
        update.event.mustEqual(CharacterNameVariantRemoved(character.id, nameVariant))
        update.character.otherNames.contains(nameVariant).mustEqual(false)
    }

    @Test
    fun `cannot remove name variant that doesn't exist`() {
        val nameVariant = characterName()
        val character = makeCharacter()
        val update = character.withoutNameVariant(nameVariant)

        update as CharacterUpdate.WithoutChange
        update.reason.mustEqual(CharacterDoesNotHaveNameVariant(character.id, nameVariant.value))
    }


}