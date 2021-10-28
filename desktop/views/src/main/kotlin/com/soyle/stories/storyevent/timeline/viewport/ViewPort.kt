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
import javafx.beans.binding.DoubleExpression
import javafx.beans.binding.LongExpression
import javafx.beans.property.*
import javafx.beans.value.WeakChangeListener
import javafx.collections.ListChangeListener
import javafx.collections.ObservableList
import javafx.collections.WeakListChangeListener
import javafx.scene.control.Control
import javafx.scene.control.Skin
import javafx.scene.input.KeyCode
import javafx.scene.input.KeyEvent
import javafx.scene.input.MouseEvent
import javafx.scene.input.ScrollEvent
import tornadofx.*
import tornadofx.Stylesheet.Companion.viewport
import kotlin.math.max

class TimelineViewPort(
    val storyEventItems: ObservableList<StoryPointLabel>,

    private val gui: TimelineViewPortComponent.Gui,
    private val dependencies: TimelineViewPortComponent.Dependencies
) : Control() {

    private val scaleProperty = object : ReadOnlyObjectWrapper<Scale>(Scale.maxZoomIn()) {
        override fun invalidated() {
            calculateMaxOffset()
            super.invalidated()
        }
    }

    fun scale(): ReadOnlyObjectProperty<Scale> = scaleProperty.readOnlyProperty
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

    fun offsetX(): ReadOnlyObjectProperty<Pixels> = offsetXProperty.readOnlyProperty
    val offsetX: Pixels by offsetX()

    private val offsetYProperty = ReadOnlyObjectWrapper(Pixels(0.0))
    fun offsetY(): ReadOnlyObjectProperty<Pixels> = offsetYProperty.readOnlyProperty
    val offsetY: Pixels by offsetY()

    private val selectionProperty = objectProperty<TimelineSelectionModel>(TimelineSelectionModel())
    fun selection(): ObjectProperty<TimelineSelectionModel> = selectionProperty
    var selection by selectionProperty

    /**
     * Whether all the labels should be collapsed or not
     */
    private val labelsCollapsedProperty = booleanProperty(false)

    /** @see labelsCollapsedProperty */
    fun labelsCollapsed() = labelsCollapsedProperty

    /** @see labelsCollapsedProperty */
    var areLabelsCollapsed: Boolean by labelsCollapsed()

    private val maxRightBoundsProperty = doubleProperty(0.0)
    private val maxOffsetXProperty = doubleBinding(maxRightBoundsProperty, widthProperty(), offsetXProperty) {
        max((maxRightBoundsProperty.get() - widthProperty().get()).coerceAtLeast(0.0), offsetX.value)
    }

    private fun calculateMaxOffset() {
        maxRightBoundsProperty.set(storyEventItems.maxOfOrNull { scale(UnitOfTime(it.time)).value + it.cachedWidth }
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
    private val storyEventItemChanged = ListChangeListener<StoryPointLabel> { change ->
        calculateMaxOffset()
        while (change.next()) {
            if (change.wasAdded()) {
                change.addedSubList.forEach {
                    it.cachedWidth().addListener(weakStoryEventItemWidthChanged)
                    it.time().addListener(weakStoryEventItemTimeChanged)
                }
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
        storyEventItems.addListener(weakStoryEventItemChanged)
    }

    fun maxOffsetX(): DoubleExpression = maxOffsetXProperty

    private val visibleRangeProperty = offsetXProperty.objectBinding(widthProperty(), scaleProperty) {
        scale(offsetX)..scale(offsetX + width)
    }

    fun visibleRange(): Binding<TimeRange?> = visibleRangeProperty

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
    fun mouseClicked(mouseEvent: MouseEvent)
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

    override fun mouseClicked(mouseEvent: MouseEvent) {
        viewModel.selection.storyEvents.clear()
        setFocus(true)
    }

    override fun keyPressed(keyEvent: KeyEvent) {
        when (keyEvent.code) {
            KeyCode.DELETE, KeyCode.BACK_SPACE -> deleteSelection()
        }
    }

    private fun deleteSelection() {
        if (! viewModel.selection.empty().value) {
            dependencies.removeStoryEventController.removeStoryEvent(viewModel.selection.storyEvents.selectedIds)
        }
    }
}