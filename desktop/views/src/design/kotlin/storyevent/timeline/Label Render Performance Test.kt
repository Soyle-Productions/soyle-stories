package com.soyle.stories.desktop.view.storyevent.timeline

import com.soyle.stories.common.NoSelectionModel
import com.soyle.stories.common.onChangeWithCurrent
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.StoryPointLabelComponentDouble
import com.soyle.stories.desktop.view.storyevent.timeline.viewport.grid.label.makeStoryPointLabel
import com.soyle.stories.storyevent.timeline.Pixels
import com.soyle.stories.storyevent.timeline.Scale
import com.soyle.stories.storyevent.timeline.TimeRange
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGrid
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGridComponent
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelComponent
import javafx.beans.property.*
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.control.skin.LabelSkin
import javafx.scene.layout.GridPane
import javafx.scene.layout.Pane
import javafx.scene.layout.Priority
import javafx.scene.paint.Color
import javafx.stage.Stage
import javafx.stage.StageStyle
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.javafx.JavaFx
import kotlinx.coroutines.javafx.awaitPulse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.junit.jupiter.api.Test
import org.testfx.api.FxRobot
import org.testfx.api.FxToolkit.registerPrimaryStage
import tornadofx.*
import java.util.*
import kotlin.math.ceil
import kotlin.math.floor

class `Label Render Performance Test` : FxRobot() {

    val generator = sequence<String> {
        while (true) {
            yield(List((1..500).random()) {
                ('A'..'z').random()
            }.joinToString())
        }
    }

    @Test
    fun `node`() {

        registerPrimaryStage()
        var stage: Stage? = null

        val nodeCount = 100_000
        val timeRange = 1_000_000L
        val cellSize = 1.0

        val gui = object : TimelineViewPortGridComponent.Gui,
            StoryPointLabelComponent by StoryPointLabelComponentDouble()
        {}

        val graph = GridPane()
        val grid = TimelineViewPortGrid(Dispatchers.Default, Dispatchers.JavaFx, gui)
        graph.apply {
            val hbar = ScrollBar().apply { orientation = Orientation.HORIZONTAL }
            val vbar = ScrollBar().apply { orientation = Orientation.VERTICAL }
            addColumn(0, grid, hbar)
            addColumn(1, vbar, Pane())

            grid.apply {
                scale = Scale.maxZoomOut()
                gridpaneColumnConstraints {
                    hgrow = Priority.ALWAYS
                    vgrow = Priority.ALWAYS
                }
                hbar.valueProperty().onChange { offsetX = Pixels(it) }
                hbar.max = scale(UnitOfTime(timeRange)).value
                vbar.valueProperty().onChange { offsetY = Pixels(it) }
                vbar.maxProperty().bind(prefHeightProperty())
                setOnScroll {
                    vbar.value = (vbar.value - it.deltaY).coerceAtLeast(0.0)
                    hbar.value = (hbar.value - it.deltaX).coerceAtLeast(0.0)
                }
                areLabelsCollapsed = true
                labels.setAll(List(nodeCount) {
                    gui.makeStoryPointLabel(
                        name ="Label $it: ${UUID.randomUUID().toString().run { take((0..length).random()) }}",
                        time = UnitOfTime((0..timeRange).random())
                    )
                })
            }
            hbar.apply {
                gridpaneColumnConstraints {
                    hgrow = Priority.ALWAYS
                }
            }
            vbar.apply {
                gridpaneColumnConstraints {
                    vgrow = Priority.ALWAYS
                }
            }
        }
        interact {
            stage = Stage(StageStyle.DECORATED)
            val stage = stage!!
            stage.scene = Scene(graph, 800.0, 500.0)
            grid.measuring().onChangeWithCurrent {
                it ?: return@onChangeWithCurrent
                if (it) stage.titleProperty().bind(grid.measuredCount().asString("Measured: %d"))
                else stage.titleProperty().cleanBind(stringProperty("Measuring complete"))
            }
            FX.applyStylesheetsTo(stage.scene)
            stage.show()
        }

        while (stage!!.isShowing) {
        }
    }

    class StoryPointLabel(text: String, time: Long) : Label(text) {

        private val timeProperty = SimpleLongProperty(this, "time", time)
        fun time() = timeProperty
        var time by time()

        private val cacheWidthProperty = SimpleDoubleProperty(this, "cacheWidth", 0.0)
        fun cacheWidth(): DoubleProperty = cacheWidthProperty
        var cacheWidth: Double by cacheWidth()

        private val rowProperty = objectProperty<Int?>()
        fun row() = rowProperty
        var row by row()

        init {
            disableProperty().bind(rowProperty.isNull)
            layoutYProperty().bind(doubleBinding(rowProperty) { (row ?: 0).times(32.0).plus(4) })
        }

        private val timeRangeProperty = objectProperty<TimeRange>(TimeRange(time..time + 1))
        fun timeRange() = timeRangeProperty
        var timeRange by timeRange()

        override fun setWidth(value: Double) {
            if (cacheWidth == 0.0 || cacheWidth < value) cacheWidth = value
            super.setWidth(value)
        }
    }

    @Test
    fun `asynchronous recalculation`() {
        registerPrimaryStage()
        var stage: Stage? = null
        val asyncScope = CoroutineScope(newFixedThreadPoolContext(1, "Async"))

        val nodeCount = 100_000
        val timeRange = 1_000_000L
        val cellSize = 1.0
        val minDistance = 8.0

        fun scale(pixels: Double): Long = floor(pixels / cellSize).toLong()
        fun scale(start: Long, pixels: Double): TimeRange {
            return TimeRange(start..start + ceil(pixels / cellSize).toLong())
        }

        fun coveredTimeRange(label: StoryPointLabel): TimeRange {
            return scale(label.time, label.cacheWidth + minDistance)
        }


        val labels = List(nodeCount) {
            StoryPointLabel(
                "Label $it: ${UUID.randomUUID().toString().run { take((0..length).random()) }}",
                (0..timeRange).random()
            ).apply {
                layoutX = time * cellSize
                style { borderColor = multi(box(Color.BLUE)) }
            }
        }.groupBy { it.time }.toSortedMap()

        val updateQueue = Channel<Triple<Any, Number, Number>>(1, BufferOverflow.DROP_OLDEST)

        asyncScope.launch {
            for (update in updateQueue) {
                println("updating layout... because ${update.first} updated ${update.second} to ${update.third}")
                val ranges = withContext(Dispatchers.JavaFx) {
                    labels.values.asSequence().flatten().associateWith { coveredTimeRange(it) }
                }
                println("calculated ranges.  back on ${Thread.currentThread().name}")
                val newOccupiedCells = mutableMapOf<Long, MutableMap<Int, StoryPointLabel>>()
                val rowUpdates = labels.flatMap { (time, values) ->
                    newOccupiedCells.keys.filter { it < time }.forEach { newOccupiedCells.remove(it) }
                    values.map { label ->
                        val row =
                            generateSequence(0) { it + 1 }.first { newOccupiedCells[time]?.containsKey(it) != true }
                        ranges.getValue(label)
                            .forEach { newOccupiedCells.getOrPut(it.value, ::mutableMapOf)[row] = label }
                        label to row
                    }
                }
                withContext(Dispatchers.JavaFx) {
                    println("updating rows... ")
                    rowUpdates.forEach { (label, newRow) ->
                        label.row = newRow
                        label.timeRange = ranges.getValue(label)
                    }
                    println("update complete")
                }
            }
        }
        val cacheWidthListener = ChangeListener<Number> { obs, old, new ->
            asyncScope.launch {
                if (obs is Property) {
                    updateQueue.send(Triple(obs.bean, old, new))
                }
            }
        }
        labels.values.flatten().forEach { label ->
            label.cacheWidth().addListener(cacheWidthListener)
        }
        run {
            val newOccupiedCells = mutableMapOf<Long, MutableMap<Int, StoryPointLabel>>()
            labels.forEach { (time, values) ->
                newOccupiedCells.keys.filter { it < time }.forEach { newOccupiedCells.remove(it) }
                values.forEach { label ->
                    val row = generateSequence(0) { it + 1 }.first { newOccupiedCells[time]?.containsKey(it) != true }
                    label.row = row
                    label.timeRange.forEach { newOccupiedCells.getOrPut(it.value, ::mutableMapOf)[row] = label }
                }
            }
        }
        val graph = Pane()
        val measurer = object : LabelSkin(Label()) {

        }
        val measuring = booleanProperty(false)
        val countMeasured = intProperty(0)
        val millisecondsPerHalfFrame = 100 // (1,000,000 ns / 60 fps) / 2
        fun measureLabels() {
            asyncScope.launch {
                withContext(Dispatchers.JavaFx) {
                    var start: Long? = null
                    var atLeastOne = false
                    val labelsToMeasure = labels.values.asSequence().flatten()
                        .filter { it.cacheWidth == 0.0 }
                        .takeWhile {
                            atLeastOne = true
                            (Date().time - start!!) < millisecondsPerHalfFrame
                        }
                        .onEach { label ->
                            label.skin = measurer
                            label.autosize()
                            label.skin = null
                        }
                    start = Date().time
                    val measuredCount = labelsToMeasure.count()
                    println("measured $measuredCount in ${Date().time - start} milliseconds")
                    if (atLeastOne) {
                        measuring.set(true)
                        awaitPulse()
                        countMeasured.set(countMeasured.get() + measuredCount)
                        measureLabels()
                    } else {
                        measuring.set(false)
                    }
                }
            }
        }
        graph.apply {
            vbox {
                style { backgroundColor = multi(Color.WHITE) }
                fitToParentSize()
                val scrollBar = ScrollBar().apply {
                    orientation = Orientation.HORIZONTAL
                    max = timeRange * cellSize
                }
                setOnScroll { scrollBar.value = (scrollBar.value - it.deltaX).coerceAtLeast(0.0) }
                pane {
                    vgrow = Priority.ALWAYS
                    translateXProperty().bind(scrollBar.valueProperty().negate())
                    val visibleLabels = objectBinding(scrollBar.valueProperty(), widthProperty()) {
                        val visibleRange = scale(scale(scrollBar.value), width)
                        labels.values.asSequence().flatten().filter {
                            it.time < visibleRange.range.last && it.timeRange.endInclusive >= visibleRange.range.first
                        }.toList()
                    }
                    properties["visibleLabels"] = visibleLabels
                    dynamicContent(visibleLabels) {
                        children.setAll(it)
                    }

                }
                add(scrollBar)
            }
        }
        measureLabels()
        interact {
            stage = Stage(StageStyle.DECORATED)
            val stage = stage!!
            stage.titleProperty()
                .bind(countMeasured.stringBinding(measuring) { if (measuring.value) "Measuring... ${it} / 100,000" else "" })
            stage.scene = Scene(graph, 800.0, 500.0)
            stage.show()
        }

        while (stage!!.isShowing) {
        }
    }

    @Test
    fun `asynchronous bubbling`() {
        registerPrimaryStage()
        var stage: Stage? = null
        val layoutMap = observableMapOf<Pair<Long, Int>, Label>()

        val asyncScope = CoroutineScope(Dispatchers.Default)

        val nodeCount = 100_000
        val timeRange = 200_000L
        val cellSize = 1.0
        val minDistance = 8.0

        val updateQueue = mutableSetOf<Label>()

        val queueMutex = Mutex()
        val layoutMapMutex = Mutex()

        suspend fun updateLayout(label: Label) {
            val time = label.properties["time"] as Long
            val rowIndex = label.properties["row"] as IntegerProperty
            val timeRange = TimeRange(time..time + ceil((label.width + minDistance) / cellSize).toLong())

            val newRow = layoutMapMutex.withLock {
                generateSequence(0) { it + 1 }.first { testRow ->
                    val existingLabel = layoutMap[time to testRow]
                    existingLabel == null || existingLabel == label || (existingLabel.properties["time"] as Long) > time
                }
            }
            if (newRow != rowIndex.get()) {
                layoutMapMutex.withLock {
                    timeRange.forEach { layoutMap.remove(it.value to rowIndex.get()) }
                }
            }

            val existingLabels = withContext(Dispatchers.JavaFx) {
                rowIndex.set(newRow)
                layoutMapMutex.withLock {
                    timeRange.mapNotNull { layoutMap.put(it.value to newRow, label) }
                }
            }
            existingLabels.toSet().sortedBy {
                it.properties["time"] as Long
            }.forEach {
                if (queueMutex.withLock {
                        if (!updateQueue.contains(it)) {
                            updateQueue.add(it)
                            true
                        } else false
                    }) updateLayout(it)
            }
        }

        val labels = List(nodeCount) {
            Label("Label $it: ${UUID.randomUUID().toString().run { take((0..length).random()) }}").apply {
                val time = (0..timeRange).random()
                properties["time"] = time
                val rowIndex = intProperty(0)
                properties["row"] = rowIndex
                widthProperty().addListener { _, oldValue, _ ->
                    val oldRange = TimeRange(time..time + ceil((oldValue.toDouble() + minDistance) / cellSize).toLong())
                    asyncScope.launch {
                        layoutMapMutex.withLock {
                            oldRange.forEach { layoutMap.remove(it.value to rowIndex.get()) }
                        }
                        if (queueMutex.withLock { updateQueue.contains(this@apply) }) return@launch
                        updateLayout(this@apply)
                        queueMutex.withLock { updateQueue.clear() }
                    }
                }
                rowIndex.onChange {
                    layoutY = (it * 32.0) + 4
                }
                style { borderColor = multi(box(Color.BLUE)) }
            }
        }.groupBy { it.properties["time"] as Long }
        labels.entries.sortedBy { it.key }.forEach { (_, values) ->
            values.forEachIndexed { index, label ->
                (label.properties["row"] as IntegerProperty).set(index)
            }
        }
        val graph = Pane().apply {
            listview<Long> {
                fitToParentSize()
                selectionModel = NoSelectionModel()
                orientation = Orientation.HORIZONTAL
                items.setAll(List(timeRange.toInt()) { it.toLong() })
                fixedCellSize = cellSize
                cellFormat {
                    val labelsThatStartHere = labels[it]
                    style {
                        backgroundColor = multi(Color.TRANSPARENT)
                    }
                    graphic = labelsThatStartHere?.let {
                        Pane().apply {
                            children.setAll(labelsThatStartHere)
                        }
                    }
                }
            }
        }
        interact {
            stage = Stage(StageStyle.DECORATED)
            val stage = stage!!
            stage.scene = Scene(graph, 800.0, 500.0)
            stage.show()
        }

        while (stage!!.isShowing) {
        }
    }


}