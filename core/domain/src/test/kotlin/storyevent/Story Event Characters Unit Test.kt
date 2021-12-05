package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.storyevent.events.CharacterInvolvedInStoryEvent
import com.soyle.stories.domain.storyevent.events.CharacterRemovedFromStoryEvent
import com.soyle.stories.domain.storyevent.exceptions.StoryEventAlreadyInvolvesCharacter
import com.soyle.stories.domain.storyevent.exceptions.StoryEventAlreadyWithoutCharacter
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.TestFactory

class `Story Event Characters Unit Test` {

    private val characterId = Character.Id()
    private val storyEvent = makeStoryEvent()

    @Nested
    inner class `Involve Character` {

        @TestFactory
        fun `Given Character has not Already Been Involved`(): List<DynamicNode> {
            val update = storyEvent.withCharacterInvolved(characterId)

            return listOf(
                dynamicTest("should involve character") {
                    assertTrue(update.storyEvent.involvedCharacters.contains(characterId))
                },
                dynamicTest("should produce successful update with event") {
                    update as Successful
                    update.change.mustEqual(CharacterInvolvedInStoryEvent(storyEvent.id))
                }
            )
        }

        @TestFactory
        fun `Given Character has Already Been Involved`(): List<DynamicTest> {
            val (storyEvent) = storyEvent.withCharacterInvolved(characterId)

            val update = storyEvent.withCharacterInvolved(characterId)

            return listOf(
                dynamicTest("should only involve the character once") {
                    update.storyEvent.involvedCharacters.mustEqual(setOf(characterId))
                },
                dynamicTest("should produce unsuccessful update") {
                    update as UnSuccessful
                    update.reason.mustEqual(StoryEventAlreadyInvolvesCharacter(storyEvent.id, characterId))
                }
            )
        }

    }

    @Nested
    inner class `Remove Character` {

        @TestFactory
        fun `Given Story Event Does Not Involve Character`(): List<DynamicTest> {
            val update = storyEvent.withCharacterRemoved(characterId)

            return listOf(
                dynamicTest("should not have character involved") {
                    update.storyEvent.involvedCharacters.mustEqual(emptySet<Character.Id>())
                },
                dynamicTest("should produce unsuccessful update") {
                    update as UnSuccessful
                    update.reason.mustEqual(StoryEventAlreadyWithoutCharacter(storyEvent.id, characterId))
                }
            )
        }

        @TestFactory
        fun `Given Story Event Involves Character`(): List<DynamicTest> {
            val (storyEvent) = storyEvent.withCharacterInvolved(characterId)

            val update = storyEvent.withCharacterRemoved(characterId)

            return listOf(
                dynamicTest("should not have character involved") {
                    update.storyEvent.involvedCharacters.mustEqual(emptySet<Character.Id>())
                },
                dynamicTest("should produce successful update with event") {
                    update as Successful
                    update.change.mustEqual(CharacterRemovedFromStoryEvent(storyEvent.id, characterId))
                }
            )
        }
    }
}