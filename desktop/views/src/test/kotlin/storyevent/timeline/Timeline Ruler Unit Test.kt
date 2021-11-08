package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.TimelineViewPortRulerComponentDouble
import com.soyle.stories.storyevent.timeline.*
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewportContext
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimeRangeSelection
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRuler
import javafx.beans.binding.ObjectExpression
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import tornadofx.objectProperty

class `Timeline Ruler Unit Test` {

    val selection = TimelineSelectionModel()
    val visibleRangeProperty = objectProperty<TimeRange>(TimeRange(0L .. 0L))
    val scaleProperty = objectProperty(Scale.maxZoomIn())
    val ruler = TimelineRuler(
        object : TimelineViewportContext {
            override val selection: TimeRangeSelection = this@`Timeline Ruler Unit Test`.selection
            override fun visibleRange(): ObjectExpression<TimeRange> = visibleRangeProperty
            override fun scale(): ObjectExpression<Scale> = scaleProperty
        },
        gui = TimelineViewPortRulerComponentDouble().gui
    )

    @Nested
    inner class `Should create enough labels to cover entire visible range` {

        @Test
        fun `at least one label should render when width is less than label step`() {
            ruler.resize(1.0, 1.0)
            visibleRangeProperty.set(TimeRange(0L..0L))
            assertThat(ruler.labels().value).hasSize(1)
        }

        @Test
        fun `should create as many labels as are in visible range`() {
            ruler.resize(1.0, 1.0)
            visibleRangeProperty.set(TimeRange(0L..5L))
            assertThat(ruler.labels().value).hasSize(6)
        }

    }

    @Nested
    inner class `Should maintain readability at lower scales` {

        init {
            // make sure labels will actually render
            ruler.resize(1.0, 1.0)
            // set a large time range that won't equally divide into any of the expected increments
            visibleRangeProperty.set(TimeRange(0L..132L))
        }

        @Test
        fun `once scaled passed 48px, should increment labels by five`() {
            scaleProperty.set(Scale.at(47.0).getOrThrow())

            ruler.labels().value.forEach { assertThat(it.range.start.value % 5).isEqualTo(0) }
            ruler.labels().value.forEach { assertThat(it.range.duration.value).isEqualTo(5) }
            assertThat(ruler.labels().value).hasSize(27)
            ruler.labels().value.forEach { assertThat(it.minWidthProperty().get()).isEqualTo(227.0) }
        }

        @Test
        fun `once scaled less than 10px, should increment labels by ten`() {
            scaleProperty.set(Scale.at(9.0).getOrThrow())

            ruler.labels().value.forEach { assertThat(it.range.start.value % 10).isEqualTo(0) }
            ruler.labels().value.forEach { assertThat(it.range.duration.value).isEqualTo(10) }
            assertThat(ruler.labels().value).hasSize(14)
            ruler.labels().value.forEach { assertThat(it.minWidthProperty().get()).isEqualTo(82.0) }
        }

        @Test
        fun `once scaled less than 5px, should increment labels by twenty`() {
            scaleProperty.set(Scale.at(4.0).getOrThrow())

            ruler.labels().value.forEach { assertThat(it.range.start.value % 20).isEqualTo(0) }
            ruler.labels().value.forEach { assertThat(it.range.duration.value).isEqualTo(20) }
            assertThat(ruler.labels().value).hasSize(7)
            ruler.labels().value.forEach { assertThat(it.minWidthProperty().get()).isEqualTo(72.0) }
        }

        @Test
        fun `once scaled less than 2px, should increment labels by fifty`() {
            scaleProperty.set(Scale.at(1.0).getOrThrow())

            ruler.labels().value.forEach { assertThat(it.range.start.value % 50).isEqualTo(0) }
            ruler.labels().value.forEach { assertThat(it.range.duration.value).isEqualTo(50) }
            assertThat(ruler.labels().value).hasSize(3)
            ruler.labels().value.forEach { assertThat(it.prefWidthProperty().get()).isEqualTo(42.0) }
        }
    }

    @Nested
    inner class `Once offset by 1000 time units, should show secondary labels` {

        init {
            // make sure labels will actually render
            ruler.resize(1.0, 1.0)
        }

        @Test
        fun `still under 1000, should not show secondary label`() {
            visibleRangeProperty.set(TimeRange(500L..980L))

            assertThat(ruler.secondaryLabels().value).isEmpty()
        }

        @Test
        fun `over 1000, should show one secondary label`() {
            visibleRangeProperty.set(TimeRange(1200L..1480L))

            ruler.secondaryLabels().value.single().apply {
                assertThat(text).isEqualTo("1,000")
            }
        }

        @Test
        fun `well over 1000, should show secondary label`() {
            visibleRangeProperty.set(TimeRange(14_987_365..14_987_865L))

            ruler.secondaryLabels().value.single().apply {
                assertThat(text).isEqualTo("14,987,000")
            }
        }

        @Test
        fun `between two 1000s, should show two secondary labels`() {
            visibleRangeProperty.set(TimeRange(14_987_998..14_988_498L))

            assertThat(ruler.secondaryLabels().value).hasSize(2)
            assertThat(ruler.secondaryLabels().value.component1().text).isEqualTo("14,987,000")
            assertThat(ruler.secondaryLabels().value.component2().text).isEqualTo("14,988,000")
        }

        @Test
        fun `right on cusp before 1000, should show just one secondary label`() {
            visibleRangeProperty.set(TimeRange(998..1_498L))

            assertThat(ruler.secondaryLabels().value).hasSize(1)
            ruler.secondaryLabels().value.single().apply {
                assertThat(text).isEqualTo("1,000")
            }
        }

    }

    @Nested
    inner class `When time ranges are selected, should show selection` {

        init {
            // make sure selection will actually render
            ruler.resize(1.0, 1.0)
            visibleRangeProperty.set(TimeRange(0..500L))
        }

        @Test
        fun `no selection, should not show any selection`() {
            assertThat(ruler.selectionRegion).isNull()
        }

        @Test
        fun `single unit of time selected, should show one region same size as time label`() {
            selection.restart(UnitOfTime(3))

            val selectionRegion = ruler.selectionRegion!!
            assertThat(selectionRegion.prefWidth).isEqualTo(48.0)
        }

        @Test
        fun `at smaller scales, should be exactly width of single unit of time`() {
            selection.restart(UnitOfTime(3))
            scaleProperty.set(Scale.at(4.0).getOrThrow())

            val selectionRegion = ruler.selectionRegion!!
            assertThat(selectionRegion.prefWidth).isEqualTo(4.0)
        }

        @Test
        fun `longer time range selected, should show one region across the time labels`() {
            selection.restart(UnitOfTime(3) .. UnitOfTime(9))

            val selectionRegion = ruler.selectionRegion!!
            assertThat(selectionRegion.prefWidth).isEqualTo(288.0)
        }

    }

    companion object {
        init {
            FxToolkit.registerPrimaryStage()
        }
    }

}