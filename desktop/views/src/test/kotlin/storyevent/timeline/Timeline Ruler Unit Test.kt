package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.ruler.TimelineViewPortRulerComponentDouble
import com.soyle.stories.storyevent.timeline.*
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRuler
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabel
import javafx.scene.input.ContextMenuEvent
import javafx.scene.input.PickResult
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import tornadofx.contextmenu
import tornadofx.observableSetOf

class `Timeline Ruler Unit Test` {

    val selection = TimelineSelectionModel()
    val ruler = TimelineRuler(selection, listOf(), gui = TimelineViewPortRulerComponentDouble().gui)

    @Nested
    inner class `Should create enough labels to cover entire visible range` {

        @Test
        fun `no labels when width is zero`() {
            assertThat(ruler.labels().value).isEmpty()
        }

        @Test
        fun `at least one label should render when width is less than label step`() {
            ruler.resize(1.0, 1.0)
            ruler.visibleRange = TimeRange(0L..0L)
            assertThat(ruler.labels().value).hasSize(1)
        }

        @Test
        fun `should create as many labels as are in visible range`() {
            ruler.resize(1.0, 1.0)
            ruler.visibleRange = TimeRange(0L..5L)
            assertThat(ruler.labels().value).hasSize(6)
        }

    }

    @Nested
    inner class `Should maintain readability at lower scales` {

        init {
            // make sure labels will actually render
            ruler.resize(1.0, 1.0)
            // set a large time range that won't equally divide into any of the expected increments
            ruler.visibleRange = TimeRange(0L..132L)
        }

        @Test
        fun `once scaled passed 48px, should increment labels by five`() {
            ruler.scale = Scale.at(47.0).getOrThrow()

            ruler.labels().value.forEach { assertThat(it.range.start.value % 5).isEqualTo(0) }
            ruler.labels().value.forEach { assertThat(it.range.duration.value).isEqualTo(5) }
            assertThat(ruler.labels().value).hasSize(27)
            ruler.labels().value.forEach { assertThat(it.minWidthProperty().get()).isEqualTo(227.0) }
        }

        @Test
        fun `once scaled less than 10px, should increment labels by ten`() {
            ruler.scale = Scale.at(9.0).getOrThrow()

            ruler.labels().value.forEach { assertThat(it.range.start.value % 10).isEqualTo(0) }
            ruler.labels().value.forEach { assertThat(it.range.duration.value).isEqualTo(10) }
            assertThat(ruler.labels().value).hasSize(14)
            ruler.labels().value.forEach { assertThat(it.minWidthProperty().get()).isEqualTo(82.0) }
        }

        @Test
        fun `once scaled less than 5px, should increment labels by twenty`() {
            ruler.scale = Scale.at(4.0).getOrThrow()

            ruler.labels().value.forEach { assertThat(it.range.start.value % 20).isEqualTo(0) }
            ruler.labels().value.forEach { assertThat(it.range.duration.value).isEqualTo(20) }
            assertThat(ruler.labels().value).hasSize(7)
            ruler.labels().value.forEach { assertThat(it.minWidthProperty().get()).isEqualTo(72.0) }
        }

        @Test
        fun `once scaled less than 2px, should increment labels by fifty`() {
            ruler.scale = Scale.at(1.0).getOrThrow()

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
            ruler.visibleRange = TimeRange(500L..980L)

            assertThat(ruler.secondaryLabels().value).isEmpty()
        }

        @Test
        fun `over 1000, should show one secondary label`() {
            ruler.visibleRange = TimeRange(1200L..1480L)

            ruler.secondaryLabels().value.single().apply {
                assertThat(text).isEqualTo("1,000")
            }
        }

        @Test
        fun `well over 1000, should show secondary label`() {
            ruler.visibleRange = TimeRange(14_987_365..14_987_865L)

            ruler.secondaryLabels().value.single().apply {
                assertThat(text).isEqualTo("14,987,000")
            }
        }

        @Test
        fun `between two 1000s, should show two secondary labels`() {
            ruler.visibleRange = TimeRange(14_987_998..14_988_498L)

            assertThat(ruler.secondaryLabels().value).hasSize(2)
            assertThat(ruler.secondaryLabels().value.component1().text).isEqualTo("14,987,000")
            assertThat(ruler.secondaryLabels().value.component2().text).isEqualTo("14,988,000")
        }

        @Test
        fun `right on cusp before 1000, should show just one secondary label`() {
            ruler.visibleRange = TimeRange(998..1_498L)

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
            ruler.visibleRange = TimeRange(0..500L)
        }

        @Test
        fun `no selection, should not show any selection`() {
            assertThat(ruler.selectionRegions).isEmpty()
        }

        @Test
        fun `single unit of time selected, should show one region same size as time label`() {
            selection.add(UnitOfTime(3))

            val selectionRegion = ruler.selectionRegions.single()
            assertThat(selectionRegion.prefWidth).isEqualTo(48.0)
        }

        @Test
        fun `at smaller scales, should be exactly width of single unit of time`() {
            selection.add(UnitOfTime(3))
            ruler.scale = Scale.at(4.0).getOrThrow()

            val selectionRegion = ruler.selectionRegions.single()
            assertThat(selectionRegion.prefWidth).isEqualTo(4.0)
        }

        @Test
        fun `longer time range selected, should show one region across the time labels`() {
            selection.add(UnitOfTime(3) .. UnitOfTime(9))

            val selectionRegion = ruler.selectionRegions.single()
            assertThat(selectionRegion.prefWidth).isEqualTo(288.0)
        }

        @Test
        fun `when multiple time spans are selected, should show multiple regions`() {
            ruler.scale = Scale.at(6.0).getOrThrow()
            selection.add(UnitOfTime(3) .. UnitOfTime(9))
            selection.add(UnitOfTime(16) .. UnitOfTime(20))
            selection.add(UnitOfTime(42) .. UnitOfTime(67))

            assertThat(ruler.selectionRegions).anyMatch { it.prefWidth == 36.0 }
            assertThat(ruler.selectionRegions).anyMatch { it.prefWidth == 24.0 }
            assertThat(ruler.selectionRegions).anyMatch { it.prefWidth == 150.0 }
        }

        @Test
        fun `selection regions that are fully offscreen should not exist`() {
            ruler.scale = Scale.at(6.0).getOrThrow()
            ruler.visibleRange = TimeRange(8 .. 67L)
            selection.add(UnitOfTime(3) .. UnitOfTime(7)) // off screen
            selection.add(UnitOfTime(6) .. UnitOfTime(10)) // partial
            selection.add(UnitOfTime(12) .. UnitOfTime(20)) // fully on screen
            selection.add(UnitOfTime(60) .. UnitOfTime(70)) // partial
            selection.add(UnitOfTime(72) .. UnitOfTime(80)) // off-screen
            selection.add(UnitOfTime(5) .. UnitOfTime(90)) // on screen, but larger than screen

            assertThat(ruler.selectionRegions).hasSize(4)

        }

    }

    companion object {
        init {
            FxToolkit.registerPrimaryStage()
        }
    }

}