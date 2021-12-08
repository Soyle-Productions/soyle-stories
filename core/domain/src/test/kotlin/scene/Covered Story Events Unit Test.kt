package com.soyle.stories.domain.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.events.StoryEventAddedToScene
import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.storyevent.makeStoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import com.soyle.stories.domain.scene.SceneUpdate.Successful
import com.soyle.stories.domain.scene.SceneUpdate.UnSuccessful
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Covered Story Events Unit Test` {

    @Test
    fun `Create Scene with Covered Story Event`() {
        val storyEventId = StoryEvent.Id()

        val update = Scene.create(Project.Id(), NonBlankString.create("Something")!!, storyEventId, Prose.Id())

        update as Successful
        update.scene.coveredStoryEvents.single().mustEqual(storyEventId)
        update.event.storyEventId.mustEqual(storyEventId)
    }

    @Nested
    inner class `Can Add Story Event to Scene` {

        private val scene = makeScene(coveredStoryEvents = emptySet())
        private val storyEvent = makeStoryEvent()
        private val secondStoryEvent = makeStoryEvent()

        @Test
        fun `Add New Story Event`() {
            val update = scene.withStoryEvent(storyEvent)

            update as Successful

            update.scene.coveredStoryEvents.single().mustEqual(storyEvent.id)

            update.event.mustEqual(StoryEventAddedToScene(scene.id, storyEvent.id, storyEvent.name.value, emptyList()))
        }

        @Test
        fun `Add Second Story Event`() {
            val update = scene.withStoryEvent(storyEvent)
                .scene.withStoryEvent(secondStoryEvent)

            update as Successful

            update.scene.coveredStoryEvents.mustEqual(setOf(storyEvent.id, secondStoryEvent.id))

            update.event.mustEqual(StoryEventAddedToScene(scene.id, secondStoryEvent.id, secondStoryEvent.name.value, emptyList()))
        }

        @Test
        fun `Add Story Event Again`() {
            val update = scene.withStoryEvent(storyEvent).scene.withStoryEvent(storyEvent)

            update as UnSuccessful
            update.scene.coveredStoryEvents.mustEqual(setOf(storyEvent.id))

            update.reason.mustEqual(SceneAlreadyCoversStoryEvent(scene.id, storyEvent.id))
        }

    }

    @Nested
    inner class `Can Remove Story Event from Scene` {

        private val storyEvent = makeStoryEvent()
        private val scene = makeScene(coveredStoryEvents = emptySet())

        @Test
        fun `Remove Story Event Not in Scene`() {
            val update = scene.withoutStoryEvent(storyEvent.id)

            update as UnSuccessful
            update.scene.coveredStoryEvents.isEmpty().mustEqual(true)
            update.reason.mustEqual(SceneDoesNotCoverStoryEvent(scene.id, storyEvent.id))
        }

        @Test
        fun `Remove Story Event from Scene`() {
            val scene = scene.withStoryEvent(storyEvent).scene

            val update: SceneUpdate<StoryEventRemovedFromScene> = scene.withoutStoryEvent(storyEvent.id)

            update as Successful
            update.scene.coveredStoryEvents.isEmpty().mustEqual(true)
            update.event.mustEqual(StoryEventRemovedFromScene(scene.id, storyEvent.id, listOf()))
        }

        @Test
        fun `Remove Story Event from Scene with Many Covered Story Events`() {
            val scene = scene.withStoryEvent(storyEvent).scene
                .withStoryEvent(makeStoryEvent()).scene
                .withStoryEvent(makeStoryEvent()).scene

            val update: SceneUpdate<StoryEventRemovedFromScene> = scene.withoutStoryEvent(storyEvent.id)

            update as Successful
            update.scene.coveredStoryEvents.size.mustEqual(2)
            update.scene.coveredStoryEvents.contains(storyEvent.id).mustEqual(false)
            update.event.mustEqual(StoryEventRemovedFromScene(scene.id, storyEvent.id, listOf()))
        }


    }

}