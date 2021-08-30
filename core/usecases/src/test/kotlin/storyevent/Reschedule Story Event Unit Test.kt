package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.*
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.storyevent.time.reschedule.RescheduleStoryEvent
import com.soyle.stories.usecase.storyevent.time.reschedule.RescheduleStoryEventUseCase
import kotlinx.coroutines.*
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Reschedule Story Event Unit Test` {

    // Summary
    /** The time of the specified story event is changed to the new specified time */

    // Preconditions
    /** A project has been created and opened */
    private val projectId = Project.Id()
    /** The story event exists */
    private val storyEvent = makeStoryEvent(time = 27L)

    // post conditions
    /** outputs a story event rescheduled event */
    private var rescheduledStoryEvent: StoryEventRescheduled? = null
    /** updates the story event in the repository */
    private var updatedStoryEvent: StoryEvent? = null
    private val storyEventRepository = StoryEventRepositoryDouble(onUpdateStoryEvent = ::updatedStoryEvent::set)

    // Use Case
    private val useCase: RescheduleStoryEvent = RescheduleStoryEventUseCase(storyEventRepository)
    private fun rescheduleStoryEvent(inputTime: Long = storyEventTime()) {
        runBlocking {
            useCase.invoke(storyEvent.id, inputTime) {
                rescheduledStoryEvent = it.storyEventRescheduled
            }
        }
    }

    @Test
    fun `should throw error if story event doesn't exist`() {
        val error = assertThrows<StoryEventDoesNotExist> { rescheduleStoryEvent() }
        error.storyEventId.mustEqual(storyEvent.id.uuid)
    }

    @Nested
    inner class `Given Story Event Exists` {

        init {
            storyEventRepository.givenStoryEvent(storyEvent)
        }

        @Test
        fun `should update story event`() {
            val inputTime = 12L
            rescheduleStoryEvent(inputTime)
            updatedStoryEvent!!.mustEqual(storyEvent.withTime(inputTime).storyEvent)
        }

        @Test
        fun `should output story event rescheduled event`() {
            val inputTime = 47L
            rescheduleStoryEvent(inputTime)
            rescheduledStoryEvent!!.mustEqual((storyEvent.withTime(inputTime) as Successful).change)
        }

        @Nested
        inner class `When input time is same as current time` {

            @Test
            fun `should not update story event`() {
                rescheduleStoryEvent(storyEvent.time)
                Assertions.assertNull(updatedStoryEvent)
            }

            @Test
            fun `should not produce event`() {
                rescheduleStoryEvent(storyEvent.time)
                Assertions.assertNull(rescheduledStoryEvent)
            }

        }
    }
}