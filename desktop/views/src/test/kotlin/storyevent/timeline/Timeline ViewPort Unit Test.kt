package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.TimelineViewPortComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.makeStoryPointLabel
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.item.StoryEventItemViewModel
import com.soyle.stories.storyevent.timeline.Pixels
import com.soyle.stories.storyevent.timeline.Scale
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPort
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPortComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import javafx.collections.ObservableList
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import tornadofx.observableListOf

class `Timeline ViewPort Unit Test` : TimelineViewPortComponent by TimelineViewPortComponentDouble(),
    StoryPointLabelComponent by StoryPointLabelComponentDouble() {

    init {
        FxToolkit.registerPrimaryStage()
    }

    private val viewPort = TimelineViewPort()
    private val presenter = object : TimelineViewPort.Presenter(viewPort) {
        fun offsetX() = offsetXProperty
        fun scale() = scaleProperty
    }

    @Nested
    inner class `Offset X` {

        @Test
        fun `can be updated by presenter`() {
            presenter.offsetX().set(Pixels(15.0))
            assertThat(viewPort.offsetX).isEqualTo(Pixels(15.0))
        }

        @Test
        fun `should never drop below zero`() {
            presenter.offsetX().set(Pixels(-15.0))
            assertThat(viewPort.offsetX).isEqualTo(Pixels(0.0))
        }

    }

    @Nested
    inner class `Max Offset X` {

        private val labels = listOf(
            makeStoryPointLabel(time = UnitOfTime(7)).apply { cachedWidth = 100.0 },
            makeStoryPointLabel(time = UnitOfTime(8)).apply { cachedWidth = 100.0 },
            // expected largest right bounds
            makeStoryPointLabel(time = UnitOfTime(6)).apply { cachedWidth = 170.0 },

            makeStoryPointLabel(time = UnitOfTime(5)).apply { cachedWidth = 100.0 }
        )

        init {
            presenter.scale().set(Scale.at(30.0).getOrThrow())
            viewPort.storyEventItems.setAll(labels)
        }

        @Test
        fun `should be equal to the largest right bounds`() {
            assertThat(viewPort.maxOffsetX().get()).isEqualTo(350.0) // 6*30 + 170
        }

        @Test
        fun `when the scale changes, should update the max offset x`() {
            presenter.scale().set(Scale.at(20.0).getOrThrow())

            assertThat(viewPort.maxOffsetX().get()).isEqualTo(290.0) // 6*20 + 170
        }

        @Test
        fun `when a time value is updated, should update the max offset x`() {
            labels[1].time = 9

            assertThat(viewPort.maxOffsetX().get()).isEqualTo(370.0) // 9*30 + 100
        }

        @Test
        fun `when a cached width value is updated, should update the max offset x`() {
            labels[1].cachedWidth = 120.0

            assertThat(viewPort.maxOffsetX().get()).isEqualTo(360.0) // 8*30 + 120
        }

        @Test
        fun `width of the viewport should be subtracted from the max offset`() {
            viewPort.resize(200.0, 100.0)

            assertThat(viewPort.maxOffsetX().get()).isEqualTo(150.0) // 6*30 + 170 - 200
        }

        @Test
        fun `should never be negative`() {
            viewPort.resize(400.0, 100.0)

            assertThat(viewPort.maxOffsetX().get()).isEqualTo(0.0) // 6*30 + 170 - 400 -> 0.0
        }

        @Test
        fun `if offset x is larger than max, should be set to current offset x value`() {
            viewPort.resize(200.0, 100.0)
            presenter.offsetX().set(Pixels(170.0))

            assertThat(viewPort.maxOffsetX().get()).isEqualTo(170.0) // 6*30 + 170 - 200 < 170
        }

        @Test
        fun `if offset x scrolls back less than max, max should be set to original max value`() {
            viewPort.resize(200.0, 100.0)
            presenter.offsetX().set(Pixels(170.0))
            presenter.offsetX().set(Pixels(140.0))

            assertThat(viewPort.maxOffsetX().get()).isEqualTo(150.0) // 6*30 + 170 - 200 > 140
        }

    }

}