package com.soyle.stories.location.locationDetails

import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipColor
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipVariant
import com.soyle.stories.common.components.text.TextStyles.Companion.fieldLabel
import javafx.geometry.Pos
import javafx.scene.control.ScrollPane
import javafx.scene.text.TextAlignment
import tornadofx.*

class LocationDetailsStyles : Stylesheet() {
    companion object {
        val locationDetails by cssclass()
        val description by cssclass()
        val hostedScenesSection by cssclass()
        val invitation by cssclass()
        val itemList by cssclass()
        val hostedSceneItem by cssclass()

        val addScene by cssid()

        init {
            importStylesheet<LocationDetailsStyles>()
        }
    }

    init {
        locationDetails {
            fitToWidth = true
            fitToHeight = true
            vBarPolicy = ScrollPane.ScrollBarPolicy.AS_NEEDED
            hBarPolicy = ScrollPane.ScrollBarPolicy.NEVER

            content {
                fillWidth = true
                padding = box(16.px)
            }
        }

        description {
            fillWidth = true

            textField {
                wrapText = true
            }
        }

        hostedScenesSection {
            fillWidth = true

            header {
                alignment = Pos.CENTER_LEFT
            }

            invitation {
                alignment = Pos.CENTER
                spacing = 16.px
                fieldLabel {
                    textAlignment = TextAlignment.CENTER
                    wrapText = true
                }
            }

            itemList {
                fillWidth = false
                padding = box(8.px)
                hostedSceneItem {
                    chipVariant = Chip.Variant.outlined
                }
            }

        }

    }
}