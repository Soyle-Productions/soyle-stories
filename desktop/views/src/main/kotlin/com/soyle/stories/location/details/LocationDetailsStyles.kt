package com.soyle.stories.location.details

import com.soyle.stories.common.components.ComponentsStyles.Companion.loaded
import com.soyle.stories.common.components.ComponentsStyles.Companion.loading
import com.soyle.stories.common.components.dataDisplay.chip.Chip
import com.soyle.stories.common.components.dataDisplay.chip.Chip.Styles.Companion.chipVariant
import com.soyle.stories.common.components.text.TextStyles.Companion.fieldLabel
import com.soyle.stories.common.components.text.TextStyles.Companion.sectionTitle
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

        val smallWidth by csspseudoclass()
        val hasScenes by csspseudoclass()

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
                spacing = 16.px

                and(loading) {
                    alignment = Pos.CENTER
                }
                and(loaded) {
                    alignment = Pos.TOP_LEFT
                }
            }
        }

        description {
            fillWidth = true

            textArea {
                wrapText = true
                prefRowCount = 5
            }
        }

        smallWidth {
            description {
                textArea {
                    prefRowCount = 10
                }
            }
        }

        hostedScenesSection {
            fillWidth = true

            header {
                alignment = Pos.CENTER_LEFT

                sectionTitle {

                    maxWidth = Double.MAX_VALUE.px
                }
            }

            invitation {
                alignment = Pos.CENTER
                spacing = 16.px
                fieldLabel {
                    textAlignment = TextAlignment.CENTER
                    wrapText = true
                    padding = box(16.px)
                }
            }

            itemList {
                fillWidth = false
                padding = box(8.px)
                hgap = 8.px
                vgap = 8.px

                hostedSceneItem {
                    chipVariant = Chip.Variant.outlined
                }
            }

        }

    }
}