package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelComponentDouble
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.item.StoryEventItemSelection
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import javafx.scene.layout.Pane
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import tornadofx.add
import tornadofx.onChange

class `Story Point Label Unit Test` : StoryPointLabelComponent by StoryPointLabelComponentDouble() {
    companion object {
        init {
            FxToolkit.registerPrimaryStage()
        }
    }

    @Nested
    inner class `When Created` {

        private val inputTime = UnitOfTime(8L)
        private val inputName = "Frank slips on a banana"

        private val label = StoryPointLabel(StoryEvent.Id(), inputName, inputTime)

        @Test
        fun `should have provided time`() {
            assertThat(label.time).isEqualTo(inputTime.value)
        }

        @Test
        fun `should have provided text`() {
            assertThat(label.text).isEqualTo(inputName)
        }

        @Test
        fun `no width should be cached`() {
            assertThat(label.cachedWidth).isEqualTo(StoryPointLabel.INVALID_CACHE)
        }

        @Test
        fun `should assume to be at top row`() {
            assertThat(label.row).isEqualTo(0)
        }

        @Test
        fun `time range should start at provided time`() {
            assertThat(label.timeRange.start).isEqualTo(inputTime)
        }

        @Test
        fun `covered duration should be 1`() {
            assertThat(label.coveredDuration).isEqualTo(UnitOfTime(1))
        }

        @Test
        fun `time range should be one unit long`() {
            assertThat(label.timeRange.endInclusive).isEqualTo(inputTime+1)
        }

        @Test
        fun `should not be collapsed`() {
            assertThat(label.isCollapsed).isFalse
        }

        @Test
        fun `should be disabled`() {
            assertThat(label).isDisabled
        }

    }

    @Nested
    inner class `When cache is updated` {

        val label = StoryPointLabel(StoryEvent.Id(), "", UnitOfTime(8))

        @Test
        fun `should detect changes to cached width`() {
            var changeDetected = false
            label.cachedWidth().onChange {
                changeDetected = true
            }
            label.cachedWidth = 42.0
            assertTrue(changeDetected)
        }

        @Test
        fun `should be enabled when valid`() {
            label.cachedWidth = 42.0
            assertThat(label).isEnabled
        }

        @Test
        fun `should disable when invalidated`() {
            label.cachedWidth = 42.0
            label.cachedWidth = StoryPointLabel.INVALID_CACHE
            assertThat(label).isDisabled
        }

    }

    @Nested
    inner class `When time is updated` {

        val label = StoryPointLabel(StoryEvent.Id(), "", UnitOfTime(8))

        @Test
        fun `should detect changes to time`() {
            var changeDetected = false
            label.time().onChange {
                changeDetected = true
            }
            label.time = 12
            assertTrue(changeDetected)
        }

        @Test
        fun `time range should be updated`() {
            label.time = 12
            assertThat(label.timeRange.start).isEqualTo(UnitOfTime(12))
            assertThat(label.timeRange.endInclusive).isEqualTo(UnitOfTime(13))
        }

        @Test
        fun `should detect time range update`() {
            var changeDetected = false
            label.timeRange().onChange {
                changeDetected = true
            }
            label.time = 12
            assertTrue(changeDetected)
        }

        @Nested
        inner class `Given Covered Duration has been Updated` {

            init {
                label.coveredDuration = UnitOfTime(6)
            }

            @Test
            fun `should update time range to cover duration from new time`() {
                label.time = 12
                assertThat(label.timeRange).isEqualTo(UnitOfTime(12)..UnitOfTime(18))
            }

        }

    }

    @Nested
    inner class `When text is updated` {

        val label = StoryPointLabel(StoryEvent.Id(), "Original Name", UnitOfTime(8)).apply {
            cachedWidth = 100.0
        }

        @Test
        fun `should invalidate cached width`() {
            label.text = "New name"
            assertThat(label.cachedWidth).isEqualTo(StoryPointLabel.INVALID_CACHE)
        }
    }

    @Nested
    inner class `When covered duration is updated` {

        @Test
        fun `time range should be updated`() {
            val label = StoryPointLabel(StoryEvent.Id(), "", UnitOfTime(8))
            var changeDetected = false
            label.timeRange().onChange {
                changeDetected = true
            }
            label.coveredDuration = UnitOfTime(6)
            assertTrue(changeDetected)
            assertThat(label.timeRange).isEqualTo(UnitOfTime(8)..UnitOfTime(14))
        }

    }

    @Nested
    inner class `When row updated` {

        val label = StoryPointLabel(StoryEvent.Id(), "", UnitOfTime(8))

        @Test
        fun `should detect change`() {
            var changeDetected = false
            label.row().onChange {
                changeDetected = true
            }
            label.row = 8
            assertTrue(changeDetected)
        }

    }

    @Nested
    inner class `Rule - cached width should only update when invalid or less than new width` {

        @Test
        fun `when invalid - should update`() {
            val label = StoryPointLabel(StoryEvent.Id(), "", UnitOfTime(8))
            label.resize(48.0, 9.0)
            assertThat(label.cachedWidth).isEqualTo(48.0)
        }

        @Test
        fun `when larger than new width - should not update`() {
            val label = StoryPointLabel(StoryEvent.Id(), "", UnitOfTime(8))
            label.cachedWidth = 100.0
            label.resize(48.0, 9.0)
            assertThat(label.cachedWidth).isEqualTo(100.0)
        }

    }

    @Test
    fun `should be notified when collapsed is updated`() {
        val label = StoryPointLabel(StoryEvent.Id(), "", UnitOfTime(8))
        var wasNotified = false
        label.collapsed().onChange { wasNotified = true }
        label.isCollapsed = true
        assertThat(wasNotified).isTrue
    }

    @Test
    fun `when selection is updated, should be selected`() {
        val label = StoryPointLabel(StoryEvent.Id(), "", UnitOfTime(8))
        val selection = StoryEventItemSelection()
        selection.add(label)

        label.selection().set(selection)

        assertThat(label.selected).isTrue
    }

    @Test
    fun `when added to selection, should be selected`() {
        val label = StoryPointLabel(StoryEvent.Id(), "", UnitOfTime(8))

        label.selection().get().add(label)

        assertThat(label.selected).isTrue
    }

}