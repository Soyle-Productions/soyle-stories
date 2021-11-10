package com.soyle.stories.domain.storyevent

import com.soyle.stories.domain.project.Project
import com.soyle.stories.domain.storyevent.events.StoryEventRescheduled
import com.soyle.stories.domain.validation.EntitySet
import com.soyle.stories.domain.validation.entitySetOf
import javax.swing.text.html.parser.Entity
import kotlin.math.abs

class StoryEventTimeService(
    private val storyEventRepository: StoryEventRepository
) {

    suspend fun rescheduleStoryEvent(
        storyEvent: StoryEvent,
        newTime: Long
    ): List<StoryEventUpdate<StoryEventRescheduled>> {
        return if (newTime < 0) normalizeAllStoryEventsAboveZero(storyEvent, newTime)
        else listOf(storyEvent.withTime(newTime.coerceAtLeast(0).toULong()))
    }

    suspend fun adjustStoryEventTimesBy(
        storyEvents: EntitySet<StoryEvent>,
        amount: Long
    ): List<StoryEventUpdate<StoryEventRescheduled>> {
        if (storyEvents.isEmpty()) return emptyList()
        val projectId = storyEvents.assertPartOfSameProject()

        if (storyEvents.any { it.time.toLong() + amount < 0 }) {
            return normalizeAllStoryEventsAboveZero(projectId, storyEvents, amount)
        } else {
            return storyEvents.map { it.withTime(it.time + amount.toULong()) }
        }
    }

    private suspend fun normalizeAllStoryEventsAboveZero(
        storyEvent: StoryEvent,
        negativeTime: Long
    ): List<StoryEventUpdate<StoryEventRescheduled>> {
        return normalizeAllStoryEventsAboveZero(
            storyEvent.projectId,
            entitySetOf(storyEvent),
            negativeTime - storyEvent.time.toLong()
        )
    }

    private suspend fun normalizeAllStoryEventsAboveZero(
        projectId: Project.Id,
        storyEvents: EntitySet<StoryEvent>,
        adjustment: Long
    ): List<StoryEventUpdate<StoryEventRescheduled>> {
        val normalizedAdjustment = abs(storyEvents.minOf { it.time.toLong() + adjustment })
        return storyEventRepository.listStoryEventsInProject(projectId)
            .map {
                if (storyEvents.containsEntityWithId(it.id)) {
                    it.withTime((it.time.toLong() + adjustment + normalizedAdjustment).toULong())
                } else {
                    it.withTime(it.time + normalizedAdjustment.toULong())
                }
            }
    }

    private fun Collection<StoryEvent>.assertPartOfSameProject(): Project.Id {
        val projectId = first().projectId
        assert(all { it.projectId == projectId })
        return projectId
    }

}