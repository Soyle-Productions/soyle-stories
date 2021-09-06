package com.soyle.stories.storyevent.list

import com.soyle.stories.common.ColorStyles
import com.soyle.stories.common.components.surfaces.SurfaceStyles
import javafx.application.Platform
import javafx.geometry.Pos
import javafx.scene.paint.Color
import tornadofx.*

class StoryEventListStyles : Stylesheet() {
    companion object {

        val storyEventList by cssid()
        val name by cssclass()
        val time by cssclass()
        val equalTime by csspseudoclass()
        val storyEventItem by cssclass()

        init {
            if (Platform.isFxApplicationThread()) importStylesheet<StoryEventListStyles>()
            else runLater { importStylesheet<StoryEventListStyles>() }
        }

    }

    init {
        storyEventList {
            fillWidth = true
            and(empty) {
                spacing = 16.px
                alignment = Pos.CENTER
            }

            listView {
                padding = box(8.px)
             //   backgroundColor = multi(SurfaceStyles.lightBackground(8.0))
            }
        }

        Stylesheet.listCell and storyEventItem {
            borderColor = multi(box(Color.LIGHTGRAY))
            borderWidth = multi(box(2.px, 0.px, 0.px, 0.px))
            and(equalTime) {
                borderColor = multi(box(Color.TRANSPARENT))
            }
            and(Stylesheet.empty) {
                borderWidth = multi(box(0.px))
            }
            and(Stylesheet.selected) {
                name {
                    textFill = ColorStyles.lightSelectionTextColor
                }
                time {
                    textFill = ColorStyles.lightSelectionTextColor
                }
            }
        }
    }
}