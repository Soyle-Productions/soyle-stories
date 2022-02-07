package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.character.Character
import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.events.StoryEventCreated
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.domain.validation.entitySetOf
import com.soyle.stories.domain.validation.toEntitySet
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource

class `Story Event Time Service Unit Test` {

    private val repoMap = mutableMapOf<StoryEvent.Id, StoryEvent>()
    private val repository = object : StoryEventRepository {
        override suspend fun getStoryEventWithCharacterNotNamed(characterId: Character.Id, name: String): StoryEvent? {
            TODO("Not yet implemented")
        }

        override suspend fun trySave(storyEvent: StoryEvent): Boolean {
            TODO("Not yet implemented")
        }
        override suspend fun listStoryEventsInProject(projectId: Project.Id): List<StoryEvent> {
            return repoMap.values.filter { it.projectId == projectId }
        }
    }
    private val service = StoryEventTimeService(repository)

    @Nested
    inner class `Create Story Event` {

        private val name = storyEventName()
        private val projectId = Project.Id()
        private val otherStoryEvents = List(5) { makeStoryEvent(projectId = projectId, time = it.toULong()) }
            .onEach { repoMap[it.id] = it }

        @Nested
        inner class `When Time is Greater Than or Equal to Zero` {

            @ParameterizedTest
            @ValueSource(longs = [0, 8])
            fun `should return single creation update for provided story event`(inputTime: Long): Unit = runBlocking {
                val (creation, reschedules) = service.createStoryEvent(name, inputTime, projectId)

                creation.storyEvent.name.mustEqual(name)
                creation.storyEvent.time.mustEqual(inputTime.toULong())
                creation.storyEvent.projectId.mustEqual(projectId)
            }

            @ParameterizedTest
            @ValueSource(longs = [0, 8])
            fun `should successfully create story event`(inputTime: Long): Unit = runBlocking {
                val (creation, reschedules) = service.createStoryEvent(name, inputTime, projectId)

                with(creation as Successful) {
                    change.time.mustEqual(inputTime.toULong())
                }
            }

        }

        @Nested
        inner class `When New Time is Less Than Zero` {

            @Test
            fun `should only create story event with time at zero`(): Unit = runBlocking {
                val (creation, reschedules) = service.createStoryEvent(name, -9, projectId)

                creation.storyEvent.time.mustEqual(0L.toULong())
                (creation as Successful).change.time.mustEqual(0L.toULong())
            }

            @Nested
            inner class `Given Other Story Events in Project` {

                @Test
                fun `should normalize all other story events above zero`(): Unit = runBlocking {
                    val (creation, reschedules) = service.createStoryEvent(name, -9, projectId)

                    reschedules.size.mustEqual(5)
                    reschedules
                        .map { it as Successful }
                        .forEach { (newStoryEvent, change) ->
                            val originalStoryEvent = repoMap.getValue(newStoryEvent.id)
                            newStoryEvent.time.mustEqual(originalStoryEvent.time + 9u)
                            change.newTime.mustEqual(originalStoryEvent.time + 9u)
                        }
                }

            }

        }

    }

    @Nested
    inner class `Reschedule Story Event` {

        private val storyEvent = makeStoryEvent(time = 10u).also { repoMap[it.id] = it }
        private val otherStoryEvents = List(5) { makeStoryEvent(projectId = storyEvent.projectId, time = it.toULong()) }
            .onEach { repoMap[it.id] = it }

        @Nested
        inner class `When New Time is Greater Than or Equal to Zero` {

            @ParameterizedTest
            @ValueSource(longs = [0, 8])
            fun `should return single update for provided story event`(inputTime: Long): Unit = runBlocking {
                val updates: List<StoryEventUpdate<StoryEventRescheduled>> =
                    service.rescheduleStoryEvent(storyEvent, inputTime)

                updates.single().storyEvent.id.mustEqual(storyEvent.id)
                updates.single().storyEvent.time.mustEqual(inputTime.toULong())
            }

            @ParameterizedTest
            @ValueSource(longs = [0, 8])
            fun `single update should be successful`(inputTime: Long): Unit = runBlocking {
                val updates: List<StoryEventUpdate<StoryEventRescheduled>> =
                    service.rescheduleStoryEvent(storyEvent, inputTime)

                with(updates.single() as Successful) {
                    change.newTime.mustEqual(inputTime.toULong())
                }
            }

        }

        @Nested
        inner class `When New Time is Less Than Zero` {

            @Test
            fun `should only update story event time to zero`(): Unit = runBlocking {
                val updates = service.rescheduleStoryEvent(storyEvent, -9)

                val update = updates.single { it.storyEvent.id == storyEvent.id }
                update.storyEvent.time.mustEqual(0L.toULong())
                (update as Successful).change.newTime.mustEqual(0L.toULong())
            }

            @Nested
            inner class `Given Other Story Events in Project` {

                @Test
                fun `should normalize all other story events above zero`(): Unit = runBlocking {
                    val updates = service.rescheduleStoryEvent(storyEvent, -9)

                    updates.size.mustEqual(6)
                    updates.filterNot { it.storyEvent.id == storyEvent.id }
                        .map { it as Successful }
                        .forEach { (newStoryEvent, change) ->
                            val originalStoryEvent = repoMap.getValue(newStoryEvent.id)
                            newStoryEvent.time.mustEqual(originalStoryEvent.time + 9u)
                            change.newTime.mustEqual(originalStoryEvent.time + 9u)
                        }
                }

            }

        }

    }

    @Nested
    inner class `Adjust Story Events Times` {

        private val projectId = Project.Id()

        @Test
        fun `empty set - should return empty list`(): Unit = runBlocking {
            val updates = service.adjustStoryEventTimesBy(entitySetOf(), 42)

            assertTrue(updates.isEmpty()) { "expected to be empty: $updates"}
        }

        @Test
        fun `if not all story events are from same project - should throw error`(): Unit = runBlocking {
            val result = runCatching {
                service.adjustStoryEventTimesBy(
                    entitySetOf(
                        makeStoryEvent(),
                        makeStoryEvent(),
                        makeStoryEvent()
                    ),
                    42
                )
            }

            assertNotNull(result.exceptionOrNull()) { "Expected exception to be thrown" }
        }

        @Nested
        inner class `When Adjustment Keeps Story Event Time Above Zero` {

            private val inputStoryEvents = List(3) { makeStoryEvent(projectId = projectId) }

            init {
                repeat(6) {
                    makeStoryEvent(projectId = projectId).also { repoMap[it.id] = it }
                }
            }

            @Test
            fun `should only update provided story events`(): Unit = runBlocking {
                val updates = service.adjustStoryEventTimesBy(inputStoryEvents.toEntitySet(), 42)

                updates.size.mustEqual(3) { "Expected size of:\n$updates\nTo be" }
                assertEquals(
                    inputStoryEvents.map { it.id }.toSet(),
                    updates.map { it.storyEvent.id }.toSet()
                ) { "Expecting exactly the same ids." }
                val assertionFailures = updates.flatMap { update ->
                    val originalStoryEvent = inputStoryEvents.single { it.id == update.storyEvent.id }
                    listOf(
                        runCatching { update.storyEvent.time.mustEqual(originalStoryEvent.time + 42u) },
                        runCatching { (update as Successful).change.originalTime.mustEqual(originalStoryEvent.time) }
                    )
                }.mapNotNull { it.exceptionOrNull() }
                if (assertionFailures.isNotEmpty()) fail<Nothing>(AssertionError(assertionFailures.joinToString("\n")))
            }

        }

        @Nested
        inner class `When Adjust Brings Some Story Events Below Zero` {

            @Test
            fun `should normalize all story events above zero`(): Unit = runBlocking {
                val storyEvents = listOf(
                    makeStoryEvent(projectId = projectId, time = 8u),
                    makeStoryEvent(projectId = projectId, time = 4u),
                    makeStoryEvent(projectId = projectId, time = 6u),
                    makeStoryEvent(projectId = projectId, time = 14u)
                ).onEach { repoMap[it.id] = it }

                val inputSet = entitySetOf(storyEvents[0], storyEvents[2])
                val updates = service.adjustStoryEventTimesBy(inputSet, -7)

                updates.size.mustEqual(4) { "Expected size of:\n$updates\nTo be" }
                val updatesById = updates.associateBy { it.storyEvent.id }
                updatesById.getValue(storyEvents[0].id).storyEvent.time.toLong().mustEqual(2L)
                updatesById.getValue(storyEvents[1].id).storyEvent.time.toLong().mustEqual(5L)
                updatesById.getValue(storyEvents[2].id).storyEvent.time.toLong().mustEqual(0L)
                updatesById.getValue(storyEvents[3].id).storyEvent.time.toLong().mustEqual(15L)
            }

        }

    }

}