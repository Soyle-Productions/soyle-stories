package com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label

import com.soyle.stories.desktop.view.storyevent.timeline.TimelineAssertions
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import org.junit.jupiter.api.Assertions.assertEquals
import org.testfx.assertions.api.AbstractParentAssert

class StoryPointLabelAssertions(val label: StoryPointLabel) :
    AbstractParentAssert<StoryPointLabelAssertions>(label, StoryPointLabelAssertions::class.java) {

    companion object {
        fun assertThat(label: StoryPointLabel, op: StoryPointLabelAssertions.() -> Unit = {}) =
            StoryPointLabelAssertions(label).apply(op)

    }

    fun hasName(expectedName: String) {
        assertEquals(expectedName, label.text)
    }
    fun isAtTime(expectedTime: Long) {
        assertEquals(expectedTime, label.time)
    }

}