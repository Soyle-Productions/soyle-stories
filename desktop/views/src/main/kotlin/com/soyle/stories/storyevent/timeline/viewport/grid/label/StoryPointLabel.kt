package com.soyle.stories.storyevent.timeline.viewport.grid.label

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.Notifier
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.elevation
import com.soyle.stories.common.listensTo
import com.soyle.stories.common.scopedListener
import com.soyle.stories.domain.storyevent.StoryEvent
import com.soyle.stories.storyevent.item.StoryEventItemSelection
import com.soyle.stories.storyevent.item.StoryEventItemViewModel
import com.soyle.stories.storyevent.item.icon.StoryEventItemIconStyles
import com.soyle.stories.storyevent.rename.StoryEventRenamedReceiver
import com.soyle.stories.storyevent.time.StoryEventRescheduledReceiver
import com.soyle.stories.storyevent.timeline.Pixels
import com.soyle.stories.storyevent.timeline.TimeRange
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.label.StoryPointLabelStyles.Companion.STORY_POINT_BORDER_RADIUS
import javafx.animation.Interpolator
import javafx.beans.InvalidationListener
import javafx.beans.WeakInvalidationListener
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.*
import javafx.beans.value.ObservableValue
import javafx.beans.value.WeakChangeListener
import javafx.geometry.Insets
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.*
import javafx.util.Duration
import kotlinx.coroutines.withContext
import tornadofx.*
import kotlin.coroutines.CoroutineContext

class StoryPointLabel

/**
 * Prefer not to use constructor directly.  Instead, use [StoryPointLabelComponent].StoryPointLabel
 */
constructor(
    override val storyEventId: StoryEvent.Id,
    guiComponent: StoryPointLabelComponent.GUIComponents,
) : Label(), StoryEventItemViewModel {

    companion object {
        const val INVALID_CACHE = -1.0
    }

    private val timeProperty = object : SimpleObjectProperty<UnitOfTime>(UnitOfTime(0)) {
        override fun invalidated() {
            timeRangeProperty.set(get()..get() + coveredDuration)
            super.invalidated()
        }
    }

    override val name: String
        get() = text

    fun time(): ObjectProperty<UnitOfTime> = timeProperty
    override var time: Long
        get() = timeProperty.get().value
        set(value) = timeProperty.set(UnitOfTime(value))

    private val cachedWidthProperty = object : SimpleDoubleProperty(INVALID_CACHE) {
        override fun invalidated() {
            isDisable = get() == INVALID_CACHE && !isCollapsed
            super.invalidated()
        }
    }

    fun cachedWidth(): DoubleProperty = cachedWidthProperty
    var cachedWidth: Double by cachedWidth()
    private val textUpdatedInvalidateCacheListener = ChangeListener<String> { o, old, new -> cachedWidth = INVALID_CACHE }
    private val textUpdatedInvalidateCacheListenerRef = WeakChangeListener(textUpdatedInvalidateCacheListener)

    init {
        isDisable = cachedWidth == INVALID_CACHE
        textProperty().addListener(textUpdatedInvalidateCacheListenerRef)
    }

    override fun setWidth(value: Double) {
        if (!isCollapsed && cachedWidth < value) cachedWidth = value
        super.setWidth(value)
    }

    private val rowProperty = intProperty(0)
    fun row(): IntegerProperty = rowProperty
    var row: Int by row()

    private val coveredDurationProperty = object : SimpleObjectProperty<UnitOfTime>(UnitOfTime(1)) {
        override fun invalidated() {
            timeRangeProperty.set(UnitOfTime(time)..UnitOfTime(time + get().value))
            super.invalidated()
        }
    }

    fun coveredDuration() = coveredDurationProperty
    var coveredDuration: UnitOfTime by coveredDurationProperty

    private val timeRangeProperty = ReadOnlyObjectWrapper<TimeRange>(UnitOfTime(time)..UnitOfTime(time + 1))
    fun timeRange(): ReadOnlyObjectProperty<TimeRange> = timeRangeProperty.readOnlyProperty
    val timeRange: TimeRange by timeRange()

    private val collapsedProperty = object : SimpleBooleanProperty(false) {
        override fun invalidated() {
            isDisable = cachedWidth == INVALID_CACHE && !get()
            super.invalidated()
        }
    }

    fun collapsed(): BooleanProperty = collapsedProperty
    var isCollapsed: Boolean by collapsed()

    private val emphasizedProperty = object : SimpleBooleanProperty(false) {
        override fun invalidated() {
            if (get()) {
                requestFocus()
                timeline(true) {
                    repeat(10) {
                        keyframe(Duration.millis(it * 50.0)) {
                            val border = Border(
                                BorderStroke(
                                    ColorStyles.secondaryColor.deriveColor(0.0, 1.0, 1.0, (it.toDouble() / 10) * 255),
                                    BorderStrokeStyle.SOLID,
                                    CornerRadii(0.0, STORY_POINT_BORDER_RADIUS.toDouble(), STORY_POINT_BORDER_RADIUS.toDouble(), 0.0, false),
                                    BorderWidths((it.toDouble() / 10) * 4),
                                    Insets(-(it.toDouble() / 10) * 4)
                                )
                            )
                            keyvalue(borderProperty(), border, Interpolator.EASE_IN)
                        }
                    }

                    cycleCount = 6
                    isAutoReverse = true
                    setOnFinished { isEmphasized = false }
                }
            }
            super.invalidated()
        }
    }

    fun emphasized(): BooleanExpression = emphasizedProperty
    var isEmphasized: Boolean by emphasizedProperty
        private set

    private val selectionProperty = object : SimpleObjectProperty<StoryEventItemSelection>(StoryEventItemSelection()) {
        private var old = get()
        override fun invalidated() {
            if (get()?.contains(storyEventId) == true) selectedProperty.set(true)
            else selectedProperty.set(false)
            old.removeListener(weakSelectionListener)
            get().addListener(weakSelectionListener)
            old = get()
            super.invalidated()
        }
    }

    fun selection(): ObjectProperty<StoryEventItemSelection> = selectionProperty

    private val selectedProperty = booleanProperty(false)
    fun selected(): BooleanExpression = selectedProperty
    val selected: Boolean by selected()

    private val selectionListener: InvalidationListener = InvalidationListener {
        selectedProperty.set(selectionProperty.get().contains(storyEventId))
    }
    private val weakSelectionListener = WeakInvalidationListener(selectionListener)
    init {
        selectionProperty.get().addListener(weakSelectionListener)
    }

    init {
        graphic = guiComponent.StoryEventItemIcon()
        addClass(StoryPointLabelStyles.storyPointLabel)
        toggleClass(Stylesheet.collapsed, collapsedProperty)
        toggleClass(Stylesheet.selected, selectedProperty)
        elevation = Elevation.getValue(8)
        isFocusTraversable = true
    }

    fun emphasize() {
        isEmphasized = true
    }

}