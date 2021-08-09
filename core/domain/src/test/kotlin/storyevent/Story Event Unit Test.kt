package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import java.util.UUID.randomUUID

class `Story Event Unit Test` {

    @Nested
    inner class `Create Story Event` {

        @Test
        fun `should create story event with provided name and time`() {
            val inputName = nonBlankStr("New Story Event Name ${randomUUID()}")
            val inputTime = (0L .. 10L).random()

            val update: StoryEventUpdate<StoryEventCreated> = StoryEvent.create(inputName, inputTime)

            update.storyEvent.name.mustEqual(inputName)
            update.storyEvent.time.mustEqual(inputTime)
        }

        @Test
        fun `should produce story event created event`() {
            val inputName = nonBlankStr("New Story Event Name ${randomUUID()}")
            val inputTime = (0L .. 10L).random()

            val update: StoryEventUpdate<StoryEventCreated> = StoryEvent.create(inputName, inputTime)
            update as Successful

            update.change.storyEventId.mustEqual(update.storyEvent.id)
            update.change.name.mustEqual(inputName.value)
            update.change.time.mustEqual(inputTime)

        }

    }

}