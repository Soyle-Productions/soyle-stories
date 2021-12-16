package com.soyle.stories.domain.character

import com.soyle.stories.domain.character.name.events.CharacterDisplayNameSelected
import com.soyle.stories.domain.character.name.events.CharacterNameAdded
import com.soyle.stories.domain.character.name.events.CharacterNameRemoved
import com.soyle.stories.domain.character.name.events.CharacterRenamed
import com.soyle.stories.domain.character.name.exceptions.CannotRemoveDisplayName
import com.soyle.stories.domain.character.name.exceptions.CharacterAlreadyHasName
import com.soyle.stories.domain.character.name.exceptions.CharacterDisplayNameAlreadySet
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Character Name Test` {

    @Nested
    inner class `Can Add Additional Names` {

        private val character = Character.buildNewCharacter(Project.Id(), nonBlankStr("Some Name"))

        @Test
        fun `cannot add name that is same as display name`() {
            val update: CharacterUpdate<CharacterNameAdded> = character.withName(nonBlankStr("Some Name"))

            update as CharacterUpdate.WithoutChange
            update.reason.mustEqual(CharacterAlreadyHasName(character.id, "Some Name"))
        }

        @Test
        fun `should succeed if new name is unique`() {
            val character = Character.buildNewCharacter(Project.Id(), nonBlankStr("Some Name"))

            val update: CharacterUpdate<CharacterNameAdded> = character.withName(nonBlankStr("Some other name"))

            update as CharacterUpdate.Updated
            update.event.mustEqual(CharacterNameAdded(character.id, "Some other name"))
        }

        @Test
        fun `cannot add same name twice`() {
            val character = Character.buildNewCharacter(Project.Id(), nonBlankStr("Some Name"))
                .withName(nonBlankStr("Some other name"))
                .character

            val update: CharacterUpdate<CharacterNameAdded> = character.withName(nonBlankStr("Some other name"))

            update as CharacterUpdate.WithoutChange
            update.reason.mustEqual(CharacterAlreadyHasName(character.id, "Some other name"))
        }

    }

    @Nested
    inner class `Can Remove Names` {

        private val character = Character.buildNewCharacter(Project.Id(), nonBlankStr("Some Name"))

        @Test
        fun `cannot remove display name`() {
            val update: CharacterUpdate<CharacterNameRemoved> = character.withName("Some Name")!!.removed()

            update as CharacterUpdate.WithoutChange
            update.reason.mustEqual(CannotRemoveDisplayName(character.id, "Some Name"))
        }

        @Test
        fun `can remove secondary names`() {
            val character = character.withName(nonBlankStr("Some Other Name")).character

            val update = character.withName("Some Other Name")!!.removed()

            update as CharacterUpdate.Updated
            update.event.mustEqual(CharacterNameRemoved(character.id, "Some Other Name"))
        }

        @Test
        fun `cannot remove name if character doesn't have it`() {
            val update = character.withName("Some Other Name")?.removed()

            assertNull(update)
        }

        @Test
        fun `cannot remove a secondary name twice`() {
            val character = character
                .withName(nonBlankStr("Some Other Name")).character
                .withName("Some Other Name")!!.removed().character

            val update = character.withName("Some Other Name")?.removed()

            assertNull(update)
        }

    }

    @Nested
    inner class `Can Rename Character` {

        private val character = Character.buildNewCharacter(Project.Id(), nonBlankStr("Some Name"))

        @Test
        fun `can rename display name`() {
            val update: CharacterUpdate<CharacterRenamed> = character.withName("Some Name")!!.renamed(nonBlankStr("New Name"))

            update as CharacterUpdate.Updated
            update.event.mustEqual(CharacterRenamed(character.id, oldName = "Some Name", name = "New Name"))
        }

        @Test
        fun `cannot rename character name that doesn't exist`() {
            val update = character.withName("Some Other Name")?.renamed(nonBlankStr("Other New Name"))

            assertNull(update)
        }

        @Test
        fun `can rename secondary names if new name is unique`() {
            val character = character.withName(nonBlankStr("Some Other Name"))
                .character

            val update: CharacterUpdate<CharacterRenamed> = character.withName("Some Other Name")!!.renamed(nonBlankStr("Other New Name"))

            update as CharacterUpdate.Updated
            update.event.mustEqual(CharacterRenamed(character.id, oldName = "Some Other Name", name = "Other New Name"))
        }

        @Test
        fun `cannot update name to the same value`() {
            val character = character.withName(nonBlankStr("Some Other Name"))
                .character

            val update: CharacterUpdate<CharacterRenamed> = character.withName("Some Other Name")!!.renamed(nonBlankStr("Some Other Name"))

            update as CharacterUpdate.WithoutChange
            update.reason.mustEqual(CharacterAlreadyHasName(character.id, "Some Other Name"))
        }

        @Test
        fun `cannot update name to the same value as another name`() {
            val character = character.withName(nonBlankStr("Some Other Name"))
                .character

            val update: CharacterUpdate<CharacterRenamed> = character.withName("Some Other Name")!!.renamed(nonBlankStr("Some Name"))

            update as CharacterUpdate.WithoutChange
            update.reason.mustEqual(CharacterAlreadyHasName(character.id, "Some Name"))
        }

    }

    @Nested
    inner class `Can Set Display Name` {

        private val character = Character.buildNewCharacter(Project.Id(), nonBlankStr("Some Name"))

        @Test
        fun `setting the same display name should not produce update`() {
            val update = character.withName("Some Name")!!.asDisplayName()

            update as CharacterUpdate.WithoutChange
            update.reason.mustEqual(CharacterDisplayNameAlreadySet(character.id, "Some Name"))
        }

        @Test
        fun `cannot set display name if character does not have name`() {
            val update = character.withName("Some Other Name")?.asDisplayName()

            assertNull(update)
        }

        @Test
        fun `can set display name to existing secondary name`() {
            val character = character.withName(nonBlankStr("Some Other Name")).character

            val update = character.withName("Some Other Name")!!.asDisplayName()

            update as CharacterUpdate.Updated
            update.event.mustEqual(CharacterDisplayNameSelected(character.id, oldName = "Some Name", name = "Some Other Name"))
        }

        @Test
        fun `cannot set display name to same name twice`() {
            val character = character.withName(nonBlankStr("Some Other Name"))
                .character.withName("Some Other Name")!!.asDisplayName()
                .character

            val update = character.withName("Some Other Name")!!.asDisplayName()

            update as CharacterUpdate.WithoutChange
            update.reason.mustEqual(CharacterDisplayNameAlreadySet(character.id, "Some Other Name"))
        }

        @Test
        fun `can switch display name back to original`() {
            val character = character.withName(nonBlankStr("Some Other Name"))
                .character.withName("Some Other Name")!!.asDisplayName()
                .character

            val update = character.withName("Some Name")!!.asDisplayName()

            update as CharacterUpdate.Updated
            update.event.mustEqual(CharacterDisplayNameSelected(character.id, oldName = "Some Other Name", name = "Some Name"))
        }

    }

}