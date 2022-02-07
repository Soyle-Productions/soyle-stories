package com.soyle.stories.domain.character

import com.soyle.stories.domain.character.events.CharacterRemovedFromStory
import com.soyle.stories.domain.character.exceptions.CharacterAlreadyRemovedFromStory
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.validation.NonBlankString
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CharacterTest {

    val projectId = Project.Id()

    @Test
    fun `characters are built`() {
        Character.buildNewCharacter(projectId, NonBlankString.create("Bob")!!)
    }

    @Nested
    inner class `Remove Character from Story` {

        @Test
        fun `can remove character from project`() {
            val character = Character.buildNewCharacter(projectId, NonBlankString.create("Bob")!!)

            val update = character.removedFromStory()

            update as CharacterUpdate.Updated
            update.event.mustEqual(CharacterRemovedFromStory(character.id, character.projectId!!))
        }

        @Test
        fun `cannot remove character twice`() {
            val (character) = Character.buildNewCharacter(projectId, NonBlankString.create("Bob")!!)
                .removedFromStory()

            val update = character.removedFromStory()

            update as CharacterUpdate.WithoutChange
            update.reason.mustEqual(CharacterAlreadyRemovedFromStory(character.id))
        }


    }

}