package com.soyle.stories.domain.scene

import com.soyle.stories.domain.mustEqual
import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.prose.Prose
import com.soyle.stories.domain.scene.events.StoryEventAddedToScene
import com.soyle.stories.domain.scene.events.StoryEventRemovedFromScene
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.domain.validation.NonBlankString
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class `Covered Story Events Unit Test` {

    @Test
    fun `Create Scene with Covered Story Event`() {
        val storyEventId = StoryEvent.Id()

        val update = Scene.create(Project.Id(), NonBlankString.create("Something")!!, storyEventId, Prose.Id())

        update as Updated
        update.scene.coveredStoryEvents.single().mustEqual(storyEventId)
        update.event.storyEventId.mustEqual(storyEventId)
    }

    @Nested
    inner class `Can Add Story Event to Scene` {

        private val scene = makeScene(coveredStoryEvents = emptySet())
        private val storyEventId = StoryEvent.Id()
        private val secondStoryEventId = StoryEvent.Id()

        @Test
        fun `Add New Story Event`() {
            val update: SceneUpdate<StoryEventAddedToScene> = scene.withStoryEvent(storyEventId)

            update as Updated

            update.scene.coveredStoryEvents.single().mustEqual(storyEventId)

            update.event.mustEqual(StoryEventAddedToScene(scene.id, storyEventId))
        }

        @Test
        fun `Add Second Story Event`() {
            val update: SceneUpdate<StoryEventAddedToScene> = scene.withStoryEvent(storyEventId)
                .scene.withStoryEvent(secondStoryEventId)

            update as Updated

            update.scene.coveredStoryEvents.mustEqual(setOf(storyEventId, secondStoryEventId))

            update.event.mustEqual(StoryEventAddedToScene(scene.id, secondStoryEventId))
        }

        @Test
        fun `Add Story Event Again`() {
            val update = scene.withStoryEvent(storyEventId).scene.withStoryEvent(storyEventId)

            update as WithoutChange
            update.scene.coveredStoryEvents.mustEqual(setOf(storyEventId))

            update.reason.mustEqual(SceneAlreadyCoversStoryEvent(scene.id, storyEventId))
        }

    }

    @Nested
    inner class `Can Remove Story Event from Scene` {

        private val storyEventId = StoryEvent.Id()
        private val scene = makeScene(coveredStoryEvents = emptySet())

        @Test
        fun `Remove Story Event Not in Scene`() {
            val update = scene.withoutStoryEvent(storyEventId)

            update as WithoutChange
            update.scene.coveredStoryEvents.isEmpty().mustEqual(true)
            update.reason.mustEqual(SceneDoesNotCoverStoryEvent(scene.id, storyEventId))
        }

        @Test
        fun `Remove Story Event from Scene`() {
            val scene = scene.withStoryEvent(storyEventId).scene

            val update: SceneUpdate<StoryEventRemovedFromScene> = scene.withoutStoryEvent(storyEventId)

            update as Updated
            update.scene.coveredStoryEvents.isEmpty().mustEqual(true)
            update.event.mustEqual(StoryEventRemovedFromScene(scene.id, storyEventId))
        }

        @Test
        fun `Remove Story Event from Scene with Many Covered Story Events`() {
            val scene = scene.withStoryEvent(storyEventId).scene
                .withStoryEvent(StoryEvent.Id()).scene
                .withStoryEvent(StoryEvent.Id()).scene

            val update: SceneUpdate<StoryEventRemovedFromScene> = scene.withoutStoryEvent(storyEventId)

            update as Updated
            update.scene.coveredStoryEvents.size.mustEqual(2)
            update.scene.coveredStoryEvents.contains(storyEventId).mustEqual(false)
            update.event.mustEqual(StoryEventRemovedFromScene(scene.id, storyEventId))
        }


    }

}