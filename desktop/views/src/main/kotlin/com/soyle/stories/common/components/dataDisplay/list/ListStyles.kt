package com.soyle.stories.common.components.dataDisplay.list

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.components.ComponentsStyles.Companion.firstChild
import com.soyle.stories.common.components.ComponentsStyles.Companion.notFirstChild
import com.soyle.stories.common.components.surfaces.Elevation
import com.soyle.stories.common.components.surfaces.SurfaceStyles
import javafx.application.Platform
import javafx.scene.control.ListCell
import javafx.scene.control.ListView
import javafx.scene.effect.Effect
import javafx.scene.paint.Color
import tornadofx.*
import tornadofx.Stylesheet.Companion.box

class ListStyles : Stylesheet() {
    companion object {

        val noCellShading by cssclass()

        fun CssSelectionBlock.removeListViewBorder() {
            backgroundInsets = multi(box(0.px))
        }

        fun ListCell<*>.applyFirstChildPseudoClasses() {
            toggleClass(firstChild, item == listView.items.firstOrNull())
            toggleClass(notFirstChild, item != listView.items.firstOrNull())
        }

        init {
            if (Platform.isFxApplicationThread()) importStylesheet<ListStyles>()
            else runLater { importStylesheet<ListStyles>() }
        }

    }

    init {
        noCellShading {
            listCell {
                backgroundColor = multi(Color.TRANSPARENT)
                and(hover) {
                    backgroundColor = multi(ColorStyles.primary.light)
                    effect = SurfaceStyles.dropShadow(2.0)
                    and(empty) {
                        backgroundColor = multi(Color.TRANSPARENT)
                        effect = SurfaceStyles.dropShadow(0.0)
                    }
                    and(selected) {
                        backgroundColor = multi(ColorStyles.primary.state.selectedBackground)
                        and(empty) {
                            backgroundColor = multi(Color.TRANSPARENT)
                            effect = SurfaceStyles.dropShadow(0.0)
                        }
                    }
                }
                and(selected) {
                    backgroundColor = multi(ColorStyles.primary.state.selectedBackground)
                    textFill = ColorStyles.lightSelectionTextColor
                }
            }
        }
    }
}