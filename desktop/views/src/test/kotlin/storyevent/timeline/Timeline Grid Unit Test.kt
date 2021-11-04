package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.makeStoryPointLabel
import com.soyle.stories.storyevent.timeline.Pixels
import com.soyle.stories.storyevent.timeline.Scale
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGrid
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGridComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import javafx.scene.Node
import javafx.scene.Parent
import javafx.scene.control.SkinBase
import kotlinx.coroutines.*
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.javafx.awaitPulse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.testfx.api.FxToolkit
import org.testfx.assertions.api.Assertions.assertThat
import org.testfx.assertions.api.Assertions.fail
import java.util.*

class `Timeline Grid Unit Test` : StoryPointLabelComponent by StoryPointLabelComponentDouble(), TimelineViewPortGridComponent.Gui {

    init {
        FxToolkit.registerPrimaryStage()
    }

    private val asyncContext = newSingleThreadContext("Async")
    private val guiContext = Dispatchers.JavaFx

    private val grid = TimelineViewPortGrid(asyncContext, guiContext, this)

    @Nested
    inner class Layout {

        // Rule: story events should be placed horizontally based on their starting point * scale

        init {
            grid.scale = Scale.at(30.0).getOrThrow()
            grid.offsetX = Pixels(874.0)
        }

        private val firstLabel = makeStoryPointLabel(time = UnitOfTime(56)).apply {
            resize(100.0, 24.0)
        }

        @Test
        fun `single story event`() {
            grid.labels.setAll(firstLabel)
            assertThat(firstLabel.layoutX).isEqualTo(1680.0)
        }

        @Test
        fun `multiple story events at the same time`() {
            val manyStoryEvents = List(5) {
                makeStoryPointLabel(time = UnitOfTime(56))
            }
            val allStoryEvents = manyStoryEvents + firstLabel
            grid.labels.setAll(allStoryEvents)
            runBlocking {
                withContext(asyncContext) { awaitPulse() }
                withContext(guiContext) { awaitPulse() }
            }
            allStoryEvents.sortedBy { it.storyEventId.uuid }.forEachIndexed { index, storyPointLabel ->
                assertThat(storyPointLabel.row).isEqualTo(index)
                assertThat(storyPointLabel.layoutY).isEqualTo((index * (4+(4+16+4)+4)) +4.0)
            }
        }

        @Test
        fun `story event from before that is not too wide`() {
            val pastLabel = makeStoryPointLabel(time = UnitOfTime(54))
            pastLabel.resize(51.0, 4+16+4.0)
            grid.labels.setAll(pastLabel, firstLabel)
            runBlocking {
                withContext(asyncContext) { awaitPulse() }
                withContext(guiContext) { awaitPulse() }
            }
            assertThat(firstLabel.row).isEqualTo(0)
            assertThat(firstLabel.layoutY).isEqualTo(4.0)
        }

        @Test
        fun `story event from before that is wide`() {
            val pastLabel = makeStoryPointLabel(time = UnitOfTime(54))
            pastLabel.resize(52.0, 4+16+4.0)
            grid.labels.setAll(pastLabel, firstLabel)
            runBlocking {
                withContext(asyncContext) { awaitPulse() }
                withContext(guiContext) { awaitPulse() }
            }
            assertThat(firstLabel.row).isEqualTo(1)
            assertThat(firstLabel.layoutY).isEqualTo((4+(4+16+4)+4) + 4.0)
        }

        @Test
        fun `should use standard width if collapsed`() {
            grid.scale = Scale.at(33.0).getOrThrow()
            grid.offsetX = Pixels(0.0)
            val labels = List(5) {
                makeStoryPointLabel(time = UnitOfTime(it.toLong() + 1)).apply {
                    resize(100.0, 24.0)
                }
            }
            grid.labels.setAll(labels)
            grid.areLabelsCollapsed = true

            runBlocking {
                withContext(asyncContext) { awaitPulse() }
                withContext(guiContext) { awaitPulse() }
            }

            labels.forEach {
                assertTrue(it.isCollapsed)
                assertThat(it.cachedWidth).isEqualTo(100.0)
                assertThat(it.row).isEqualTo(0)
            }
        }


        // Rule: only labels within the clip of the grid should be added as children
        @Nested
        inner class `Rule - Only labels within the clip of the grid should be added as children` {

            init {
                grid.resize(300.0, 200.0)
                grid.labels.setAll(firstLabel)
            }

            @Test
            fun `label completely before clip`() {
                grid.offsetX = Pixels(906.0)

                runBlocking {
                    withContext(asyncContext) { awaitPulse() }
                    withContext(guiContext) { awaitPulse() }
                }

                assertThat(firstLabel.parent).isNull()
            }

            @Test
            fun `label intersecting with start of clip`() {
                grid.offsetX = Pixels(1700.0)

                runBlocking {
                    withContext(asyncContext) { awaitPulse() }
                    withContext(guiContext) { awaitPulse() }
                }

                firstLabel.mustDescendFrom(grid)
            }

            @Test
            fun `label intersecting with end of clip`() {
                grid.offsetX = Pixels(1400.0)

                runBlocking {
                    withContext(asyncContext) { awaitPulse() }
                    withContext(guiContext) { awaitPulse() }
                }

                firstLabel.mustDescendFrom(grid)
            }

            @Test
            fun `label completely after clip`() {
                grid.offsetX = Pixels(506.0)

                runBlocking {
                    withContext(asyncContext) { awaitPulse() }
                    withContext(guiContext) { awaitPulse() }
                }

                assertThat(firstLabel.parent).isNull()
            }

            @Test
            fun `all of the above`() {
                grid.offsetX = Pixels(500.0)
                val labels = listOf(
                    makeStoryPointLabel(time = UnitOfTime(13)),
                    makeStoryPointLabel(time = UnitOfTime(15)),
                    makeStoryPointLabel(time = UnitOfTime(20)),
                    makeStoryPointLabel(time = UnitOfTime(25)),
                    makeStoryPointLabel(time = UnitOfTime(27))
                ).onEach { it.resize(100.0, 24.0) }
                grid.labels.setAll(labels)

                runBlocking {
                    withContext(asyncContext) { awaitPulse() }
                    withContext(guiContext) { awaitPulse() }
                }

                labels.subList(1, 4).forEach { it.mustDescendFrom(grid) }
                listOf(labels[0], labels[4]).forEach { assertThat(it.parent).isNull() }
            }

        }

        @Test
        fun `should auto-size labels that are visible`() {
            grid.resize(300.0, 200.0)
            grid.offsetX = Pixels(500.0)
            val labels = listOf(
                makeStoryPointLabel(time = UnitOfTime(13)),
                makeStoryPointLabel(time = UnitOfTime(15)),
                makeStoryPointLabel(time = UnitOfTime(20)),
                makeStoryPointLabel(time = UnitOfTime(25)),
                makeStoryPointLabel(time = UnitOfTime(27))
            ).onEach {
                it.text = UUID.randomUUID().toString().run { take((0 .. length).random()) }
                it.resize(100.0, 10.0)
                it.skin = object : SkinBase<StoryPointLabel>(it) {
                    override fun computePrefWidth(
                        height: Double,
                        topInset: Double,
                        rightInset: Double,
                        bottomInset: Double,
                        leftInset: Double
                    ): Double {
                        return 120.0
                    }
                }
            }
            grid.labels.setAll(labels)

            runBlocking {
                withContext(asyncContext) { awaitPulse() }
                withContext(guiContext) { awaitPulse() }
            }
            grid.layout()

            assertThat(listOf(labels[1], labels[2], labels[3])).allMatch { it.width == 120.0 }
            assertThat(listOf(labels[0], labels[4])).allMatch { it.width == 100.0 }
        }

    }

    @Test
    fun `whenever a cached width is updated, should layout`() {
        grid.offsetX = Pixels(1356.0)
        grid.labels.setAll(
            listOf(
                makeStoryPointLabel(time = UnitOfTime(13)),
                makeStoryPointLabel(time = UnitOfTime(15)),
                makeStoryPointLabel(time = UnitOfTime(20)),
                makeStoryPointLabel(time = UnitOfTime(25)),
                makeStoryPointLabel(time = UnitOfTime(27))
            ).onEach {
                it.text = UUID.randomUUID().toString().run { take((0 .. length).random()) }
            }
        )
        runBlocking {
            withContext(asyncContext) { awaitPulse() }
            withContext(guiContext) { awaitPulse() }
        }
        grid.labels.forEach { assertThat(it.parent).isNull() }

        grid.labels[4].cachedWidth = 120.0
        runBlocking {
            withContext(asyncContext) { awaitPulse() }
            withContext(guiContext) { awaitPulse() }
        }

        grid.labels.last().mustDescendFrom(grid)
        grid.labels.subList(0, 4).forEach { assertThat(it.parent).isNull() }
    }

    @Test
    fun `should relayout when scaled`() {
        grid.labels.setAll(
            listOf(
                makeStoryPointLabel(time = UnitOfTime(13)),
                makeStoryPointLabel(time = UnitOfTime(15)),
                makeStoryPointLabel(time = UnitOfTime(20)),
                makeStoryPointLabel(time = UnitOfTime(25)),
                makeStoryPointLabel(time = UnitOfTime(27))
            ).onEach {
                it.resize(100.0, 0.0)
                it.text = UUID.randomUUID().toString().run { take((0 .. length).random()) }
            }
        )
        runBlocking {
            withContext(asyncContext) { awaitPulse() }
            withContext(guiContext) { awaitPulse() }
        }
        grid.layout()
        grid.scale = Scale.maxZoomOut()
        runBlocking {
            withContext(asyncContext) { awaitPulse() }
            withContext(guiContext) { awaitPulse() }
        }

        grid.labels.forEachIndexed { index, storyPointLabel ->
            assertThat(storyPointLabel.row).isEqualTo(index)
        }
    }

    @Test
    fun `should relayout when event time changes`() {
        val labels = listOf(
            makeStoryPointLabel(time = UnitOfTime(1)),
            makeStoryPointLabel(time = UnitOfTime(2))
        ).onEach { it.resize(100.0, 24.0) }
        grid.labels.setAll(labels)
        runBlocking {
            withContext(asyncContext) { awaitPulse() }
            withContext(guiContext) { awaitPulse() }
        }

        labels[0].time = 3L

        runBlocking {
            withContext(asyncContext) { awaitPulse() }
            withContext(guiContext) { awaitPulse() }
        }
        assertThat(labels[0].layoutX).isEqualTo(144.0)
        assertThat(grid.labels[1].row).isEqualTo(0)
        assertThat(grid.labels[0].row).isEqualTo(1)
    }

    private fun Node.mustDescendFrom(expectedAncestor: Parent) {
        if (parent == expectedAncestor) return
        if (parent == null) fail<Nothing>("$this does not descend from $expectedAncestor")
        parent!!.mustDescendFrom(expectedAncestor)
    }

}