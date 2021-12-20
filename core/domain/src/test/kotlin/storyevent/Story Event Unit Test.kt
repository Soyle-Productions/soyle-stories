package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.nonBlankStr
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.scene.Scene
import com.soyle.stories.domain.storyevent.events.*
import com.soyle.stories.domain.storyevent.exceptions.StoryEventAlreadyCoveredByScene
import com.soyle.stories.domain.storyevent.exceptions.storyEventAlreadyWithoutCoverage
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.UUID.randomUUID

class `Story Event Unit Test` {

    @Nested
    inner class `Create Story Event` {

        val inputName = nonBlankStr("New Story Event Name ${randomUUID()}")
        val inputTime = ULongRange(0u, 10u).random()
        val projectId = Project.Id()

        @Test
        fun `should create story event with provided name and time`() {
            val update: StoryEventUpdate<StoryEventCreated> = StoryEvent.create(inputName, inputTime, projectId)

            update.storyEvent.name.mustEqual(inputName)
            update.storyEvent.time.mustEqual(inputTime)
            update.storyEvent.projectId.mustEqual(projectId)
            update.storyEvent.sceneId.mustEqual(null)

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

        @Test
        fun `should not be covered by a scene`() {
            val update: StoryEventUpdate<StoryEventCreated> = StoryEvent.create(inputName, inputTime, projectId)
            update as Successful

            update.storyEvent.sceneId.mustEqual(null)
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

    @Nested
    inner class `Reschedule Story Event` {

        private val storyEvent = makeStoryEvent(time = 7u)
        private val inputTime: ULong = 18u

        @Test
        fun `should produce story event with new time`() {
            val (updatedStoryEvent: StoryEvent) = storyEvent.withTime(inputTime)

            updatedStoryEvent.id.mustEqual(storyEvent.id)
            updatedStoryEvent.time.mustEqual(inputTime)
        }

        @Test
        fun `should produce story event rescheduled event`() {
            val update: StoryEventUpdate<StoryEventRescheduled> = storyEvent.withTime(inputTime)
            update as Successful

            update.change.mustEqual(StoryEventRescheduled(
                storyEventId = storyEvent.id,
                newTime = inputTime,
                originalTime = storyEvent.time
            ))
        }

        @Nested
        inner class `When time is identical` {

            @Test
            fun `should not produce event`() {
                val update: StoryEventUpdate<StoryEventRescheduled> = storyEvent.withTime(storyEvent.time)
                update as UnSuccessful

                update.storyEvent.time.mustEqual(storyEvent.time)
            }

        }

    }

    @Nested
    inner class `Cover Story Event in Scene` {

        val storyEvent = makeStoryEvent()
        val inputSceneId = Scene.Id()

        @Test
        fun `should set provided scene id as coverage scene`() {
            val update: StoryEventUpdate<*> = storyEvent.coveredByScene(inputSceneId)

            update.storyEvent.sceneId.mustEqual(inputSceneId)
        }

        @Test
        fun `should output covered in scene event`() {
            val update: StoryEventUpdate<StoryEventCoveredByScene> = storyEvent.coveredByScene(inputSceneId)

            update as Successful
            update.change.storyEventId.mustEqual(storyEvent.id)
            update.change.sceneId.mustEqual(inputSceneId)
        }

        @Nested
        inner class `When Scene Id is Already Used` {

            private val coveredStoryEvent = storyEvent.coveredByScene(inputSceneId).storyEvent

            @Test
            fun `should not produce update`() {
                val update = coveredStoryEvent.coveredByScene(inputSceneId)

                update as UnSuccessful
                update.storyEvent.mustEqual(coveredStoryEvent)
                update.reason.mustEqual(StoryEventAlreadyCoveredByScene(storyEvent.id, inputSceneId))
            }

        }

        @Nested
        inner class `Given Another Scene Already Covers the Story Event` {

            private val otherSceneId = Scene.Id()
            private val coveredStoryEvent = storyEvent.coveredByScene(otherSceneId).storyEvent

            @Test
            fun `should have previous id in change event`() {
                val update = coveredStoryEvent.coveredByScene(inputSceneId)

                update as Successful
                update.change.uncovered.mustEqual(StoryEventUncoveredFromScene(storyEvent.id, otherSceneId))
            }

        }

    }

    @Nested
    inner class `Uncover Story Event from Scene` {

        private val storyEvent = makeStoryEvent()

        @Test
        fun `should produce no update when story event is not covered`() {
            val update: StoryEventUpdate<*> = storyEvent.withoutCoverage()

            update as UnSuccessful
            update.reason.mustEqual(storyEventAlreadyWithoutCoverage(storyEvent.id))
        }

        @Nested
        inner class `When Story Event is Covered` {

            private val coveredSceneId = Scene.Id()
            private val coveredStoryEvent = storyEvent.coveredByScene(coveredSceneId).storyEvent

            @Test
            fun `should clear scene id`() {
                val update = coveredStoryEvent.withoutCoverage()

                update as Successful
                update.storyEvent.sceneId.mustEqual(null)
            }

            @Test
            fun `should produce event with previous scene id`() {
                val update: StoryEventUpdate<StoryEventUncoveredFromScene> = coveredStoryEvent.withoutCoverage()

                update as Successful
                update.change.storyEventId.mustEqual(storyEvent.id)
                update.change.previousSceneId.mustEqual(coveredSceneId)
            }

        }

    }

}