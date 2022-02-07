package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.storyevent.timeline.TimelineAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.TimelineViewportAccess.Companion.access
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelAssertions
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelAssertions.Companion.assertThat
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.timeline.Timeline
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.fail
import org.testfx.assertions.api.AbstractParentAssert
import org.testfx.assertions.api.Assertions.assertThat

class TimelineAssertions private constructor(private val access: TimelineAccess) :
    AbstractParentAssert<TimelineAssertions>(access.timeline, TimelineAssertions::class.java) {
    companion object {
        fun Timeline.assertThis(op: TimelineAssertions.() -> Unit) {
            TimelineAssertions(access()).op()
        }

        fun assertThat(timeline: Timeline, op: TimelineAssertions.() -> Unit = {}): TimelineAssertions =
            TimelineAssertions(timeline.access()).apply(op)
    }

    fun hasNotStoryEvents() {
        val viewport = access.viewport ?: return
        assertTrue(viewport.storyPointLabels.isEmpty())
    }

    fun hasStoryPointLabel(storyEventId: StoryEvent.Id) {
        val viewport = access.viewport ?: fail("No story events have yet been loaded in the timeline.")
        assertTrue(
            viewport.storyPointLabels.any { it.storyEventId == storyEventId },
            "Could not find story point label with id $storyEventId"
        )
    }

    fun doesNotHaveStoryPointLabelWithName(name: String) {
        val viewport = access.viewport ?: return
        assertNull(
            viewport.storyPointLabels.find { it.name == name },
            "Expected not to find story point label with name \"$name\""
        )
    }

    fun andStoryPointLabel(storyEventId: StoryEvent.Id, op: StoryPointLabelAssertions.() -> Unit) {
        val viewport = access.viewport ?: fail("No story events have yet been loaded in the timeline.")
        val label = viewport.storyPointLabels.single { it.storyEventId == storyEventId }
        assertThat(label, op)
    }

    fun StoryPointLabelAssertions.isInView() {
        val viewport = access.viewport ?: fail("No story events have yet been loaded in the timeline.")
        assertThat(viewport.access().grid.visibleLabels().value)
            .contains(label)
    }

}