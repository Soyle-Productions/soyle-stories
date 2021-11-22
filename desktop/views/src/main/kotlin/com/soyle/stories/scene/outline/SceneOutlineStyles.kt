package com.soyle.stories.scene.outline

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.StyleImporter
import com.soyle.stories.common.components.ComponentsStyles.Companion.card
import com.soyle.stories.common.components.ComponentsStyles.Companion.failed
import com.soyle.stories.common.components.ComponentsStyles.Companion.loading
import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chip
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipColor
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipVariant
import com.soyle.stories.common.components.surfaces.SurfaceStyles
import com.soyle.stories.common.components.text.TextStyles.Companion.fieldLabel
import com.soyle.stories.common.components.text.TextStyles.Companion.sectionTitle
import javafx.geometry.Pos
import javafx.scene.control.ContentDisplay
import javafx.scene.paint.Color
import javafx.scene.text.TextAlignment
import tornadofx.*

class SceneOutlineStyles : Stylesheet() {
    companion object : StyleImporter<SceneOutlineStyles>(SceneOutlineStyles::class) {
        val sceneOutline by cssclass()

        val untargeted by csspseudoclass()
    }

    init {

        val unpopulatedMessage = mixin {
            padding = box(16.px)
            alignment = Pos.CENTER
            spacing = (1.142857).em // 16px @ 14px root

            sectionTitle {
                wrapText = true
                textAlignment = TextAlignment.CENTER
            }
        }

        sceneOutline {
            alignment = Pos.TOP_LEFT
            fillWidth = true
            fillHeight = true
            and(loading, failed, untargeted) {
                + unpopulatedMessage
            }

            headerPanel {
                padding = box(8.px)

                sectionTitle {
                    contentDisplay = ContentDisplay.RIGHT

                    chip {
                        fontFamily = "Segoe UI"
                        fontSize = 12.px

                        maxHeight = 24.px
                        maxWidth = 24.px
                        padding = box(0.px)
                        chipColor = Chip.Color.secondary
                        label {
                            padding = box(2.px, 8.px)
                        }
                    }
                }
            }

            content {
                alignment = Pos.TOP_LEFT
                and(empty) {
                    + unpopulatedMessage
                }

                listView {
                    listCell {
                        and(hover) {
                            // textFill = Color.BLACK
                            backgroundColor = multi(Color.TRANSPARENT)
                            effect = SurfaceStyles.dropShadow(0.0)
                        }
                        and(selected) {
                            //textFill = ColorStyles.lightSelectionTextColor
                            backgroundColor = multi(Color.TRANSPARENT)
                            card {
                                fieldLabel {
                                    textFill = ColorStyles.lightSelectionTextColor
                                }
                                backgroundColor = multi(ColorStyles.lightSelectionColor)
                            }
                        }
                    }

                    card and hover {
                        fieldLabel {
                            textFill = Color.BLACK
                        }
                        backgroundColor = multi(ColorStyles.lightHighlightColor)
                    }
                }
            }
        }
    }
}