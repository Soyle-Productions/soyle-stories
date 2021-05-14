package com.soyle.stories.common.components.layouts

import javafx.beans.property.DoubleProperty
import javafx.css.CssMetaData
import javafx.css.Styleable
import javafx.css.StyleableDoubleProperty
import javafx.css.StyleableProperty
import javafx.css.converter.SizeConverter
import javafx.geometry.HPos
import javafx.geometry.Orientation
import javafx.geometry.VPos
import javafx.scene.Node
import javafx.scene.layout.Pane
import javafx.scene.layout.Region
import kotlin.math.floor

class WaterfallPane : Pane() {

    private var _tileWidth: Double = -1.0

    private val hgapProperty = lazy {
        object : StyleableDoubleProperty() {
            override fun getName(): String = "hgap"
            override fun getBean(): Any = this@WaterfallPane
            override fun getCssMetaData(): CssMetaData<WaterfallPane, Number> = StyleableProperties.HGAP
            override fun invalidated() {
                requestLayout()
            }
        }
    }

    fun hgapProperty(): DoubleProperty = hgapProperty.value
    var hgap: Double
        get() = hgapProperty().value
        set(value) = hgapProperty().set(value)

    private val vgapProperty = lazy {
        object : StyleableDoubleProperty() {
            override fun getName(): String = "vgap"
            override fun getBean(): Any = this@WaterfallPane
            override fun getCssMetaData(): CssMetaData<WaterfallPane, Number> = StyleableProperties.VGAP
            override fun invalidated() {
                requestLayout()
            }
        }
    }

    fun vgapProperty(): DoubleProperty = vgapProperty.value
    var vgap: Double
        get() = vgapProperty().value
        set(value) = vgapProperty().set(value)

    override fun computeMinWidth(height: Double): Double {
        return insets.left + _tileWidth + insets.right
    }

    override fun layoutChildren() {
        val managedChildren = getManagedChildren<Node>()
        val top = snapSpaceY(insets.top)
        val left = snapSpaceX(insets.left)
        val bottom = snapSpaceY(insets.bottom)
        val right = snapSpaceX(insets.right)
        val vgap = snapSpaceY(vgap)
        val hgap = snapSpaceX(hgap)
        val insideWidth = width - left - right

        val averageWidth = managedChildren.asSequence().map { it.prefWidth(-1.0) }.average().coerceAtLeast(1.0)
        val columnCount = floor((insideWidth + hgap) / (averageWidth + hgap)).toInt().coerceAtLeast(1)
        if (managedChildren.size < columnCount) {
            val prefWidths = managedChildren.map { it.prefWidth(-1.0) }
            if (prefWidths.asSequence().map { it + hgap }.sum() <= (insideWidth + hgap)) {
                var offset = left
                managedChildren.forEachIndexed { index, node ->
                    val tileWidth = prefWidths[index]
                    layoutInArea(
                        node,
                        offset,
                        top,
                        tileWidth,
                        node.prefHeight(tileWidth),
                        baselineOffset,
                        null,
                        HPos.LEFT,
                        VPos.TOP
                    )
                    offset += tileWidth + hgap
                }
            }
            return
        }
        val uniformTileWidth = (insideWidth + hgap) / columnCount

        val baselineOffset = baselineOffset

        val columnHeights = List(columnCount) { IndexedValue(it, top) }.toMutableList()
        managedChildren.forEachIndexed { index, node ->
            val columnIndex = columnHeights.minByOrNull { it.value }!!.index
            val tileX = left + (columnIndex * uniformTileWidth)
            val tileY = columnHeights[columnIndex].value
            val tileWidth = uniformTileWidth - hgap
            val tileHeight = node.prefHeight(tileWidth)
            layoutInArea(node, tileX, tileY, tileWidth, tileHeight, baselineOffset, null, HPos.LEFT, VPos.TOP)
            columnHeights[columnIndex] = IndexedValue(columnIndex, tileY + tileHeight + vgap)
        }
    }

    companion object {

        fun getClassCssMetaDate(): List<CssMetaData<out Styleable, *>> = StyleableProperties.STYLEABLES
    }

    override fun getCssMetaData(): MutableList<CssMetaData<out Styleable, *>> = getClassCssMetaData()

    private class StyleableProperties {
        companion object {

            val HGAP: CssMetaData<WaterfallPane, Number> =
                object : CssMetaData<WaterfallPane, Number>("-fx-hgap", SizeConverter.getInstance(), 0.0) {
                    override fun isSettable(styleable: WaterfallPane?): Boolean {
                        return !(styleable!!.hgapProperty.isInitialized() || styleable.hgapProperty.value.isBound)
                    }

                    override fun getStyleableProperty(styleable: WaterfallPane?): StyleableProperty<Number> {
                        return styleable!!.hgapProperty.value
                    }
                }
            val VGAP: CssMetaData<WaterfallPane, Number> =
                object : CssMetaData<WaterfallPane, Number>("-fx-vgap", SizeConverter.getInstance(), 0.0) {
                    override fun isSettable(styleable: WaterfallPane?): Boolean {
                        return !(styleable!!.vgapProperty.isInitialized() || styleable.vgapProperty.value.isBound)
                    }

                    override fun getStyleableProperty(styleable: WaterfallPane?): StyleableProperty<Number> {
                        return styleable!!.vgapProperty.value
                    }
                }

            val STYLEABLES = Region.getClassCssMetaData() + listOf(HGAP, VGAP)
        }
    }

}