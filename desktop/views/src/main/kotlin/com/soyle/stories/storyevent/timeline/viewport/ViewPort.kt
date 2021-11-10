package com.soyle.stories.storyevent.timeline.viewport

import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.storyevent.item.StoryEventItemSelection
import com.soyle.stories.storyevent.item.StoryEventItemViewModel
import com.soyle.stories.storyevent.timeline.*
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabel
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.beans.binding.Binding
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.binding.DoubleExpression
import javafx.beans.binding.LongExpression
import javafx.beans.binding.ObjectExpression
import javafx.beans.property.*
import javafx.beans.value.WeakChangeListener
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import javafx.event.EventHandler
import javafx.event.WeakEventHandler
import javafx.scene.Node
import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.input.*
import tornadofx.*
import tornadofx.Stylesheet.Companion.viewport
import kotlin.math.max

class TimelineViewPort(
    override val storyPointLabels: ObservableList<StoryPointLabel>,

    private val gui: TimelineViewPortComponent.Gui,
    private val dependencies: TimelineViewPortComponent.Dependencies
) : Control(), TimelineViewportContext {

    private val scaleProperty = object : ReadOnlyObjectWrapper<Scale>(Scale.maxZoomIn()) {
        override fun invalidated() {
            calculateMaxOffset()
            super.invalidated()
        }
    }

    override fun scale(): ReadOnlyObjectProperty<Scale> = scaleProperty.readOnlyProperty
    val scale by scale()

    private val offsetXProperty = object : ReadOnlyObjectWrapper<Pixels>(Pixels(0.0)) {
        override fun invalidated() {
            calculateMaxOffset()
            super.invalidated()
        }

        override fun set(newValue: Pixels?) {
            if (newValue == null || newValue.value < 0.0) super.set(Pixels(0.0))
            else super.set(newValue)
        }
    }

    override fun offsetX(): ReadOnlyObjectProperty<Pixels> = offsetXProperty.readOnlyProperty
    val offsetX: Pixels by offsetX()

    private val offsetYProperty = ReadOnlyObjectWrapper(Pixels(0.0))
    override fun offsetY(): ReadOnlyObjectProperty<Pixels> = offsetYProperty.readOnlyProperty
    val offsetY: Pixels by offsetY()

    private val selectionProperty = objectProperty<TimelineSelectionModel>(TimelineSelectionModel())
    fun selection(): ObjectProperty<TimelineSelectionModel> = selectionProperty
    override var selection by selectionProperty

    /**
     * Whether all the labels should be collapsed or not
     */
    private val labelsCollapsedProperty = booleanProperty(false)

    /** @see labelsCollapsedProperty */
    override fun labelsCollapsed() = labelsCollapsedProperty

    /** @see labelsCollapsedProperty */
    var areLabelsCollapsed: Boolean by labelsCollapsed()

    private val dragLabelsProperty = objectProperty<List<StoryPointLabel>>(emptyList())
    override fun dragLabels(): ObjectExpression<List<StoryPointLabel>> = dragLabelsProperty
    val dragLabels: List<StoryPointLabel> get() = dragLabels().get()

    private val maxRightBoundsProperty = doubleProperty(0.0)
    private val maxOffsetXProperty =
        doubleBinding(maxRightBoundsProperty, widthProperty(), offsetXProperty, selection.timeRange, scaleProperty) {
            listOf(
                (maxRightBoundsProperty.get() - widthProperty().get() / 2).coerceAtLeast(0.0),
                offsetX.value,
                selection.timeRange.value?.let { scale(it.endInclusive + 1).value - widthProperty().get() }
                    ?.coerceAtLeast(0.0) ?: 0.0
            ).maxOfOrNull { it }!!
        }

    private fun calculateMaxOffset() {
        maxRightBoundsProperty.set(storyPointLabels.maxOfOrNull { scale(UnitOfTime(it.time)).value + it.cachedWidth }
            ?: 0.0)
    }

    private val storyEventItemWidthChanged = InvalidationListener {
        calculateMaxOffset()
    }
    private val weakStoryEventItemWidthChanged = WeakInvalidationListener(storyEventItemWidthChanged)
    private val storyEventItemTimeChanged = InvalidationListener {
        calculateMaxOffset()
    }
    private val weakStoryEventItemTimeChanged = WeakInvalidationListener(storyEventItemTimeChanged)

    private fun initializeAddedStoryPointLabel(label: StoryPointLabel) {
        label.cachedWidth().addListener(weakStoryEventItemWidthChanged)
        label.time().addListener(weakStoryEventItemTimeChanged)
        label.setOnMousePressed(actions::mousePressedOnLabel)
    }
    private val storyEventItemChanged = ListChangeListener<StoryPointLabel> { change ->
        calculateMaxOffset()
        while (change.next()) {
            if (change.wasAdded()) {
                change.addedSubList.forEach(::initializeAddedStoryPointLabel)
            }
            if (change.wasRemoved()) {
                change.removed.forEach {
                    it.cachedWidth().removeListener(weakStoryEventItemWidthChanged)
                    it.time().removeListener(weakStoryEventItemTimeChanged)
                }
            }
        }
    }
    private val weakStoryEventItemChanged = WeakListChangeListener(storyEventItemChanged)

    init {
        storyPointLabels.addListener(weakStoryEventItemChanged)
    }

    fun maxOffsetX(): DoubleExpression = maxOffsetXProperty

    private val visibleRangeProperty = createObjectBinding({
        scale(offsetX)..scale(offsetX + width)
    }, offsetXProperty, widthProperty(), scaleProperty)

    override fun visibleRange(): ObjectExpression<TimeRange> = visibleRangeProperty

    private var dragStartTime: UnitOfTime? = null

    private val dragDetectedListener = EventHandler<MouseEvent> {
        if (it.target !is StoryPointLabel && (it.target as? Node)?.findParent<StoryPointLabel>() == null) return@EventHandler
        dragStartTime = scale(Pixels(it.x + offsetX.value))
        val sources = storyPointLabels.filter { it.selected }
            .onEach { if (! it.hasClass(TimelineStyles.dragging)) it.addClass(TimelineStyles.dragging) }
            .map {
                gui.StoryPointLabel(it.storyEventId, it.name, it.time.unit)
                    .apply {
                        row = it.row
                        resize(it.width, it.height)
                        properties["origin"] = it
                        layoutXProperty().bind(scaleProperty.doubleBinding(time()) { scale(UnitOfTime(time)).value })
                        asSurface {
                            inheritedElevation = Elevation.getValue(8)
                            relativeElevation = Elevation.getValue(6)
                        }
                    }
            }

        dragLabelsProperty.set(sources)
    }
    private val mouseDraggedListener = EventHandler<MouseEvent> { event ->
        val dragStartTime = dragStartTime ?: return@EventHandler
        val adjustment = scale(Pixels(event.x) + offsetX) - dragStartTime
        dragLabels.forEach { it.time = (it.properties["origin"] as StoryPointLabel).time + adjustment.value }

        if (event.x > width - 10) {
            offsetXProperty.set(offsetX + event.x - (width - 10))
        } else if (event.x < 10) {
            offsetXProperty.set(offsetX + (event.x - 10))
        }
    }
    private val mouseDragReleaseListener = EventHandler<MouseEvent> { event ->
        val dragStartTime = dragStartTime ?: return@EventHandler
        val dragLabels = dragLabels.takeUnless { it.isEmpty() } ?: return@EventHandler
        val adjustment = dragLabels.first().let { it.time - (it.properties["origin"] as StoryPointLabel).time }
        val originLabels= dragLabels.map { it.properties["origin"] as StoryPointLabel }
        originLabels.onEach { it.removeClass(TimelineStyles.dragging) }

        dependencies.adjustStoryEventsTimeController.adjustTimesBy(
            dragLabels.map { it.storyEventId }.toSet(),
            adjustment,
            confirmation = true
        )
        dragLabelsProperty.set(emptyList())
    }

    private val actions: TimelineViewPortActions = TimelineViewPortPresenter(this, dependencies)

    override fun createDefaultSkin(): Skin<*> = TimelineViewPortView(this, actions, gui)

    init {
        addClass(viewport)
        asSurface {
            absoluteElevation = Elevation.getValue(4)
        }
        prefHeight = USE_COMPUTED_SIZE
        prefWidth = USE_COMPUTED_SIZE
        isFocusTraversable = true
        setOnKeyPressed(actions::keyPressed)

        addEventFilter(MouseEvent.DRAG_DETECTED, dragDetectedListener)
        addEventFilter(MouseEvent.MOUSE_DRAGGED, mouseDraggedListener)
        addEventFilter(MouseDragEvent.MOUSE_RELEASED, mouseDragReleaseListener)

        storyPointLabels.forEach(::initializeAddedStoryPointLabel)
    }

    fun scrollToTime(time: UnitOfTime) {
        offsetXProperty.set(scale(time))
    }

    fun scrollToLabel(label: StoryPointLabel) {
        scrollToTime(UnitOfTime(label.time))
        label.emphasize()
    }

    open class Presenter(protected val viewModel: TimelineViewPort) {
        protected val offsetXProperty = viewModel.offsetXProperty
        protected val offsetYProperty = viewModel.offsetYProperty
        protected val scaleProperty = viewModel.scaleProperty
        protected fun setFocus(focus: Boolean) {
            viewModel.isFocused = focus
        }
    }

}

interface TimelineViewPortActions {
    fun offsetX(): ObjectProperty<Pixels>
    fun offsetY(): ObjectProperty<Pixels>
    fun scroll(scrollEvent: ScrollEvent)
    fun mousePressed(mouseEvent: MouseEvent)
    fun mousePressedOnLabel(mouseEvent: MouseEvent)
    fun maxOffsetY(): DoubleProperty
    fun hScroll(value: Double)
    fun vScroll(value: Double)
    fun keyPressed(keyEvent: KeyEvent)
}

private class TimelineViewPortPresenter(
    viewModel: TimelineViewPort,
    private val dependencies: TimelineViewPortComponent.Dependencies
) :
    TimelineViewPort.Presenter(viewModel),
    TimelineViewPortActions {

    override fun offsetX(): ObjectProperty<Pixels> = offsetXProperty
    override fun offsetY(): ObjectProperty<Pixels> = offsetYProperty

    private val maxOffsetYProperty = object : SimpleDoubleProperty(0.0) {
        override fun invalidated() {
            offsetYProperty.set(Pixels(offsetYProperty.get().value.coerceAtMost(get())))
            super.invalidated()
        }
    }

    override fun maxOffsetY(): DoubleProperty = maxOffsetYProperty

    override fun scroll(scrollEvent: ScrollEvent) {
        if (scrollEvent.isControlDown) {
            val focalPoint = scaleProperty.get().invoke(offsetX().get() + scrollEvent.x)
            val newScale = scaleProperty.get().zoomed(scrollEvent.deltaY)
            val newOffsetX = newScale(focalPoint) - scrollEvent.x
            scaleProperty.set(newScale)
            offsetXProperty.set(newOffsetX)
            return
        }
        vScroll(offsetYProperty.value.value - scrollEvent.deltaY)
        hScroll(offsetXProperty.value.value - scrollEvent.deltaX)
    }

    override fun hScroll(value: Double) {
        offsetXProperty.value = Pixels(value.coerceAtLeast(0.0))
    }

    override fun vScroll(value: Double) {
        offsetYProperty.value = Pixels(value.coerceIn(0.0, maxOffsetYProperty.value))
    }

    override fun mousePressedOnLabel(mouseEvent: MouseEvent) {
        val sourceAsPointLabel = mouseEvent.source as? StoryPointLabel
        if (! mouseEvent.isShiftDown && sourceAsPointLabel?.selected != true) {
            viewModel.selection.clear()
        }
        sourceAsPointLabel?.let(viewModel.selection.storyEvents::add)
        if (sourceAsPointLabel != null) mouseEvent.consume()
    }

    override fun mousePressed(mouseEvent: MouseEvent) {
        if (! mouseEvent.isShiftDown) {
            viewModel.selection.clear()
        }
    }

    override fun keyPressed(keyEvent: KeyEvent) {
        when (keyEvent.code) {
            KeyCode.DELETE, KeyCode.BACK_SPACE -> deleteSelection()
        }
    }

    private fun deleteSelection() {
        if (!viewModel.selection.empty().value) {
            dependencies.removeStoryEventController.removeStoryEvent(viewModel.selection.storyEvents.selectedIds)
        }
    }
}