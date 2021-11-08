package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.common.events.*
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.TimelineViewPortComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.makeStoryPointLabel
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.item.StoryEventItemViewModel
import com.soyle.stories.storyevent.timeline.Pixels
import com.soyle.stories.storyevent.timeline.Scale
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.unit
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPort
import com.soyle.stories.storyevent.timeline.viewport.TimelineViewPortComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import io.mockk.verify
import javafx.collections.ObservableList
import javafx.scene.input.MouseDragEvent
import javafx.scene.input.MouseEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.javafx.awaitPulse
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import tornadofx.observableListOf
import kotlin.math.exp

class `Timeline ViewPort Unit Test` :
    StoryPointLabelComponent by StoryPointLabelComponentDouble() {

    init {
        FxToolkit.registerPrimaryStage()
    }

    private val component: TimelineViewPortComponentDouble = TimelineViewPortComponentDouble()
    private val viewPort = component.TimelineViewPort()
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
            viewPort.storyPointLabels.setAll(labels)
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
        fun `half width of the viewport should be subtracted from the max offset`() {
            viewPort.resize(200.0, 100.0)

            assertThat(viewPort.maxOffsetX().get()).isEqualTo(250.0) // 6*30 + 170 - 100
        }

        @Test
        fun `should never be negative`() {
            viewPort.resize(800.0, 100.0)

            assertThat(viewPort.maxOffsetX().get()).isEqualTo(0.0) // 6*30 + 170 - 400 = -50 -> 0.0
        }

        @Test
        fun `if offset x is larger than max, should be set to current offset x value`() {
            viewPort.resize(200.0, 100.0)
            presenter.offsetX().set(Pixels(300.0))

            assertThat(viewPort.maxOffsetX().get()).isEqualTo(300.0) // 6*30 + 170 - 100 < 300
        }

        @Test
        fun `if offset x scrolls back less than max, max should be set to original max value`() {
            viewPort.resize(200.0, 100.0)
            presenter.offsetX().set(Pixels(300.0))
            presenter.offsetX().set(Pixels(140.0))

            assertThat(viewPort.maxOffsetX().get()).isEqualTo(250.0) // 6*30 + 170 - 100 > 140
        }

    }


    @Nested
    inner class `Drag and Drop Story Events` {

        private val label = makeStoryPointLabel()

        init {
            viewPort.resize(600.0, 400.0)
            viewPort.layout()

            runBlocking {
                withContext(Dispatchers.Default) { awaitPulse() }
                withContext(Dispatchers.JavaFx) { awaitPulse() }
            }

            viewPort.storyPointLabels.add(label)
            label.selection().get().add(label)
        }

        @Test
        fun `should create duplicate story event label when dragged`() {
            viewPort.fireEvent(dragDetectedEvent(pickResult = pickResult(label)))

            assertThat(viewPort.dragLabels)
                .hasSize(1)
                .first()
                .satisfies {
                    assertThat(it.storyEventId).isEqualTo(label.storyEventId)
                    assertThat(it.time).isEqualTo(label.time)
                    assertThat(it.row).isEqualTo(label.row)
                    assertThat(it.width).isEqualTo(label.width)
                    assertThat(it.height).isEqualTo(label.height)
                }
        }

        @Nested
        inner class `Given label has been removed` {

            init {
                viewPort.storyPointLabels.remove(label)
            }

            @Test
            fun `should no longer detect drag`() {
                viewPort.fireEvent(dragDetectedEvent(pickResult = pickResult(label)))

                assertThat(viewPort.dragLabels).isEmpty()
            }

        }

        @Test
        fun `if multiple story points have been selected, should create duplicates for all of them`() {
            val additionalLabels = List(6) { makeStoryPointLabel() }.onEach { it.resize(50.0, 50.0) }
            viewPort.storyPointLabels.addAll(additionalLabels)
            additionalLabels.onEach { it.selection().get().add(it) }

            viewPort.fireEvent(dragDetectedEvent(pickResult = pickResult(label)))

            assertThat(viewPort.dragLabels)
                .hasSize(7)
                .allSatisfy { dragLabel ->
                    val backingLabel = (additionalLabels + label).single { it.storyEventId == dragLabel.storyEventId }
                    assertThat(dragLabel.time).isEqualTo(backingLabel.time)
                    assertThat(dragLabel.name).isEqualTo(backingLabel.name)
                    assertThat(dragLabel.row).isEqualTo(backingLabel.row)
                    assertThat(dragLabel.width).isEqualTo(backingLabel.width)
                    assertThat(dragLabel.height).isEqualTo(backingLabel.height)
                }
        }

        @Nested
        inner class `Given Labels are being dragged` {

            @ParameterizedTest
            @ValueSource(ints = [2, 6, 14])
            fun `should move drag labels with mouse`(expectedAdjustment: Int) {
                val additionalLabels = List(6) { makeStoryPointLabel(time = 4L.unit) }
                viewPort.storyPointLabels.addAll(additionalLabels)
                additionalLabels.onEach { it.selection().get().add(it) }
                viewPort.fireEvent(dragDetectedEvent(pickResult = pickResult(additionalLabels.first())))

                viewPort.fireEvent(mouseDraggedEvent(x = viewPort.scale(expectedAdjustment.toLong().unit).value))

                assertThat(viewPort.dragLabels)
                    .hasSize(7)
                    .allSatisfy { dragLabel ->
                        val backingLabel =
                            (additionalLabels + label).single { it.storyEventId == dragLabel.storyEventId }
                        assertThat(dragLabel.time).isEqualTo(backingLabel.time + expectedAdjustment)
                    }
            }

            @Test
            fun `subsequent drags should override previous`() {
                val additionalLabels = List(6) { makeStoryPointLabel(time = 2L.unit) }
                viewPort.storyPointLabels.addAll(additionalLabels)
                label.selection().get().clear() // remove [label] from selection
                additionalLabels.onEach { it.selection().get().add(it) }
                viewPort.fireEvent(dragDetectedEvent(pickResult = pickResult(additionalLabels.first())))

                viewPort.fireEvent(mouseDraggedEvent(x = viewPort.scale(4L.unit).value))
                viewPort.fireEvent(mouseDraggedEvent(x = viewPort.scale(3L.unit).value))
                viewPort.fireEvent(mouseDraggedEvent(x = viewPort.scale(6L.unit).value))

                assertThat(viewPort.dragLabels)
                    .hasSize(6)
                    .allSatisfy { dragLabel ->
                        assertThat(dragLabel.time).isEqualTo(8L)
                    }
            }

            @Nested
            inner class `When Dragged to Edge of Screen` {

                init {
                    viewPort.fireEvent(dragDetectedEvent(pickResult = pickResult(label)))
                }

                @Test
                fun `should scroll`() {
                    repeat(4) {
                        viewPort.fireEvent(mouseDraggedEvent(x = 592.0))

                        assertThat(viewPort.offsetX.value).isEqualTo(2.0 * (it + 1))
                    }
                }

            }

            @Nested
            inner class `When Scrolled During Drag` {

                init {
                    viewPort.fireEvent(dragDetectedEvent(pickResult = pickResult(label)))
                    presenter.offsetX().set(Pixels(1680.0))
                }

                @Test
                fun `should update time based on scroll amount`() {
                    viewPort.fireEvent(mouseDraggedEvent(x = viewPort.scale(4L.unit).value))

                    assertThat(viewPort.dragLabels)
                        .hasSize(1).first()
                        .satisfies {
                            assertThat(it.time).isEqualTo(39L)
                        }
                }

            }

            @Nested
            inner class `Given Drag Started with Offset` {

                init {
                    label.time = 42L
                    presenter.offsetX().set(Pixels(1680.0))
                    viewPort.fireEvent(dragDetectedEvent(pickResult = pickResult(label)))
                }

                @Nested
                inner class `When Scrolled back` {

                    @Test
                    fun `should update time based on scrolled x`() {
                        presenter.offsetX().set(Pixels(0.0))

                        viewPort.fireEvent(mouseDraggedEvent(x = viewPort.scale(4L.unit).value))

                        assertThat(viewPort.dragLabels)
                            .hasSize(1).first()
                            .satisfies {
                                assertThat(it.time).isEqualTo(11L)
                            }
                    }

                }

            }

            @Nested
            inner class `When Drag is Released` {

                private val additionalLabels = List(6) { makeStoryPointLabel(time = 4L.unit) }

                init {
                    viewPort.storyPointLabels.addAll(additionalLabels)
                    additionalLabels.take(3).onEach { it.selection().get().add(it) }
                    viewPort.fireEvent(dragDetectedEvent(pickResult = pickResult(additionalLabels.first())))
                    viewPort.fireEvent(mouseDraggedEvent(x = viewPort.scale(6L.unit).value))
                }

                @Test
                fun `should send adjustment amount to controller`() {
                    val expectedAdjustment = 6L
                    viewPort.fireEvent(mouseDraggedEvent(x = viewPort.scale(expectedAdjustment.unit).value))

                    viewPort.fireEvent(mouseReleasedEvent())

                    verify {
                        component.dependencies.adjustStoryEventsTimeController.adjustStoryEventsTime(
                            any(),
                            expectedAdjustment
                        )
                    }
                }

                @Test
                fun `should send all selected story event ids to controller`() {
                    viewPort.fireEvent(mouseReleasedEvent())

                    verify {
                        component.dependencies.adjustStoryEventsTimeController.adjustStoryEventsTime(
                            (additionalLabels.take(3) + label).map { it.storyEventId }.toSet(), any()
                        )
                    }
                }

                @Test
                fun `should clear drag labels`() {
                    viewPort.fireEvent(mouseReleasedEvent())

                    assertThat(viewPort.dragLabels).isEmpty()
                }

                @Test
                fun `selected labels should no longer be in dragged state`() {
                    viewPort.fireEvent(mouseReleasedEvent())

                    assertThat(viewPort.storyPointLabels)
                        .allSatisfy {
                            assertThat(it).doesNotHaveStyle("dragged")
                        }
                }

            }

        }

    }

}