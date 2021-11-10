package com.soyle.stories.usecase.storyevent

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.domain.storyevent.storyEventName
import com.soyle.stories.domain.storyevent.storyEventTime
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.usecase.repositories.StoryEventRepositoryDouble
import com.soyle.stories.usecase.storyevent.create.CreateStoryEvent
import com.soyle.stories.usecase.storyevent.create.CreateStoryEventUseCase
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class `Create Story Event Unit Test` {

    // Summary
    /** A new story event is created in the project */

    // Preconditions
    /** A project has been created and opened */
    private val projectId = Project.Id()

    // post conditions
    /** outputs a story event created event */
    private var createdStoryEvent: StoryEventCreated? = null

    /** may output story event rescheduled events, if time is less than zero */
    private var rescheduledStoryEvents: List<StoryEventRescheduled>? = null

    /** stores a new story event in the repository */
    private var storyEvent: StoryEvent? = null

    /** may update story events, if time is less than zero */
    private val updatedStoryEvents = mutableListOf<StoryEvent>()
    private val storyEventRepository = StoryEventRepositoryDouble(
        onAddNewStoryEvent = ::storyEvent::set,
        onUpdateStoryEvent = updatedStoryEvents::add
    )

    // Use Case
    private val useCase: CreateStoryEvent = CreateStoryEventUseCase(storyEventRepository)
    private fun createStoryEvent(request: CreateStoryEvent.RequestModel) {
        runBlocking {
            useCase.invoke(request) {
                createdStoryEvent = it.createdStoryEvent
                rescheduledStoryEvents = it.rescheduledStoryEvents
            }
        }
    }

    @Nested
    inner class `When No Time Value is Provided` {

        private val request = CreateStoryEvent.RequestModel(
            storyEventName(),
            projectId
        )

        init {
            createStoryEvent(request)
        }

        @Test
        fun `should have stored a new story event`() {
            assertNotNull(storyEvent) { "Story event was not added to repository" }
            storyEvent!!.name.mustEqual(request.name)
            storyEvent!!.projectId.mustEqual(projectId)
            storyEvent!!.time.toLong().mustEqual(1L)
        }

        @Test
        fun `should output story event created event`() {
            createdStoryEvent!!.name.mustEqual(request.name.value)
            createdStoryEvent!!.time.toLong().mustEqual(1L)
            createdStoryEvent!!.storyEventId.mustEqual(storyEvent!!.id)
        }

    }

    @Nested
    inner class `When Time Value is Provided` {

        private val requestedTime = 47L
        private val request = CreateStoryEvent.RequestModel(
            storyEventName(),
            projectId,
            requestedTime
        )

        @Test
        fun `should create story event at requested time`() {
            createStoryEvent(request)

            storyEvent!!.time.toLong().mustEqual(requestedTime)
            createdStoryEvent!!.time.toLong().mustEqual(requestedTime)
        }

        @Nested
        inner class `When Provided Time Value is Less than Zero` {

            private val requestedTime = -47L
            private val request = CreateStoryEvent.RequestModel(
                storyEventName(),
                projectId,
                requestedTime
            )

            @Test
            fun `should create story event at time zero`() {
                createStoryEvent(request)

                storyEvent!!.time.toLong().mustEqual(0L)
                createdStoryEvent!!.time.toLong().mustEqual(0L)
            }

        }

    }

    @Nested
    inner class `Given Multiple Story Events Already Exist` {

        private val preExistingStoryEvents = List(6) { makeStoryEvent(projectId = projectId) }

        init {
            preExistingStoryEvents.forEach(storyEventRepository::givenStoryEvent)
        }

        private val request = CreateStoryEvent.RequestModel(
            storyEventName(),
            projectId
        )

        @Test
        fun `should create new story event at the end of the story`() {
            createStoryEvent(request)

            storyEvent!!.time.toLong().mustEqual(preExistingStoryEvents.maxOf { it.time.toLong() } + 1)
            createdStoryEvent!!.time.mustEqual(storyEvent!!.time)
        }

        @Nested
        inner class `When Provided Time Value is Less than Zero` {

            private val requestedTime = -47L
            private val request = CreateStoryEvent.RequestModel(
                storyEventName(),
                projectId,
                requestedTime
            )

            @Test
            fun `should update normalized story events`() {
                createStoryEvent(request)

                updatedStoryEvents.size.mustEqual(6)
                rescheduledStoryEvents!!.size.mustEqual(6)
            }

        }

    }

    @Nested
    inner class `When Inserted Relative to Other Story Event` {

        private val relativeStoryEvent = makeStoryEvent(projectId = projectId, time = 8u)

        @Test
        fun `should throw error if relative story event does not exist`() {
            val request = CreateStoryEvent.RequestModel(storyEventName(), projectId, relativeStoryEvent.id, -1)

            val error = assertThrows<StoryEventDoesNotExist> {
                createStoryEvent(request)
            }
            error.storyEventId.mustEqual(relativeStoryEvent.id.uuid)
        }

        @Nested
        inner class `Given Other Story Event Exists` {

            init {
                storyEventRepository.givenStoryEvent(relativeStoryEvent)
            }

            @Nested
            inner class `When Inserted Before` {

                @Test
                fun `should be inserted at time one less than relative event`() {
                    val request = CreateStoryEvent.RequestModel(storyEventName(), projectId, relativeStoryEvent.id, -1)
                    createStoryEvent(request)

                    storyEvent!!.time.mustEqual(relativeStoryEvent.time - 1u)
                    createdStoryEvent!!.time.mustEqual(storyEvent!!.time)
                }

                @Test
                fun `when relative event is at zero - should normalize timeline`() {
                    val relativeStoryEvent = makeStoryEvent(projectId = projectId, time = 0u)
                        .also(storyEventRepository::givenStoryEvent)

                    val request = CreateStoryEvent.RequestModel(storyEventName(), projectId, relativeStoryEvent.id, -1)
                    createStoryEvent(request)

                    storyEvent!!.time.toLong().mustEqual(0L)
                    updatedStoryEvents.size.mustEqual(2)
                }

            }

            @Test
            fun `when inserted after`() {
                val request = CreateStoryEvent.RequestModel(storyEventName(), projectId, relativeStoryEvent.id, +1)
                createStoryEvent(request)

                storyEvent!!.time.mustEqual(relativeStoryEvent.time + 1u)
                createdStoryEvent!!.time.mustEqual(storyEvent!!.time)
            }

            @Test
            fun `when inserted at the same moment`() {
                val request = CreateStoryEvent.RequestModel(storyEventName(), projectId, relativeStoryEvent.id, 0)
                createStoryEvent(request)

                storyEvent!!.time.mustEqual(relativeStoryEvent.time)
                createdStoryEvent!!.time.mustEqual(storyEvent!!.time)
            }

        }

    }
}