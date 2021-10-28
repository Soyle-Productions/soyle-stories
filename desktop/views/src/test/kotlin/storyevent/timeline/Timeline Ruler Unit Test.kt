package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.TimelineViewPortRulerComponentDouble
import com.soyle.stories.storyevent.timeline.Scale
import com.soyle.stories.storyevent.timeline.TimeRange
import com.soyle.stories.storyevent.timeline.TimelineStyles
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRuler
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabel
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat

class `Timeline Ruler Unit Test` {

    val ruler = TimelineRuler(gui = TimelineViewPortRulerComponentDouble().gui)

    @Nested
    inner class `Should create enough labels to cover entire visible range` {

        @Test
        fun `no labels when width is zero`() {
            assertThat(ruler.labels().value).isEmpty()
        }

        @Test
        fun `at least one label should render when width is less than label step`() {
            ruler.resize(1.0, 1.0)
            ruler.visibleRange = TimeRange(0L .. 0L)
            assertThat(ruler.labels().value).hasSize(1)
        }

        @Test
        fun `should create as many labels as are in visible range`() {
            ruler.resize(1.0, 1.0)
            ruler.visibleRange = TimeRange(0L .. 5L)
            assertThat(ruler.labels().value).hasSize(6)
        }

    }

    @Nested
    inner class `Should maintain readability at lower scales` {

        init {
            // make sure labels will actually render
            ruler.resize(1.0, 1.0)
            // set a large time range that won't equally divide into any of the expected increments
            ruler.visibleRange = TimeRange(0L .. 132L)
        }

        @Test
        fun `once scaled passed 48px, should increment labels by five`() {
            ruler.scale = Scale.at(47.0).getOrThrow()

            ruler.labels().value.forEach { assertThat(it.number % 5).isEqualTo(0) }
            assertThat(ruler.labels().value).hasSize(27)
            ruler.labels().value.forEach { assertThat(it.minWidthProperty().get()).isEqualTo(227.0) }
        }

        @Test
        fun `once scaled less than 9point6 px, should increment labels by ten`() {
            ruler.scale = Scale.at(9.5).getOrThrow()

            ruler.labels().value.forEach { assertThat(it.number % 10).isEqualTo(0) }
            assertThat(ruler.labels().value).hasSize(14)
            ruler.labels().value.forEach { assertThat(it.minWidthProperty().get()).isEqualTo(87.0) }
        }

        @Test
        fun `once scaled less than 4point8 px, should increment labels by twenty`() {
            ruler.scale = Scale.at(4.7).getOrThrow()

            ruler.labels().value.forEach { assertThat(it.number % 20).isEqualTo(0) }
            assertThat(ruler.labels().value).hasSize(7)
            ruler.labels().value.forEach { assertThat(it.minWidthProperty().get()).isEqualTo(86.0) }
        }

        @Test
        fun `once scaled less than 1point92 px, should increment labels by fifty`() {
            ruler.scale = Scale.at(1.91).getOrThrow()

            ruler.labels().value.forEach { assertThat(it.number % 50).isEqualTo(0) }
            assertThat(ruler.labels().value).hasSize(3)
            ruler.labels().value.forEach { assertThat(it.minWidthProperty().get()).isEqualTo(87.5) }
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
            ruler.visibleRange = TimeRange(500L .. 980L)

            assertThat(ruler.secondaryLabels().value).isEmpty()
        }

        @Test
        fun `over 1000, should show one secondary label`() {
            ruler.visibleRange = TimeRange(1200L .. 1480L)

            ruler.secondaryLabels().value.single().apply {
                assertThat(text).isEqualTo("1,000")
            }
        }

        @Test
        fun `well over 1000, should show secondary label`() {
            ruler.visibleRange = TimeRange(14_987_365 .. 14_987_865L)

            ruler.secondaryLabels().value.single().apply {
                assertThat(text).isEqualTo("14,987,000")
            }
        }

        @Test
        fun `between two 1000s, should show two secondary labels`() {
            ruler.visibleRange = TimeRange(14_987_998 .. 14_988_498L)

            assertThat(ruler.secondaryLabels().value).hasSize(2)
            assertThat(ruler.secondaryLabels().value.component1().text).isEqualTo("14,987,000")
            assertThat(ruler.secondaryLabels().value.component2().text).isEqualTo("14,988,000")
        }

        @Test
        fun `right on cusp before 1000, should show just one secondary label`() {
            ruler.visibleRange = TimeRange(998 .. 1_498L)

            assertThat(ruler.secondaryLabels().value).hasSize(1)
            ruler.secondaryLabels().value.single().apply {
                assertThat(text).isEqualTo("1,000")
            }
        }

    }

    companion object {
        init {
            FxToolkit.registerPrimaryStage()
        }
    }

}