package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.character.makeCharacter
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.storyevent.events.CharacterInvolvedInStoryEvent
import com.soyle.stories.domain.storyevent.events.CharacterRemovedFromStoryEvent
import com.soyle.stories.domain.storyevent.exceptions.StoryEventAlreadyInvolvesCharacter
import com.soyle.stories.domain.storyevent.exceptions.StoryEventAlreadyWithoutCharacter
import com.soyle.stories.domain.validation.noEntities
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory

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
                    update.reason.mustEqual(StoryEventAlreadyInvolvesCharacter(storyEvent.id, character.id))
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
                    update.reason.mustEqual(StoryEventAlreadyWithoutCharacter(storyEvent.id, character.id))
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
}