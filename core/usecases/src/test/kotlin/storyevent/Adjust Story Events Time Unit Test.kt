package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.Successful
import com.soyle.stories.domain.storyevent.events.StoryEventRenamed
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.domain.storyevent.storyEventTime
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEvent
import com.soyle.stories.usecase.storyevent.rename.RenameStoryEventUseCase
import com.soyle.stories.usecase.storyevent.time.adjust.AdjustStoryEventsTime
import com.soyle.stories.usecase.storyevent.time.adjust.AdjustStoryEventsTimeUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class `Adjust Story Events Time Unit Test` {

    // Summary
    /** The times for one or more story events are increased or decreased by a specified number */

    // Preconditions
    /** A project has been created and opened */
    private val projectId = Project.Id()
    /** The story events exist */
    private val storyEvents = List(6) { makeStoryEvent(projectId = projectId) }

    // post conditions
    /** outputs story event rescheduled events */
    private val rescheduledStoryEvents = mutableListOf<StoryEventRescheduled>()
    /** updates the story events in the repository */
    private val updatedStoryEvents: List<StoryEvent>
    private val storyEventRepository: StoryEventRepositoryDouble
    init {
        val mutableUpdatedStoryEvents = mutableListOf<StoryEvent>()
        storyEventRepository = StoryEventRepositoryDouble(onUpdateStoryEvent = mutableUpdatedStoryEvents::add)
        updatedStoryEvents = mutableUpdatedStoryEvents
    }

    // Use Case
    private val useCase: AdjustStoryEventsTime = AdjustStoryEventsTimeUseCase(storyEventRepository)
    private fun adjustStoryEventTimes(number: Long = storyEventTime().toLong()) {
        runBlocking {
            useCase.invoke(storyEvents.map { it.id }.toSet(), number) {
                rescheduledStoryEvents.addAll(it.rescheduledStoryEvents)
            }
        }
    }

    @Test
    fun `should throw error if any story event doesn't exist`() {
        val nonExistentStoryEvent = storyEvents.random()
        (storyEvents - nonExistentStoryEvent).forEach(storyEventRepository::givenStoryEvent)

        val error = assertThrows<StoryEventDoesNotExist> { adjustStoryEventTimes() }

        error.storyEventId.mustEqual(nonExistentStoryEvent.id.uuid)
    }

    @Nested
    inner class `Given All Story Events Exist` {

        init {
            storyEvents.forEach(storyEventRepository::givenStoryEvent)
        }

        @Test
        fun `should update all story events`() {
            val storyEventsById = storyEvents.associateBy { it.id }
            val inputTime = 12L

            adjustStoryEventTimes(inputTime)

            updatedStoryEvents.size.mustEqual(storyEvents.size)
            updatedStoryEvents.map { it.id }.toSet().mustEqual(storyEventsById.keys)
            updatedStoryEvents.forEach { updatedStoryEvent ->
                val originalStoryEvent = storyEventsById.getValue(updatedStoryEvent.id)
                updatedStoryEvent.time.mustEqual(originalStoryEvent.time + inputTime.toULong())
            }
        }

        @Test
        fun `should output story event rescheduled events`() {
            val storyEventsById = storyEvents.associateBy { it.id }
            val inputTime = 47L

            adjustStoryEventTimes(inputTime)

            rescheduledStoryEvents.size.mustEqual(storyEvents.size)
            rescheduledStoryEvents.map { it.storyEventId }.toSet().mustEqual(storyEventsById.keys)
            rescheduledStoryEvents.forEach { rescheduledStoryEvent ->
                val originalStoryEvent = storyEventsById.getValue(rescheduledStoryEvent.storyEventId)
                rescheduledStoryEvent.newTime.mustEqual(originalStoryEvent.time + inputTime.toULong())
                rescheduledStoryEvent.originalTime.mustEqual(originalStoryEvent.time)
            }
        }

        @Nested
        inner class `When input adjustment is 0` {

            @Test
            fun `should not update story events`() {
                adjustStoryEventTimes(0)
                updatedStoryEvents.size.mustEqual(0)
            }

            @Test
            fun `should not produce event`() {
                adjustStoryEventTimes(0)
                rescheduledStoryEvents.size.mustEqual(0)
            }

        }
    }

}