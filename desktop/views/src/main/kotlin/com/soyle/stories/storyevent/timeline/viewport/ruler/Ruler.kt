package com.soyle.stories.storyevent.timeline.viewport.ruler

import com.soyle.stories.common.ViewBuilder
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.Surface.Companion.asSurface
import com.soyle.stories.common.existsWhen
import com.soyle.stories.storyevent.timeline.*
import com.soyle.stories.storyevent.timeline.TimelineStyles.Companion.incrementalLabelHolder
import com.soyle.stories.storyevent.timeline.TimelineStyles.Companion.largeMagnitudeLabelHolder
import com.soyle.stories.storyevent.timeline.TimelineStyles.Companion.rulerSpacing
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabel
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabelComponent
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.menu.TimelineRulerLabelMenuComponent
import com.soyle.stories.storyevent.timeline.viewport.ruler.selection.SelectionRegion
import javafx.beans.binding.Bindings.createObjectBinding
import javafx.beans.binding.BooleanExpression
import javafx.beans.binding.DoubleExpression
import javafx.beans.binding.ObjectBinding
import javafx.beans.binding.ObjectExpression
import javafx.beans.property.LongProperty
import javafx.beans.property.ObjectProperty
import javafx.collections.ObservableSet
import javafx.event.EventTarget
import javafx.scene.control.Control
import javafx.scene.control.Label
import javafx.scene.control.Skin
import javafx.scene.control.SkinBase
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import javafx.scene.layout.VBox
import tornadofx.*
import java.text.NumberFormat
import java.util.*

@Suppress("FunctionName")
interface TimelineRulerComponent {

    @ViewBuilder
    fun EventTarget.timelineRuler(
        selection: TimeRangeSelection,
        op: TimelineRuler.() -> Unit = {}
    ): TimelineRuler = TimelineRuler(selection)
        .also { add(it) }
        .apply(op)

    fun TimelineRuler(selection: TimeRangeSelection): TimelineRuler

    interface Gui : TimeSpanLabelComponent, TimelineRulerLabelMenuComponent

    companion object {
        fun Implementation(
            gui: Gui
        ) = object : TimelineRulerComponent {
            override fun TimelineRuler(selection: TimeRangeSelection): TimelineRuler {
                return TimelineRuler(selection, gui)
            }
        }
    }
}

class TimelineRuler(
    private val selection: TimeRangeSelection = TimelineSelectionModel(),
    private val gui: TimelineRulerComponent.Gui,
) : Control() {

    private val visibleRangeProperty = objectProperty(UnitOfTime(0)..UnitOfTime(1))
    fun visibleRange(): ObjectProperty<TimeRange> = visibleRangeProperty
    var visibleRange: TimeRange by visibleRangeProperty

    private val scaleProperty: ObjectProperty<Scale> = objectProperty(Scale.maxZoomIn())
    fun scale(): ObjectProperty<Scale> = scaleProperty
    var scale: Scale by scaleProperty

    private val offsetXProperty = objectProperty(Pixels(0.0))
    fun offsetX(): ObjectProperty<Pixels> = offsetXProperty
    var offsetX: Pixels by offsetXProperty

    private val visibleOver1KProperty = booleanBinding(visibleRangeProperty) { visibleRange.endInclusive >= 1_000 }
    fun isVisibleOver1K(): BooleanExpression = visibleOver1KProperty

    private val labelStepProperty = createObjectBinding({
        val requiredLabelWidth = rulerSpacing + (2 * TimelineStyles.rulerTimeLabelPadding) + 32
        labelStepOptions.first { scale(UnitOfTime(it)).value >= requiredLabelWidth }
            .let(::UnitOfTime)
    }, scaleProperty)

    fun labelStep(): ObjectExpression<UnitOfTime> = labelStepProperty
    private val labelStep: UnitOfTime by labelStepProperty

    private val labelWidthProperty = labelStepProperty.doubleBinding {
        it ?: return@doubleBinding 0.0
        scale.invoke(it).value - rulerSpacing
    }

    fun labelWidth(): DoubleExpression = labelWidthProperty

    private val labelMenu = gui.TimelineRulerLabelMenu(selection)

    private val labelsProperty = createObjectBinding({
        if (width <= 0.0) return@createObjectBinding emptyList()

        val firstVisibleUnit = visibleRange.start
        val finalVisibleUnit = visibleRange.endInclusive
        val firstLabelValue = (firstVisibleUnit / labelStep) * labelStep
        val finalLabelValue = (finalVisibleUnit / labelStep) * labelStep

        (firstLabelValue.value..finalLabelValue.value step labelStep.value).map {
            gui.TimeSpanLabel(selection).apply {
                range = TimeRange(it .. (labelStep + it).value)
                contextMenu = labelMenu
                minWidthProperty().bind(labelWidth())
                prefWidthProperty().bind(labelWidth())
            }
        }
    }, visibleRangeProperty, labelStepProperty, labelWidthProperty)

    fun labels(): ObjectBinding<List<TimeSpanLabel>> = labelsProperty
    private val labels: List<TimeSpanLabel> by labels()

    private val secondaryLabelsProperty = createObjectBinding({
        if (visibleRange.endInclusive >= 1000L) {
            val firstFlooredThousand = (visibleRange.start.value / 1000) * 1000
            val lastFlooredThousand = (visibleRange.endInclusive.value / 1000) * 1000
            (firstFlooredThousand..lastFlooredThousand step 1000)
                .asSequence()
                .filter { it >= 1000 }
                .map { number ->
                    val text = NumberFormat.getNumberInstance(Locale.US).format(number)
                    Label(text).apply {
                        val minX = scale(UnitOfTime(number)).value - offsetX.value
                        val width = scale(UnitOfTime(1000)) - rulerSpacing
                        val maxX = minX + width.value
                        layoutX = minX.coerceAtLeast(0.0)
                        prefWidth = maxX.coerceAtMost(this@TimelineRuler.width) - layoutX
                        minWidth = prefWidth
                        maxWidth = prefWidth
                    }
                }
                .toList()
        } else listOf<Label>()
    }, visibleRangeProperty)

    fun secondaryLabels(): ObjectBinding<List<Label>> = secondaryLabelsProperty

    private val relativeOffsetXProperty = offsetXProperty.doubleBinding(labelsProperty) {
        val firstLabel = labels.firstOrNull() ?: return@doubleBinding 0.0
        scale(firstLabel.range.start).value - offsetX.value
    }

    fun relativeOffsetX() = relativeOffsetXProperty

    private val selectionRegionsProperty = createObjectBinding({
        if (selection.isEmpty()) emptyList<Region>()
        else selection.asSequence()
            .filter { it.start <= visibleRange.endInclusive && it.endInclusive >= visibleRange.start }
            .map { SelectionRegion(it, scale) }
            .toList()
    }, selection, scaleProperty, visibleRangeProperty)
    fun selectionRegions(): ObjectExpression<List<Region>> = selectionRegionsProperty
    val selectionRegions: List<Region>
        get() = selectionRegionsProperty.get()

    override fun createDefaultSkin(): Skin<*> = TimelineRulerSkin(this, gui)

    init {
        addClass(TimelineStyles.ruler)
    }

    companion object {
        @JvmStatic
        private val labelStepOptions = listOf<Long>(
            1,
            5,
            10,
            20,
            50
        )
    }

}

class TimelineRulerSkin(ruler: TimelineRuler, gui: TimelineRulerComponent.Gui) : SkinBase<TimelineRuler>(ruler) {

    private val root = VBox().apply {
        with(gui) {
            pane {
                addClass(largeMagnitudeLabelHolder)
                existsWhen(ruler.isVisibleOver1K())
                dynamicContent(ruler.secondaryLabels()) { labels ->
                    children.setAll(labels.orEmpty().onEach { label ->
                        label.asSurface {
                            inheritedElevation = Elevation[4]!!
                            relativeElevation = Elevation.getValue(0)
                        }
                    })
                }
            }
            stackpane {
                hbox {
                    addClass(incrementalLabelHolder)
                    paddingLeftProperty.bind(ruler.relativeOffsetX())
                    dynamicContent(ruler.labels()) { labels ->
                        children.setAll(labels.orEmpty().onEach { label ->
                            label.asSurface { inheritedElevation = Elevation[4]!! }
                        })
                    }
                }
                hbox {
                    isPickOnBounds = false
                    paddingLeftProperty.bind(ruler.offsetX().doubleBinding { -(it?.value ?: 0.0) })
                    pane {
                        isPickOnBounds = false
                        dynamicContent(ruler.selectionRegions()) { regions ->
                            children.setAll(regions.orEmpty().onEach {

                            })
                        }
                    }
                }
            }
        }
    }

    init {
        children.add(root)
    }

}