package com.soyle.stories.storyevent.timeline.viewport

import com.soyle.stories.storyevent.timeline.TimelineStyles.Companion.RULER_PADDING
import com.soyle.stories.storyevent.timeline.TimelineStyles.Companion.backgroundLine
import com.soyle.stories.storyevent.timeline.TimelineStyles.Companion.unlabeled
import com.soyle.stories.storyevent.timeline.UnitOfTime
import com.soyle.stories.storyevent.timeline.viewport.grid.TimelineViewPortGrid
import com.soyle.stories.storyevent.timeline.viewport.ruler.TimelineRuler
import com.soyle.stories.storyevent.timeline.viewport.ruler.label.TimeSpanLabel
import javafx.geometry.HPos
import javafx.geometry.Insets
import javafx.geometry.Orientation
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.control.ScrollBar
import javafx.scene.control.SkinBase
import javafx.scene.input.MouseEvent
import javafx.scene.layout.*
import javafx.scene.shape.Line
import tornadofx.*
import java.lang.Double.isNaN

class TimelineViewPortView(
    viewPort: TimelineViewPort,
    actions: TimelineViewPortActions,

    gui: TimelineViewPortComponent.Gui
) : SkinBase<TimelineViewPort>(viewPort) {

    private val backgroundLines = Pane()

    private val ruler: TimelineRuler = gui.TimelineRuler(viewPort)
        .apply {
            setOnScroll(actions::scroll)
        }

    private var dragTarget: TimeSpanLabel? = null

    init {
        viewPort.addEventFilter(MouseEvent.DRAG_DETECTED) {
            val target = it.target as? TimeSpanLabel ?: (it.target as? Node)?.findParent<TimeSpanLabel>()
            if (it.isPrimaryButtonDown && target != null) {
                dragTarget = target
            }
        }
        viewPort.addEventFilter(MouseEvent.MOUSE_RELEASED) {
            dragTarget = null
        }
        viewPort.addEventFilter(MouseEvent.MOUSE_DRAGGED) {
            dragTarget ?: return@addEventFilter
            if (it.isPrimaryButtonDown) {
                it.consume()
                val coordsInScene = viewPort.localToScene(it.x, it.y)
                val draggedToNode = it.pickResult?.intersectedNode
                if (draggedToNode is TimeSpanLabel) {
                    viewPort.selection.extendFromRecentStart(draggedToNode.range)
                } else {
                    val intersectedLabel = ruler.labels().get().find { otherLabel ->
                        val relativeCoords = otherLabel.sceneToLocal(coordsInScene)
                        otherLabel.contains(relativeCoords.x, 0.0)
                    }
                    if (intersectedLabel != null) {
                        viewPort.selection.extendFromRecentStart(intersectedLabel.range)
                    }
                }

                if (viewPort.width > 10 && it.x > (viewPort.width - 10)) actions.hScroll(viewPort.offsetX.value + (it.x - (viewPort.width - 10)))
                else if (it.x < 0)  actions.hScroll((viewPort.offsetX.value + it.x))
            }
        }
    }

    private val grid: TimelineViewPortGrid = gui.TimelineViewPortGrid(viewPort).apply {
        setOnScroll(actions::scroll)
        setOnMousePressed(actions::mousePressed)
    }

    private val vbar: ScrollBar = ScrollBar().apply {
        orientation = Orientation.VERTICAL
        viewPort.offsetY().onChange { value = it?.value ?: 0.0 }
        valueProperty().onChange { actions.vScroll(value) }
    }

    private val hbar: ScrollBar = ScrollBar().apply {
        orientation = Orientation.HORIZONTAL
        maxProperty().bind(viewPort.maxOffsetX())
        valueProperty().onChange { actions.hScroll(value) }
        viewPort.offsetX().onChange { value = it?.value ?: 0.0 }
        visibleAmountProperty().bind(doubleBinding(viewPort.widthProperty(), viewPort.maxOffsetX()) {
            val newVisibleAmount = viewPort.maxOffsetX().get() * (viewPort.width / (viewPort.maxOffsetX().get() + viewPort.width))
            if (isNaN(newVisibleAmount)) viewPort.width
            else newVisibleAmount
        })
        visibleWhen(booleanBinding(viewPort.maxOffsetX()) {
            viewPort.maxOffsetX().get() > 0.0
        })
    }

    private val maxOffsetYProperty = doubleBinding(grid.minHeightProperty(), viewPort.heightProperty(), ruler.heightProperty(), hbar.heightProperty()) {
        (grid.minHeight - (viewPort.height - ruler.height - hbar.height)).coerceAtLeast(0.0)
    }

    private val shouldVBarBeVisible = booleanBinding(maxOffsetYProperty) {
        maxOffsetYProperty.get() > 0.0
    }
    init {
        vbar.visibleWhen(shouldVBarBeVisible)
        vbar.maxProperty().bind(maxOffsetYProperty)
        actions.maxOffsetY().bind(maxOffsetYProperty)
        vbar.visibleAmountProperty().bind(doubleBinding(viewPort.heightProperty(), ruler.heightProperty()) {
            (viewPort.height - ruler.height)
        })
    }

    init {
        children.add(backgroundLines)
        children.add(grid)
        children.add(ruler)
        children.add(vbar)
        children.add(hbar)
    }

    init {
        backgroundLines.dynamicContent(ruler.labels()) {
            ruler.labels().get().forEach { label ->
                line {
                    addClass(backgroundLine)
                    startXProperty().bind(label.layoutXProperty())
                    endXProperty().bind(label.layoutXProperty())
                }
                if (viewPort.scale.unitInPixels >= 12.0) {
                    val count = ruler.labelStep().value.value.toInt()
                    repeat(count) {
                        line {
                            addClass(backgroundLine, unlabeled)
                            startXProperty().bind(label.layoutXProperty().plus(viewPort.scale(UnitOfTime(it.toLong())).value))
                            endXProperty().bind(startXProperty())
                        }
                    }
                }
            }
        }
    }

    override fun computeMinHeight(
        width: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return ruler.minHeight(width) + grid.minHeight(width) + 8
    }

    override fun computeMinWidth(
        height: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return grid.minHeight(height)
    }

    override fun computePrefHeight(
        width: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return ruler.prefHeight(width) + grid.prefHeight(width) + 8
    }

    override fun computePrefWidth(
        height: Double,
        topInset: Double,
        rightInset: Double,
        bottomInset: Double,
        leftInset: Double
    ): Double {
        return grid.prefWidth(height)
    }

    override fun layoutChildren(contentX: Double, contentY: Double, contentWidth: Double, contentHeight: Double) {
        val rulerHeight = ruler.prefHeight(contentWidth).coerceAtLeast(24.0)

        layoutInArea(
            backgroundLines,
            contentX + 8,
            contentY,
            contentWidth - 8,
            contentHeight,
            0.0,
            Insets.EMPTY,
            true,
            true,
            HPos.LEFT,
            VPos.TOP
        )
        backgroundLines.children.filterIsInstance<Line>().forEach {
            it.startY = rulerHeight - RULER_PADDING
            it.endY = contentHeight
        }
        layoutInArea(
            grid,
            contentX + 8,
            contentY + rulerHeight + 8,
            contentWidth - 8,
            contentHeight - rulerHeight - 8,
            0.0,
            Insets.EMPTY,
            true,
            true,
            HPos.LEFT,
            VPos.TOP
        )
        layoutInArea(
            ruler,
            contentX + 8,
            contentY,
            contentWidth - 8,
            rulerHeight,
            0.0,
            Insets.EMPTY,
            true,
            true,
            HPos.LEFT,
            VPos.TOP
        )

        val hbarHeight = hbar.prefHeight(contentWidth)
        val vbarHeight = contentHeight - rulerHeight - if (hbar.isVisible) hbarHeight else 0.0
        val vbarWidth = vbar.prefWidth(vbarHeight)
        val hbarWidth = contentWidth - if (vbar.isVisible) vbarWidth else 0.0

        if (vbar.isVisible) {
            layoutInArea(
                vbar,
                contentWidth - vbarWidth,
                rulerHeight,
                vbarWidth,
                vbarHeight,
                0.0,
                Insets.EMPTY,
                true,
                true,
                HPos.RIGHT,
                VPos.TOP
            )
        }

        if (hbar.isVisible) {
            layoutInArea(
                hbar,
                contentX,
                contentHeight - hbarHeight,
                hbarWidth,
                hbarHeight,
                0.0,
                Insets.EMPTY,
                true,
                true,
                HPos.LEFT,
                VPos.TOP
            )
        }
    }

}