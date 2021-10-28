package com.soyle.stories.storyevent.timeline.viewport.grid

import com.soyle.stories.common.collections.binarySubList
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.onChangeUntil
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.timeline.Pixels
import com.soyle.stories.storyevent.timeline.Scale
import com.soyle.stories.storyevent.timeline.TimeRange
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGridStyles.Companion.LABEL_SPACING
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGridStyles.Companion.ROW_HEIGHT
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGridStyles.Companion.ROW_V_PADDING
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGridStyles.Companion.timelineViewPortGrid
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel.Companion.INVALID_CACHE
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.beans.property.*
import javafx.beans.value.WeakChangeListener
import javafx.collections.FXCollections
import javafx.collections.ListChangeListener
import javafx.collections.WeakListChangeListener
import javafx.scene.layout.Region
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tornadofx.*
import java.lang.Double.max
import java.util.*
import kotlin.coroutines.CoroutineContext


/**
 * Responsible to laying out the [labels].  They will be arranged according to their time and id.  Labels will not
 * overlap, so if a label is meant to be at a certain time, but a past label is currently there, it will be pushed to
 * the next row.  Due to the potentially large number of labels and time values dealt with, the grid will use async
 * coroutines to arrange the labels and to calculate their layout bounds.  However, updates to the layout bounds will
 * always happen on the GUI thread.  Additionally, due to the number of potential labels, only labels that should appear
 * within the viewport will be in the scene graph.  But, since the width of the labels in part determines if they are in
 * the viewport and how they are arranged, it may not be possible to fully arrange all labels immediately.  Instead, the
 * labels that are known to be in the viewport because their layout X is within it will be displayed first.  Any labels
 * still off-screen will be measured using coroutines and a dummy label.  When widths are updated, the arrangement of the
 * labels will be periodically updated.
 */
@Suppress("RemoveExplicitTypeArguments")
class TimelineViewPortGrid(
    private val asyncContext: CoroutineContext,
    private val guiContext: CoroutineContext,
    gui: TimelineViewPortGridComponent.Gui
) : Region(), CoroutineScope by CoroutineScope(asyncContext) {

    init {
        addClass(timelineViewPortGrid)
    }

    //**************************************************************************
    //*                                                                        *
    //* region Properties                                                      *
    //*                                                                        *
    //**************************************************************************

    /**
     * the labels to arrange.
     */
    val labels = observableListOf<StoryPointLabel>()

    /**
     * keeps the labels sorted so that the arrangement can arrange everything properly.
     */
    private val _observableLabels = FXCollections.observableArrayList<StoryPointLabel>{ arrayOf(it.time()) }
        .apply { bind(labels) { it } }
    private val sortedLabels = _observableLabels.sorted(compareBy({ it.time }, { it.storyEventId.uuid }))

    /**
     * the scale at which to render the labels.
     */
    private val scaleProperty = objectProperty<Scale>(Scale.maxZoomIn())
    /** @see scaleProperty */
    fun scale(): ObjectProperty<Scale> = scaleProperty
    /** @see scaleProperty */
    var scale: Scale by scaleProperty

    /**
     * The amount in which the labels are shifted upward
     */
    private val offsetYProperty = objectProperty<Pixels>(Pixels(0.0))
    /** @see offsetYProperty */
    fun offsetY() = offsetYProperty
    /** @see offsetYProperty */
    var offsetY: Pixels by offsetYProperty

    /**
     * The amount in which the labels are shifted left
     */
    private val offsetXProperty = objectProperty<Pixels>(Pixels(0.0))
    /** @see offsetXProperty */
    fun offsetX() = offsetXProperty
    /** @see offsetXProperty */
    var offsetX: Pixels by offsetXProperty

    /**
     * Whether all the labels should be collapsed or not
     */
    private val labelsCollapsedProperty = booleanProperty(false)
    /** @see labelsCollapsedProperty */
    fun labelsCollapsed() = labelsCollapsedProperty
    /** @see labelsCollapsedProperty */
    var areLabelsCollapsed: Boolean by labelsCollapsed()


    //**************************************************************************
    //*                                                                        *
    //* endregion Properties                                                   *
    //*                                                                        *
    //* region Outline                                                         *
    //*                                                                        *
    //**************************************************************************

    private suspend fun arrangeLabels(arrangement: Arrangement) {
        // cache them temporarily so that, if an update
        // comes through during the arrangement, the
        // arrangement will still be valid.
        val labelsByTimeRange = arrangement.associateLabelsToCoveredTimeRanges()
        val labelRowUpdates = arrangement.arrangeLabelsByTimeAndRow(labelsByTimeRange)
        arrangement.updateLabelRows(labelRowUpdates)
    }

    private fun shouldLabelBeVisible(
        viewPortRightBound: Pixels,
        arrangementWidth: StoryPointLabel.() -> Double
    ): (StoryPointLabel) -> Boolean = fun(label: StoryPointLabel): Boolean {
        return label.layoutX < viewPortRightBound.value &&
                label.layoutX + label.arrangementWidth() >= offsetX.value
    }

    private fun measureLabel(dummyLabel: StoryPointLabel): suspend (StoryPointLabel) -> Unit = { label ->
        dummyLabel.text = label.text
        dummyLabel.autosize()
        if (dummyLabel.width > 0.0 && dummyLabel.height > 0.0) {
            label.cachedWidth = dummyLabel.width
        }
    }

    //**************************************************************************
    //*                                                                        *
    //* endregion Outline                                                      *
    //*                                                                        *
    //* region Arrangement                                                     *
    //*                                                                        *
    //**************************************************************************

    private val arrangementQueue = Channel<Unit>(1, BufferOverflow.DROP_OLDEST)
    private fun requestArrangement() {
        launch { arrangementQueue.send(Unit) }
    }

    @Suppress("unused")
    private val arrangementJob = launch {
        for (stack in arrangementQueue) {
            arrangeLabels(Arrangement(arrangementWidthBinding.value!!))
        }
    }

    private fun arrangementWidth(areLabelsCollapsed: Boolean): StoryPointLabel.() -> Double {
        if (areLabelsCollapsed) {
            return { TimelineViewPortGridStyles.COLLAPSED_LABEL_WIDTH }
        } else {
            return {
                if (cachedWidth == INVALID_CACHE) TimelineViewPortGridStyles.COLLAPSED_LABEL_WIDTH
                else cachedWidth
            }
        }
    }

    private var durationInvalid = false
    private val invalidDurationListener = InvalidationListener {
        durationInvalid = true
        requestArrangement()
    }
    private val weakInvalidDurationListener = WeakInvalidationListener(invalidDurationListener)

    private inner class Arrangement(private val arrangementWidth: StoryPointLabel.() -> Double) {

        /**
         * Collects the time ranges for each label and associates them.  May update the duration covered by the label
         * if needed.  This happens on the gui thread so that the durations will not be updated by something else on the
         * gui thread unexpectedly.
         */
        suspend fun associateLabelsToCoveredTimeRanges(): List<Pair<StoryPointLabel, TimeRange>> {
            return withContext(guiContext) {
                sortedLabels.asSequence()
                    .updateEachDurationIfNeeded()
                    .map { it to it.timeRange }
                    .toList()
            }
        }

        private fun Sequence<StoryPointLabel>.updateEachDurationIfNeeded(): Sequence<StoryPointLabel> {
            if (durationInvalid) {
                durationInvalid = false
                return onEach {
                    it.coveredDuration = scale.invoke(Pixels(arrangementWidth(it) + LABEL_SPACING)) + 1
                }
            }
            return this
        }

        fun arrangeLabelsByTimeAndRow(labelsByRange: List<Pair<StoryPointLabel, TimeRange>>): List<Pair<StoryPointLabel, Int>> {
            val occupiedCells = OccupiedCells()
            return labelsByRange.mapNotNull { (label, timeRange) ->
                // memory optimization.
                // Labels are sorted, so any time that happened before this label is no longer relevant
                occupiedCells.dropTimeBefore(timeRange.start)

                // find first row in the label's time "column" that is not already occupied
                val row = allIntegers.first(occupiedCells.notOccupiedAt(timeRange.start))

                // for each time "column" the label occupies, register it so other labels don't get placed there
                occupiedCells.occupyRow(row)(timeRange)

                if (label.row == row) null
                else label to row
            }
        }

        suspend fun updateLabelRows(updates: List<Pair<StoryPointLabel, Int>>) {
            withContext(guiContext) {
                updates.forEach { (label, row) -> label.row = row }
                invalidVisibleLabelsListener.invalidated(visibleLabels)
            }
        }

    }


    //**************************************************************************
    //*                                                                        *
    //* endregion Arrangement                                                  *
    //*                                                                        *
    //* region Visibility                                                      *
    //*                                                                        *
    //**************************************************************************

    private val visibleLabels = ReadOnlyObjectWrapper<List<StoryPointLabel>>(listOf())
    fun visibleLabels(): ReadOnlyObjectProperty<List<StoryPointLabel>> = visibleLabels.readOnlyProperty
    private val invalidVisibleLabelsListener = InvalidationListener {
        visibleLabels.set(calculateVisibleLabels())
    }

    private fun calculateVisibleLabels(): List<StoryPointLabel> {
        return sortedLabels.binarySubList(
            predicate = shouldLabelBeVisible(offsetX + width, arrangementWidthBinding.value!!),
            lookForward = { it.layoutX > offsetX.value }
        )
    }

    //**************************************************************************
    //*                                                                        *
    //* endregion Visibility                                                   *
    //*                                                                        *
    //* region Measurement                                                     *
    //*                                                                        *
    //**************************************************************************

    private val measurementQueue = Channel<Unit>(1, BufferOverflow.DROP_OLDEST)
    private fun requestMeasurement() {
        launch { measurementQueue.send(Unit) }
    }

    @Suppress("unused")
    private val measurementJob = launch {
        for (unit in measurementQueue) {
            measureLabels()
        }
    }

    private val dummyLabel: StoryPointLabel = gui.StoryPointLabel(StoryEvent.Id(), "", UnitOfTime(0)).apply {
        isVisible = false
        isManaged = false
    }

    private val measuredCountProperty = ReadOnlyIntegerWrapper(0)
    fun measuredCount(): ReadOnlyIntegerProperty = measuredCountProperty.readOnlyProperty

    private val measuringProperty = ReadOnlyBooleanWrapper(false)
    fun measuring(): ReadOnlyBooleanProperty = measuringProperty.readOnlyProperty

    private suspend fun measureLabels() {
        withContext(guiContext) {
            measuringProperty.set(true)
        }
        val start = Date().time
        var atLeastOne = false
        val numberMeasured = sortedLabels.asSequence().asFlow()
            .filter { it.cachedWidth == INVALID_CACHE }
            // might not actually be able to process any,
            // so the returned count might be 0, even if
            // there are still labels to measure.  This
            // guarantees that, if at least one needs
            // to measured, we'll try again later
            .onEach { atLeastOne = true }
            .flowOn(asyncContext) // ^ makes filter and onEach happen on current context
            .onEach(measureLabel(dummyLabel))
            .flowOn(guiContext) // makes measureLabel happen on gui thread ^
            .takeWhile { Date().time - start < 50 }
            .count() // just terminate the sequence somehow
        if (atLeastOne) {
            requestMeasurement()
        } else {
            withContext(guiContext) {
                measuringProperty.set(false)
            }
        }
        if (numberMeasured > 0) {
            withContext(guiContext) {
                measuredCountProperty.set(measuredCountProperty.get() + numberMeasured)
            }
        }
    }

    //**************************************************************************
    //*                                                                        *
    //* endregion Measurement                                                  *
    //*                                                                        *
    //* region Height                                                          *
    //*                                                                        *
    //**************************************************************************

    private var _tallestLabel = -1.0
        set(value) {
            field = value
            if (field != value) {
                requestLayout()
            }
        }

    private fun calculateTallestLabel(): Double {
        return sortedLabels.asSequence()
            .map { it.layoutY + it.height }
            .maxOrNull() ?: 0.0
    }

    override fun computePrefHeight(width: Double): Double {
        if (_tallestLabel != -1.0) return insets.top + insets.bottom + _tallestLabel
        return max(height, computeMinHeight(width)).also { requestMinHeight() }
    }

    override fun computeMinHeight(width: Double): Double {
        return insets.top + insets.bottom + if (sortedLabels.isEmpty()) 0.0 else ROW_HEIGHT
    }

    private val minHeightQueue = Channel<Unit>(1, BufferOverflow.DROP_OLDEST)
    private fun requestMinHeight() {
        launch { minHeightQueue.send(Unit) }
    }

    @Suppress("unused")
    private val minHeightJob = launch {
        for (unit in minHeightQueue) {
            val tallest = calculateTallestLabel()
            withContext(guiContext) { _tallestLabel = tallest }
        }
    }

    //**************************************************************************
    //*                                                                        *
    //* endregion Height                                                       *
    //*                                                                        *
    //**************************************************************************

    private val cachedHeightChangeListener = InvalidationListener {
        requestMinHeight()
    }
    private val weakCachedHeightChangeListener = WeakInvalidationListener(cachedHeightChangeListener)

    private fun initializeAddedLabel(label: StoryPointLabel) {
        label.collapsed().bind(labelsCollapsedProperty)
        label.asSurface {
            inheritedElevation = Elevation[4]!!
            relativeElevation = Elevation[4]!!
        }
        label.layoutXProperty().bind(scaleProperty.doubleBinding(label.time()) { scale(UnitOfTime(label.time)).value })
        label.cachedWidth().addListener(weakInvalidDurationListener)
        // the assumption is that all labels will have invalid caches when first added, but if not, their durations
        // will need to be calculated
        if (label.cachedWidth != INVALID_CACHE) weakInvalidDurationListener.invalidated(label.cachedWidth())
        label.viewOrderProperty().cleanBind(label.time().doubleBinding { it?.value?.toDouble() ?: 0.0 })

        label.layoutYProperty().bind(label.row() * ROW_HEIGHT + ROW_V_PADDING)
        label.layoutYProperty().addListener(weakCachedHeightChangeListener)
        label.heightProperty().addListener(weakCachedHeightChangeListener)
    }

    private fun unbindRemovedLabel(label: StoryPointLabel) {
        label.collapsed().unbind()
        label.cachedWidth().removeListener(weakInvalidDurationListener)
        label.viewOrderProperty().unbind()
        label.layoutXProperty().unbind()
        label.layoutYProperty().unbind()
        label.layoutYProperty().removeListener(weakCachedHeightChangeListener)
        label.heightProperty().removeListener(weakCachedHeightChangeListener)
    }

    private val itemChangedListener = ListChangeListener<StoryPointLabel> {
        while (it.next()) {
            if (it.wasAdded()) {
                it.addedSubList.forEach(::initializeAddedLabel)
                requestMeasurement()
                requestArrangement()
            }
            if (it.wasRemoved()) {
                it.removed.forEach(::unbindRemovedLabel)
                requestArrangement()
            }
            if (it.wasPermutated()) {
                requestArrangement()
            }
        }
    }
    private val weakListChangeListener = WeakListChangeListener(itemChangedListener)

    private val arrangementWidthBinding = objectBinding(labelsCollapsedProperty) {
        arrangementWidth(areLabelsCollapsed)
    }

    // region apply listeners
    init {
        sortedLabels.addListener(weakListChangeListener)
        arrangementWidthBinding.addListener(invalidVisibleLabelsListener)
        offsetXProperty.addListener(invalidVisibleLabelsListener)
        widthProperty().addListener(invalidVisibleLabelsListener)
        scaleProperty.addListener(weakInvalidDurationListener)
        labelsCollapsedProperty.addListener(weakInvalidDurationListener)
    }
    // endregion apply listeners

    // region labelGroup
    /**
     * Moves the labels by the provided [offsetX] and [offsetY].  Parent to all [visibleLabels]
     */
    private val labelGroup = group {}
    init {
        labelGroup.dynamicContent(visibleLabels) { labelGroup.children.setAll(it.orEmpty()) }
        labelGroup.layoutXProperty().bind(offsetXProperty.doubleBinding { -(it?.value ?: 0.0) })
        labelGroup.layoutYProperty().bind(offsetYProperty.doubleBinding { -(it?.value ?: 0.0) })
    }
    // endregion labelGroup

    init {
        minHeight = USE_COMPUTED_SIZE
        widthProperty().onChangeUntil({ (it?.toDouble() ?: 0.0) > 0.0 }) {
            if ((it?.toDouble() ?: 0.0) > 0.0) {
                add(dummyLabel)
                requestArrangement()
                requestMeasurement()
            }
        }
        prefHeight = USE_COMPUTED_SIZE
        prefWidth = USE_COMPUTED_SIZE
        isFocusTraversable = true
    }

    private class OccupiedCells {

        private val cells: MutableMap<UnitOfTime, MutableSet<Int>> = TreeMap<UnitOfTime, MutableSet<Int>>()

        fun notOccupiedAt(time: UnitOfTime): (Int) -> Boolean {
            val rowsInColumn = cells[time].orEmpty()
            return { row ->
                row !in rowsInColumn
            }
        }

        fun dropTimeBefore(time: UnitOfTime) {
            cells.keys.takeWhile { it < time }.forEach(cells::remove)
        }

        fun occupyRow(row: Int): (TimeRange) -> Unit {
            return { range ->
                range.forEach { time ->
                    cells.getOrPut(time, ::mutableSetOf).add(row)
                }
            }
        }

    }

    companion object {

        private val allIntegers: Sequence<Int>
            get() = generateSequence(0) { it + 1 }
    }

}