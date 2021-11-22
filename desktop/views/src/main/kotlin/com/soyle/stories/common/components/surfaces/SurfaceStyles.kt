package com.soyle.stories.common.components.surfaces

import com.soyle.stories.common.ColorStyles
import javafx.scene.effect.DropShadow
import javafx.scene.paint.Color
import tornadofx.*
import kotlin.math.pow
import kotlin.math.round

class SurfaceStyles : Stylesheet() {
    companion object {

        val surface by cssclass()


        /**
         * valid indices: 0-24
         *
         * example: ```elevation[0];  elevation[24]```
         */
        val elevated = List(25) { CssRule.c("elevation${it}") }
        val relativeElevation = List(25) { CssRule.pc("relativeElevation${it}") }

        val dropShadowColor = Color.web("black", 0.20)
        fun dropShadow(elevation: Double) = DropShadow(elevation, 0.0, elevation, dropShadowColor)

        fun red(elevation: Double) = round(205.0 * (elevation.pow(0.0679)))
        fun green(elevation: Double) = round(199.0 * (elevation.pow(0.0756)))
        fun blue(elevation: Double) = round(198.0 * (elevation.pow(0.0779)))

        fun lightBackground(elevation: Double) = Color.rgb(red(elevation).toInt(), green(elevation).toInt(), blue(elevation).toInt())

        init {
            importStylesheet<SurfaceStyles>()
        }
    }

    init {
        elevated.withIndex().forEach { (index, elevationStyle) ->
            if (index == 0) return@forEach
            elevationStyle {
                val calculatedBackgroundColor = lightBackground(index.toDouble() + 1)
                backgroundColor = multi(calculatedBackgroundColor)

                select(treeCell, listCell) {
                    backgroundColor = multi(Color.TRANSPARENT)
                    and(hover) {
                        textFill = Color.BLACK
                        backgroundColor = multi(ColorStyles.lightHighlightColor)
                        effect = dropShadow(2.0)
                        and(empty) {
                            backgroundColor = multi(Color.TRANSPARENT)
                            //unsafe("-fx-effect", raw("none"))
                        }
                    }
                    and(selected) {
                        textFill = ColorStyles.lightSelectionTextColor
                        treeDisclosureNode {
                            arrow {
                                backgroundColor = multi(ColorStyles.lightSelectionTextColor)
                            }
                        }
                        backgroundColor = multi(ColorStyles.lightSelectionColor)
                    }
                }
            }
        }
        elevated[0] {
            backgroundColor = multi(Color.TRANSPARENT)
        }
        relativeElevation.withIndex().forEach { (index, relativeElevation) ->
            relativeElevation {
                if (index == 0) {
                    borderColor = multi(box(Color.rgb(0, 0, 0, 0.12)))
                    borderWidth = multi(box(1.px))
                } else {
                    effect = dropShadow(index.toDouble())
                }
            }
        }
    }

}