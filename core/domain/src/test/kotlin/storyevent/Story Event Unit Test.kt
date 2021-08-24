package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*
import java.util.UUID.randomUUID

class `Story Event Unit Test` {

    @Nested
    inner class `Create Story Event` {

        val inputName = nonBlankStr("New Story Event Name ${randomUUID()}")
        val inputTime = (0L .. 10L).random()
        val projectId = Project.Id()

        @Test
        fun `should create story event with provided name and time`() {
            val update: StoryEventUpdate<StoryEventCreated> = StoryEvent.create(inputName, inputTime, projectId)

            update.storyEvent.name.mustEqual(inputName)
            update.storyEvent.time.mustEqual(inputTime)
            update.storyEvent.projectId.mustEqual(projectId)

        }

        @Test
        fun `should produce story event created event`() {
            val update: StoryEventUpdate<StoryEventCreated> = StoryEvent.create(inputName, inputTime, projectId)
            update as Successful

            update.change.storyEventId.mustEqual(update.storyEvent.id)
            update.change.name.mustEqual(inputName.value)
            update.change.time.mustEqual(inputTime)
            update.change.projectId.mustEqual(projectId)

        }

    }

    @Nested
    inner class `Rename Story Event` {

        private val storyEvent = makeStoryEvent()
        private val inputName = nonBlankStr("Story Event Name ${randomUUID()}")

        @Test
        fun `should rename story event to provided name`() {
            val update: StoryEventUpdate<StoryEventRenamed> = storyEvent.withName(inputName)

            update.storyEvent.id.mustEqual(storyEvent.id)
            update.storyEvent.name.mustEqual(inputName)
        }

        @Test
        fun `should produce story event renamed event`() {
            val update: StoryEventUpdate<StoryEventRenamed> = storyEvent.withName(inputName)
            update as Successful

            update.change.storyEventId.mustEqual(storyEvent.id)
            update.change.newName.mustEqual(inputName)
        }

        @Nested
        inner class `When name is identical` {

            @Test
            fun `should not produce event`() {
                val update: StoryEventUpdate<StoryEventRenamed> = storyEvent.withName(storyEvent.name)
                update as UnSuccessful

                update.storyEvent.name.mustEqual(storyEvent.name)
            }

        }

    }

}