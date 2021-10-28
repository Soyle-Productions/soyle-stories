package com.soyle.stories.storyevent.timeline.viewport.ruler.label

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.storyevent.timeline.TimelineStyles
import com.soyle.stories.storyevent.timeline.UnitOfTime
import javafx.beans.binding.BooleanExpression
import javafx.beans.property.LongProperty
import javafx.collections.ObservableSet
import javafx.event.EventTarget
import javafx.scene.layout.Region
import tornadofx.*

interface TimeSpanLabelComponent {

    fun TimeSpanLabel(selection: ObservableSet<UnitOfTime>): TimeSpanLabel

    @ViewBuilder
    fun EventTarget.timeSpanLabel(
        selection: ObservableSet<UnitOfTime>,
        op: TimeSpanLabel.() -> Unit = {}): TimeSpanLabel = TimeSpanLabel(selection)
        .also { add(it) }
        .apply(op)

    companion object {
        fun Implementation(

        ) = object : TimeSpanLabelComponent {
            override fun TimeSpanLabel(selection: ObservableSet<UnitOfTime>): TimeSpanLabel {
                return com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabel(selection)
            }
        }
    }

}

class TimeSpanLabel(
    private val selection: ObservableSet<UnitOfTime> = observableSetOf()
) : Region() {

    private val numberProperty: LongProperty = longProperty(0)
    fun number(): LongProperty = numberProperty
    var number: Long by number()

    private val truncatedAboveThousand = longBinding(numberProperty) {
        get() % 1000
    }

    private val selectedProperty = booleanBinding(selection, numberProperty) { selection.contains(UnitOfTime(number)) }
    fun selected(): BooleanExpression = selectedProperty
    var selected: Boolean
        get() = selectedProperty.get()
        set(value) {
            if (value) selection.add(UnitOfTime(number))
            else selection.remove(UnitOfTime(number))
        }

    private val label = label {
        textProperty().bind(truncatedAboveThousand.asString("%d"))
    }

    init {
        addClass(TimelineStyles.timeLabel)
        toggleClass(Stylesheet.selected, selected())
        asSurface {
            relativeElevation = Elevation[2]!!
        }
        minHeight = Region.USE_PREF_SIZE
        setOnMouseClicked {
            selected = true
        }
    }

    override fun toString(): String {
        return super.toString()+"\'${label.text}\'"
    }
}