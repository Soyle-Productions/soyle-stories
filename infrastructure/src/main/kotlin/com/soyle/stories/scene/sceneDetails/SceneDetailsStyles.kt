package com.soyle.stories.scene.sceneDetails

import com.soyle.stories.soylestories.Styles
import javafx.scene.paint.Color
import tornadofx.*

class SceneDetailsStyles : Stylesheet() {
    companion object {
        val noSelection by cssclass()

        val arcSelectionCounter by cssclass()
        val noneSelected by cssclass()
        val someSelected by cssclass()
        val allSelected by cssclass()

        val arcSectionItem by cssclass()

        init {
            importStylesheet(SceneDetailsStyles::class)
        }
    }

    init {
        noSelection {
            focused {
                unsafe("-fx-accent", "transparent")
                unsafe("-fx-selection-bar", "transparent")

                label {
                    unsafe("-fx-text-fill", "-fx-text-base-color")
                }
            }
        }

        arcSelectionCounter {
            borderWidth += box(0.px, 0.px, 2.px, 0.px)
            padding = box(0.px, 0.px, (-2).px, 0.px)
            and(noneSelected) {
                borderColor += box(Color.GREY)
            }
            and(someSelected) {
                borderColor += box(Styles.Purple)
            }
            and(allSelected) {
                borderColor += box(Styles.Blue)
            }
        }

        menuItem {
            and(hover) {
                arcSelectionCounter {
                    textFill = Color.WHITE
                }
            }
            and(focused) {
                arcSelectionCounter {
                    textFill = Color.WHITE
                }
            }
        }

        arcSectionItem {
            checkBox {
                textFill = Color.BLACK
            }
            and(focused) {
                checkBox {
                    textFill = Color.WHITE
                }
            }
        }
    }
}