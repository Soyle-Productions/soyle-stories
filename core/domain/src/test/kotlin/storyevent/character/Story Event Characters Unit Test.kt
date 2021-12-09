package com.soyle.stories.domain.storyevent.character

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.characterName
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.domain.storyevent.UnSuccessful
import com.soyle.stories.domain.storyevent.character.changes.CharacterInvolvedInStoryEvent
import com.soyle.stories.domain.storyevent.character.changes.CharacterInvolvedWithStoryEventRenamed
import com.soyle.stories.domain.storyevent.character.changes.CharacterRemovedFromStoryEvent
import com.soyle.stories.domain.storyevent.character.exceptions.involvedCharacterAlreadyHasName
import com.soyle.stories.domain.storyevent.character.exceptions.storyEventAlreadyInvolvesCharacter
import com.soyle.stories.domain.storyevent.character.exceptions.storyEventAlreadyWithoutCharacter
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.domain.validation.noEntities
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest.dynamicTest

class `Story Event Characters Unit Test` {

    private val character = makeCharacter()
    private val storyEvent = makeStoryEvent()

    @Nested
    inner class `Involve Character` {

        @TestFactory
        fun `Given Character has not Already Been Involved`(): List<DynamicNode> {
            val update = storyEvent.withCharacterInvolved(character)

            return listOf(
                dynamicTest("should involve character") {
                    assertTrue(update.storyEvent.involvedCharacters.containsEntityWithId(character.id))
                    update.storyEvent.involvedCharacters.getEntityById(character.id)!!.name.mustEqual(character.name.value)
                },
                dynamicTest("should produce successful update with event") {
                    update as Successful
                    update.change.mustEqual(CharacterInvolvedInStoryEvent(storyEvent.id, character.id, character.name.value))
                }
            )
        }

        @TestFactory
        fun `Given Character has Already Been Involved`(): List<DynamicTest> {
            val (storyEvent) = storyEvent.withCharacterInvolved(character)

            val update = storyEvent.withCharacterInvolved(character)

            return listOf(
                dynamicTest("should only involve the character once") {
                    update.storyEvent.involvedCharacters.map { it.id }.mustEqual(listOf(character.id))
                },
                dynamicTest("should produce unsuccessful update") {
                    update as UnSuccessful
                    update.reason.mustEqual(storyEventAlreadyInvolvesCharacter(storyEvent.id, character.id))
                }
            )
        }

    }

    @Nested
    inner class `Remove Character` {

        @TestFactory
        fun `Given Story Event Does Not Involve Character`(): List<DynamicTest> {
            val update = storyEvent.withCharacterRemoved(character.id)

            return listOf(
                dynamicTest("should not have character involved") {
                    update.storyEvent.involvedCharacters.mustEqual(noEntities<Character.Id, InvolvedCharacter>())
                },
                dynamicTest("should produce unsuccessful update") {
                    update as UnSuccessful
                    update.reason.mustEqual(storyEventAlreadyWithoutCharacter(storyEvent.id, character.id))
                }
            )
        }

        @TestFactory
        fun `Given Story Event Involves Character`(): List<DynamicTest> {
            val (storyEvent) = storyEvent.withCharacterInvolved(character)

            val update = storyEvent.withCharacterRemoved(character.id)

            return listOf(
                dynamicTest("should not have character involved") {
                    update.storyEvent.involvedCharacters.mustEqual(noEntities<Character.Id, InvolvedCharacter>())
                },
                dynamicTest("should produce successful update with event") {
                    update as Successful
                    update.change.mustEqual(CharacterRemovedFromStoryEvent(storyEvent.id, character.id))
                }
            )
        }
    }

    @Nested
    inner class `Rename Involved Character` {

        @Test
        fun `cannot rename character that is not involved`() {
            assertNull(storyEvent.withCharacter(character.id)?.renamed(character.name.value))
        }

        @Test
        fun `should not produce update if new name is identical`() {
            val update = storyEvent.withCharacterInvolved(character)
                .storyEvent.withCharacter(character.id)!!.renamed(character.name.value)

            update as UnSuccessful

            update.reason.mustEqual(involvedCharacterAlreadyHasName(storyEvent.id, character.id, character.name.value))
        }

        @Test
        fun `should update story event and produce change when new name is different`() {
            val newName = characterName().value

            val update = storyEvent.withCharacterInvolved(character)
                .storyEvent.withCharacter(character.id)!!.renamed(newName)

            update as Successful

            update.storyEvent.involvedCharacters.getEntityById(character.id)!!.name.mustEqual(newName)

            update.change.mustEqual(CharacterInvolvedWithStoryEventRenamed(storyEvent.id, character.id, newName))
        }

    }
}