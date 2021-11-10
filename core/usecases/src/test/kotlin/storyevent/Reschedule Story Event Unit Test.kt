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
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
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
    private val storyEvent = makeStoryEvent(projectId = projectId, time = 27u)

    // post conditions
    /** outputs a story event rescheduled event, or more, if the time is less than 0 */
    private var rescheduledStoryEvents: List<StoryEventRescheduled>? = null

    /** updates the story event in the repository, or more, if the time is less than 0 */
    private var updatedStoryEvents: List<StoryEvent>
    private val storyEventRepository: StoryEventRepositoryDouble

    init {
        val mutableUpdatedStoryEvents = mutableListOf<StoryEvent>()
        storyEventRepository = StoryEventRepositoryDouble(
            onUpdateStoryEvent = mutableUpdatedStoryEvents::add
        )
        updatedStoryEvents = mutableUpdatedStoryEvents
    }

    // Use Case
    private val useCase: RescheduleStoryEvent = RescheduleStoryEventUseCase(storyEventRepository)
    private fun rescheduleStoryEvent(inputTime: Long = storyEventTime().toLong()) {
        runBlocking {
            useCase.invoke(storyEvent.id, inputTime) {
                rescheduledStoryEvents = it.storyEventsRescheduled
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
            updatedStoryEvents.single().time.toLong().mustEqual(inputTime)
        }

        @Test
        fun `should output story event rescheduled event`() {
            val inputTime = 47L
            rescheduleStoryEvent(inputTime)
            rescheduledStoryEvents!!.single().newTime.toLong().mustEqual(inputTime)
        }

        @Nested
        inner class `When input time is same as current time` {

            @Test
            fun `should not update story event`() {
                rescheduleStoryEvent(storyEvent.time.toLong())
                assertTrue(updatedStoryEvents.isEmpty()) { "Expected updated story event list to be empty" }
            }

            @Test
            fun `should not produce event`() {
                rescheduleStoryEvent(storyEvent.time.toLong())
                assertTrue(rescheduledStoryEvents.orEmpty().isEmpty()) { "Expected output events to be empty" }
            }

        }

        @Nested
        inner class `When input time is less than zero` {

            @Test
            fun `should only update it to zero`() {
                val inputTime = -12L
                rescheduleStoryEvent(inputTime)
                updatedStoryEvents.single().time.toLong().mustEqual(0L)
            }

            @Nested
            inner class `When other story events exist` {

                private val otherStoryEventsById =
                    List(5) { makeStoryEvent(projectId = projectId) }
                        .onEach(storyEventRepository::givenStoryEvent)
                        .associateBy { it.id }

                @Test
                fun `should normalize other story events`() {
                    val inputTime = -12L
                    rescheduleStoryEvent(inputTime)

                    updatedStoryEvents.size.mustEqual(6)
                    assertEquals(
                        otherStoryEventsById.keys + storyEvent.id,
                        updatedStoryEvents.map { it.id }.toSet()
                    )
                    updatedStoryEvents.filterNot { it.id == storyEvent.id }.forEach {
                        val originalStoryEvent = otherStoryEventsById.getValue(it.id)
                        it.time.mustEqual(originalStoryEvent.time + 12u)
                    }
                }

                @Test
                fun `should output normalized rescheduled events`() {
                    val inputTime = -12L
                    rescheduleStoryEvent(inputTime)

                    rescheduledStoryEvents!!.size.mustEqual(6)
                }

            }

        }
    }
}