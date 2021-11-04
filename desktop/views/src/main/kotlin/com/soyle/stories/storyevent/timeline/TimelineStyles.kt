package com.soyle.stories.storyevent.timeline

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.components.ComponentsStyles.Companion.loading
import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipColor
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipVariant
import com.soyle.stories.common.components.surfaces.SurfaceStyles
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import tornadofx.*

class TimelineStyles : Stylesheet() {

    companion object {

        val timeline by cssclass()

        val timelineHeaderArea by cssclass()

        val ruler by cssclass()
        val timeLabel by cssclass()
        val largeMagnitudeLabelHolder by cssclass()
        val incrementalLabelHolder by cssclass()

        val storyPointLabel by cssclass()

        val backgroundLine by cssclass()
        val unlabeled by csspseudoclass()

        const val RULER_PADDING = 8.0
        const val rulerSpacing = 8.0
        const val rulerTimeLabelPadding = 4.0

        const val GRID_LABEL_PADDING = 4.0
        const val ROW_HEIGHT = 1 + GRID_LABEL_PADDING + 24 + GRID_LABEL_PADDING + 1

        const val COLLAPSED_WIDTH = 1 + GRID_LABEL_PADDING + 24 + GRID_LABEL_PADDING + 1

        init {
            if (Platform.isFxApplicationThread()) importStylesheet<TimelineStyles>()
            else runLater { importStylesheet<TimelineStyles>() }
        }

    }

    init {
        timeline {
            and(loading) {
                alignment = Pos.CENTER
            }
            timelineHeaderArea {
                padding = box(8.px)
                alignment = Pos.CENTER_LEFT
                spacing = 8.px
            }
            viewport {
                backgroundLine {
                    stroke = SurfaceStyles.lightBackground(1.0)
                    and(unlabeled) {
                        strokeWidth = 1.px
                    }
                }
                scrollBar {
                    backgroundColor = multi(Color.TRANSPARENT)
                    track {
                        backgroundColor = multi(Color.TRANSPARENT)
                        borderColor = multi(box(Color.TRANSPARENT))
                        visibility = FXVisibility.HIDDEN
                        opacity = 0.0
                    }
                    thumb {
                        backgroundInsets = multi(box(0.px))
                        opacity = 0.65
                        backgroundColor = multi(ColorStyles.lightTextColor)
                    }
                    select(incrementButton, decrementButton) {
                        backgroundColor = multi(Color.TRANSPARENT)
                        select(incrementArrow, decrementArrow) {
                            shape = " "
                        }
                    }
                    and(vertical) {
                        select(incrementButton, decrementButton) {
                            padding = box(0.px, 8.px, 0.px, 0.px)
                            select(incrementArrow, decrementArrow) {
                                padding = box(0.px, 0.1.em)
                            }
                        }
                    }
                    and(horizontal) {
                        select(incrementButton, decrementButton) {
                            padding = box(0.px, 0.px, 8.px, 0.px)
                            select(incrementArrow, decrementArrow) {
                                padding = box(0.1.em, 0.px)
                            }
                        }
                    }
                }
            }
            select(SurfaceStyles.relativeElevation[0], SurfaceStyles.relativeElevation[1]) {
                and(ruler) {
                    borderColor = multi(box(Color.TRANSPARENT))
                    borderWidth = multi(box(0.px))
                }
            }
            ruler {
                padding = box(RULER_PADDING.px, 0.px, RULER_PADDING.px, 0.px)
                spacing = RULER_PADDING.px
                largeMagnitudeLabelHolder {
                    backgroundColor = multi(SurfaceStyles.lightBackground(5.0))
                    minHeight = 24.px
                    spacing = rulerSpacing.px
                }
                incrementalLabelHolder {
                    backgroundColor = multi(SurfaceStyles.lightBackground(5.0))
                    //padding = box(0.px, RULER_PADDING.px, 0.px, RULER_PADDING.px)
                    spacing = rulerSpacing.px
                }
                timeLabel {
                    padding = box(0.px, rulerTimeLabelPadding.px)
                    and(hover) {
                        backgroundColor += ColorStyles.lightHighlightColor
                    }
                    and(selected) {
                        borderColor = multi(box(ColorStyles.primaryColor))
                        borderWidth = multi(box(2.px))
                        borderInsets = multi(box((-2).px))
                    }
                }
            }
        }
//        storyPointLabel {
//            borderRadius = multi(box(16.px))
//            backgroundRadius = multi(box(16.px))
//
//            and(hover) {
//                borderColor = multi(box(ColorStyles.primaryColor))
//                borderWidth = multi(box(1.px))
//                borderInsets = multi(box((-1).px))
//            }
//
//            and(selected) {
//                borderColor = multi(box(ColorStyles.primaryColor))
//                borderWidth = multi(box(2.px))
//                and(hover) {
//                    borderInsets = multi(box(0.px))
//                }
//            }
//        }
    }

}